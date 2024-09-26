(ns clojure-noob.e-domain-operations)

; https://github.com/nubank/tech-learning-clojure/blob/main/04-domain-operations/en/README.md
(def currencies {
                 :usd { :divisor 100 :code "USD" :sign "$"
                       :desc "US Dollars" }
                 :brl { :divisor 100 :code "BRL" :sign "R$"
                       :desc "Brazilian Real" }
                 :ukg { :divisor (* 17 29) :code "UKG" :sign "ʛ"
                       :desc "Galleons of the United Kingdom"}})


;; Building entities
(defn make-money
  "takes an amount and a currency, creating a Money entity"
  [amount currency] {:amount amount
                     :currency currency})


;; Default Values
(def default-currency (:brl currencies))

(defn make-money
  "takes an amount and a currency, creating a Money entity"
  ([]                   {:amount 0
                         :currency default-currency})
  ([amount]             {:amount amount
                         :currency default-currency})
  ([amount currency]    {:amount amount
                         :currency currency}))

;; Calculated Values
(defn show-galleons
  "creates a display string for Harry Potter money"
  [amount]
  (let [{:keys [divisor code sign desc]} (:ukg currencies)
        galleons      (int (/ amount divisor))
        less-galleons (rem amount divisor)
        sickles       (int (/ less-galleons 17))
        knuts         (rem less-galleons 29)]
    (str galleons " Galleons, " sickles " Sickles, and " knuts " Knuts")))

(defn show-money
  "creates a display string for a Money entity"
  [{:keys [amount currency]}]
  (let [{:keys [divisor code sign desc]} currency]
    (cond
      (= code "UKG")
      (show-galleons amount)
      :else
      (let [major (int (/ amount divisor))
            minor (mod amount divisor)]
        (str sign major "." minor)))))

(defn make-money
  "takes an amount and a currency, creating a Money entity"
  [amount currency]
  (let [money {:amount amount
               :currency currency}]
    (-> money
        (assoc :displayed (show-money money)))))

(make-money 525 (:ukg currencies))
(make-money 525 (:brl currencies))

;; About Side Effects
;; Sometimes, the initialization of an entity must include unavoidable side-effects, such as I/O or network access.
;; Including the side-effects in the constructor isolates them from the rest of the code.
;; In this code we are printing the transaction to the console, which is a side-effect. But it is isolated in the audit-transaction function.
(defn audit-transaction
  "method that creates an audit record for a transaction"
  [transaction]
  ; printing is a side-effect
  (println (str "auditing: " transaction))
  transaction)

(defn make-transaction
  "creates a Transaction and generates an audit entry"
  [trx-type account-id amount & details]
  (let [timestamp (quot (System/currentTimeMillis) 1000)
        transaction { :transaction-type :debit
                     :account-id       account-id
                     :details          details
                     :timestamp        timestamp
                     :amount           amount}]
    (audit-transaction transaction)))


;; Domain Operations
(defn- same-currency?
  "true if the Currencies of the Money entities are the same"
  ([m1] true)
  ([m1 m2]
   (= (:currency m1) (:currency m2)))
  ([m1 m2 & monies]
   (every? true? (map #(same-currency? m1 %) (conj monies m2)))))

(defn- same-amount?
  "true if the amount of the Money entities are the same"
  ([m1] true)
  ([m1 m2] (zero? (.compareTo (:amount m1) (:amount m2))))
  ([m1 m2 & monies]
   (every? true? (map #(same-amount? m1 %) (conj monies m2)))))

(defn- ensure-same-currency!
  "throws an exception if the Currencies do not match, true otherwise"
  ([m1] true)
  ([m1 m2]
   (or (same-currency? m1 m2)
       (throw
         (ex-info "Currencies do not match."
                  {:m1 m1 :m2 m2}))))
  ([m1 m2 & monies]
   (every? true? (map #(ensure-same-currency! m1 %) (conj monies m2)))))

(defn =$
  "true if Money entities are equal"
  ([m1] true)
  ([m1 m2]
   (and (same-currency? m1 m2)
        (same-amount? m1 m2)))
  ([m1 m2 & monies]
   (every? true? (map #(=$ m1 %) (conj monies m2)))))

(defn +$
  "creates a Money object equal to the sum of the Money arguments"
  ([m1] m1)
  ([m1 m2]
   (ensure-same-currency! m1 m2)
   (make-money (+ (:amount m1) (:amount m2)) (:currency m1)))
  ([m1 m2 & monies]
   (apply ensure-same-currency! m1 m2 monies)
   (let [amounts (map :amount (conj monies m1 m2))
         new-amount (reduce + amounts)]
     (make-money new-amount (:currency m1)))))


;; Specs
(s/def :money/amount int?)
(s/def :currency/divisor int?)
(s/def :currency/sign string?)
(s/def :currency/desc string?)


(s/def :currency/code (and string? #{"USD" "BRL" "UKG" ,,, }))
(s/def :currency/sign (s/nilable string?))
(s/def :currency/desc (s/nilable string?))


(s/def :finance/currency (s/keys :req-un [:currency/divisor
                                          :currency/code]
                                 :opt-un [:currency/sign
                                          :currency/desc]))

(s/valid? :finance/currency (:usd currencies))
(map #(s/valid? :finance/currency %) (vals currencies))



(s/def :finance/money (s/keys :req-un [:money/amount
                                       :finance/currency]))

(s/valid? :finance/money (make-money 1234 (:brl currencies)))
(s/explain :finance/money {:amount "a", :currency (:brl currencies)})
;"a" - failed: int? in: [:amount] at: [:amount] spec: :money/amount