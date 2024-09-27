(ns clojure-noob.b-data-structures)

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
(get {:a 0 :b 1} :b)                                        ;; Each element in a map is called MapEntry

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

(zipmap [:mina :red :taco] ["cat" "squirrel" "chihuahua"])

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
(require ['clojure.set :refer :all])
(def sample-set #{:a :b :c 1 2 3})
(clojure.set/subset? #{:a :b} sample-set)                       ;; Check if all elements of the first set are in the second set
(clojure.set/superset? #{:a :b} sample-set)                     ;; Check if all elements of the second set are in the first set
;; Remember you can also use difference, intersection, and union


