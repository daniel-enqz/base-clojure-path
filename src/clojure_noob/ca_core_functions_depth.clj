(ns clojure-noob.ca-core-functions-depth)


;; Programming to abstractions in Clojure
;; Means writing functions that work with general concepts (like sequences)
;; rather than specific data structures.
;; This allows functions to work with multiple data types seamlessly.

;Example:
;The map function in Clojure works on lists, vectors, sets, and maps because it's built on the sequence abstraction,
;which only requires three operations: first, rest, and cons.
;All these data structures can be treated as sequences, allowing map to work consistently across them.

(map inc [1 2 3])     ; Works on a vector
(map inc '(1 2 3))    ; Works on a list
(map inc #{1 2 3})    ; Works on a set
(map inc {:a 1 :b 2}) ; Works on a map,
;So a map converts what we give it to a seq, therefore, is designed to work with any data structure that can be viewed as a sequence

;; MAP
(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(stats [3 4 10])
; => (17 3 17/3)

(stats [80 1 44 13 6])
; => (144 5 144/5)

;; We can also use keywords as functions
(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities)


;; REDUCE
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})

;; MAP
(map inc [1 2 3 4])


(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

;; TAKE, DROP
(take 3 [1 2 3 4 5 6 7 8 9 10])
(drop 3 [1 2 3 4 5 6 7 8 9 10])

;; FILTER, SOME
(filter #(< (:human %) 5) food-journal)
(some #(> (:critter %) 3) food-journal)

;; SORT, SORT BY
(sort [3 1 2])
(sort-by count ["aaa" "c" "bb"])

;; CONCAT
(concat [1 2] [3 4])

;; LAZY SEQS
(def lazy-seq (map inc (range 10000)))                      ;; It will not increment any of those until consulted

;; Vampire Example
(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true  :name "McFishwich"}
   1 {:makes-blood-puns? false, :has-pulse? true  :name "McMackson"}
   2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:makes-blood-puns? true,  :has-pulse? true  :name "Mickey Mouse"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

;; This next line will take barley a second because it's just a recipe of what to call until applied
(time (def mapped-details (map vampire-related-details (range 0 1000000))))

;; The time will be 32 seconds because Clojure chunks some other elements as well
;; It applies the recipe multiple times. But not 1M times.
(time (first mapped-details))                               ;; If we access again, it will take no time.


(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))
(time (identify-vampire (range 0 1000000)))

(time (vampire-related-details 0))

;; Infinite Sequences
;; It will always ask for only the first 10 even-numbers from the infinite sequence.
(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers))
