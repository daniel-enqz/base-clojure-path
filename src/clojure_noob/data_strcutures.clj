(ns clojure-noob.data-strcutures)

;; Numbers
93
1/5
1.2

;; Strings
"hello"
(def person-name "Chewbacca")
(def age 12)
(str "Hello Im " age " years old, my name is " person-name)

;; Maps
{}

(hash-map :a 1 :b 2)
{"string-key" +}
(get {:a 0 :b 1} :b)

(def map-name { :first-name "Charlie" :last-name "McFishwich" :address {:country "Mexico" :postal-code 03700}})
(get map-name :first-name)
(get map-name :age)
(get map-name :age "nao")
(get map-name :address)
(get-in map-name [:address :country])
(contains? #{:a :b} :a)
(map-name :first-name)
(:first-name map-name)
(map-name :age "not-found-key-error")

;; Arrays/Vectors
(def my-array [3 2 1])
(my-array 0)
(def my-second-array (vector "hello" "pana" 12))
(my-second-array 1)
(conj my-array 4)

;; Lists (Iterate over all elements to get the nth element)
(def my-list '(1 2 3 4))
(nth my-list 1)
(nth (list 1 "two" {3 4}) 2)
(conj my-list 7)                                            ;; added at the beginning

;; Sets (Collections of unique values)
#{"kurt" 20 :circle}
(hash-set 1 1 2 2)                                          ;; #{ 1 2 }
(set [ 3 3 4 4 ])
(contains? #{:a :b} :a)
(get #{:a nil} nil)




