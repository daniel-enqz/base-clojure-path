(ns exercises.transactions-pipeline)


; When doing combinations:
; Figure out what question you’re trying to ask. This step is often the most difficult.
; Filter the data to remove unneeded elements.
; Transform the elements into the desired form.
; Reduce the transformed elements to the answer.

(def all-transactions [,,,]) ; every transaction ever!

(def currencies {
                 :usd { :divisor 100 :code "USD" :sign "$"
                       :desc "US Dollars" }
                 :brl { :divisor 100 :code "BRL" :sign "R$"
                       :desc "Brazilian Real" }})

(def default-currency (:brl currencies))

(defn make-money
  "takes an amount and a currency, creating a Money entity"
  ([]                {:amount 0
                      :currency default-currency})
  ([amount]          {:amount amount
                      :currency default-currency})
  ([amount currency] {:amount amount
                      :currency currency}))

(defn transactions-for-account-id
  "given an account-id and a seq of transactions,
   return all transactions belonging to that account id"
  [account-id txs]
  (filter #(= account-id (:account-id %)) txs))

(defn filter-by-status
  "given a status and a list of transactions,
   return all transactions matching that status"
  [status txs]
  (filter #(= status (:status %)) txs))

(defn filter-by-type
  "given a tx-type and a list of transactions, return all transactions
   whose :transaction-type matches the tx-type."
  [tx-type txs]
  (filter #(= tx-type (:transaction-type %)) txs))

; 1. what is the total of all deposits made into an account?
(defn total-settled-debits
  "given an account-id, returns a Money entity whose :amount
   is the sum of all Money deposited into the account. includes
   :pending transactions"
  [account-id]
  (->> all-transactions
       (transactions-for-account-id account-id)
       (filter-by-type :debit)
       (map :amount) ; extract the :amount field
       (reduce +$))) ; add amounts together

; 2. what is the available balance? the trick here is that for a negative
;    balance, the available balance is zero.
(defn available-balance
  "get all available (settled) balance"
  [account-id]
  (let [txs (transactions-for-account-id account-id all-transactions)
        settled-txs (filter-by-status :settled txs)
        debits (filter-by-type :debit settled-txs)
        credits (filter-by-type :credit settled-txs)
        debit-amts (map :amount debits)
        credit-amts (map :amount credits)
        neg-amts (map #(assoc % :amount (- (:amount %))) credit-amts)
        amounts (concat debit-amts neg-amts)
        total-monies (reduce +$ amounts)]

    (cond
      (<= 0 (:amount total-monies)) total-monies
      :else (make-money 0 default-currency))))


; 3. what is the total of all pending outgoing transactions?
(defn pending-credits
  "given a sequence of transactions, returns a money entity
   representing all unsettled (pending) credit transactions"
  [txs]

  (->> txs
       (filter-by-type :credit)
       (filter-by-status :pending)
       (map :amount)
       (reduce +$)))