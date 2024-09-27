(ns exercises.g_threading-operations)

;; Threading macros, also known as arrow macros,
;; convert nested function calls into a linear flow of function calls, improving readability.

;; THREAD-FIRST MACRO
;; First way of implementing a function:
(defn hire
  [employee]
  (assoc (update (assoc employee :email (str (:first-name employee) "@nubank.com.br")) :employment-history conj "Nubank") :hired-at (java.util.Date.)))

;; Refactored:
(defn hire
  [employee]
  (let [email (str (:first-name employee) "@nubank.com.br")
        employee-with-email (assoc employee :email email)
        employee-with-history (update employee-with-email :employment-history conj "Nubank")]
    (assoc employee-with-history :hired-at (java.util.Date.))))

;; This previous refactoring can be further improved using threading macros:
;; Using the -> macro, you can more closely mirror your function's requirement specification,
;; remove the need to introduce intermediate bindings, and greatly increase the readability.

(defn hire
  [employee]
  (-> employee
      (assoc :email (str (:first-name employee) "@nubank.com.br"))
      (update :employment-history conj "Nubank")
      (assoc :hired-at (java.util.Date.))))

(hire {:first-name "Rich" :last-name "Hickey" :employment-history ["Cognitect"]})

;; THREAD-LAST MACRO
;; Current DB of hired employees
(def employees
  [{:first-name "A" :last-name "B" :email "A@nubank.com.br" :hired-at #inst "2022-05-20"}
   {:first-name "C" :last-name "D" :email "C@nubank.com.br" :hired-at #inst "2022-05-21"}
   {:first-name "E" :last-name "F" :email "E@nubank.com.br" :hired-at #inst "2022-05-21"}

   ;; Your fix was implemented and deployed here

   {:first-name "G" :last-name "H" :email "G.H@nubank.com.br" :hired-at #inst "2022-05-21"}
   {:first-name "I" :last-name "J" :email "I.J@nubank.com.br" :hired-at #inst "2022-05-22"}])

(defn email-address
  "Return email address containing first and last name."
  [employee]
  (format "%s.%s@nubank.com.br"
          (:first-name employee)
          (:last-name employee)))

(defn old-email-format?
  "Return true when employee email does not follow the new format."
  [employee]
  (not= (:email employee) (email-address employee)))

(defn hired-day
  "Return the day of hire from :hired-at."
  [employee]
  (.getDay (:hired-at employee)))

;; Nested way:
(defn report
  [employees]
  (vals (group-by hired-day (map #(assoc % :email (email-address %)) (filter old-email-format? employees)))))

;; Thread Last way:
(defn report
  [employees]
  (->> employees
       (filter old-email-format?)
       (map #(assoc % :email (email-address %)))
       (group-by hired-day)
       vals))

(report employees)


;; Other example usages:
;; nested
(update (assoc {} :foo "bar") :foo keyword) ;; => {:foo :bar}

;; thread first
(-> {}
    (assoc :foo "bar")
    (update :foo keyword)) ;; => {:foo :bar}

;It starts with an empty map {}.
;Then it applies (assoc _ :foo "bar"), where _ is replaced by {}.
;Finally, it applies (update _ :foo keyword), where _ is replaced by the result of the previous step.

;; nested
(apply + (filter even? (map inc (range 5)))) ;; => 6

;; thread last
(->> (range 5)
     (map inc)
     (filter even?)
     (apply +)) ;; => 6

;It starts with (range 5), which generates (0 1 2 3 4).
;Then it applies (map inc _), where _ is replaced by (range 5).
;Next, it applies (filter even? _) to the result of the previous step.
;Finally, it applies (apply + _) to the filtered result.


;Some Thread First some->
;Prevent operating on nil values unexpectedly.

(-> {:first-name "Rich" :last-name "Hickey"}
    :hired-at
    .getTime) ;; Execution error (NullPointerException)

(some-> {:first-name "Rich" :last-name "Hickey"}
        :hired-at
        .getTime) ;; => nil

(some-> {:first-name "Rich" :last-name "Hickey" :hired-at #inst "2020-07-23"}
        :hired-at
        .getTime) ;; => 1595462400000


;Conditional Thread First cond->
;Given an expression and set of predicate/form pairs, thread the expression into the first argument position when the test is truthy.

(defn describe-number
  [n]
  (cond-> []
          (odd? n)  (conj "odd")
          (even? n) (conj "even")
          (zero? n) (conj "zero")
          (pos? n)  (conj "positive")))

(describe-number 3) ;; => ["odd" "positive"]
(describe-number 4) ;; => ["even" "positive"]

;Thread As as->
;Create a binding var that will control the placement of the expressions. In this example you will use $ but any valid var is acceptable.

(as-> {:ints (range 5)} $
      (:ints $)
      (map inc $)  ;; last position
      (conj $ 10)  ;; first position
      (apply + $)) ;; => 25

; Because any valid Clojure expression can be used,
; you are free to introduce logic into the expression but be careful as this may reduce the readability.
(as-> 10 $
      (dec $)
      (if (even? $)
        (dec $)
        (inc $))
      (* 2 $)) ;; => 20