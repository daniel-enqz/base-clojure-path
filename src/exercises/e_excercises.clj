(ns clojure-noob.e-excercises)

;; 1. Use the str, vector, list, hash-map, and hash-set functions.
(def pana "Daniel")
(str "Hello " pana)

(vector 1 2 3)
(list '1 2 3)
(hash-map :a 1 :b 2)
(hash-set 1 1 2 2)

;; 2. Write a funciton that takes a number and adds 100 to it
(defn add-100
  [number]
  (+ number 100))
(add-100 37)


;; 3. Write a function, dec-maker, that works exactly like the function inc-maker except with subtraction:
(defn dec-maker
  "Custom decrease"
  [number]
  #(- % number))

(def dec9 (dec-maker 9))
(dec9 10)

;; 4. Write a function, mapset, that works like map except the return value is a set:
(defn mapset
  "Applies map behavior but returning a set"
  [func operand]
  (set (map func operand)))

(mapset inc [1 1 2 2])
; => #{2 3}


;; Create a function that generalizes symmetrize-body-parts
;; and the function you created in Exercise 5.
;; The new function should take a collection of body parts and the number of matching body parts to add.
(def asym-alien-body-parts [{:name "head" :size 3}
                            {:name "left-eye" :size 1}
                            {:name "left-ear" :size 1}
                            {:name "mouth" :size 1}
                            {:name "nose" :size 1}
                            {:name "neck" :size 2}
                            {:name "left-shoulder" :size 3}
                            {:name "left-upper-arm" :size 3}
                            {:name "chest" :size 10}
                            {:name "back" :size 10}
                            {:name "left-forearm" :size 3}
                            {:name "abdomen" :size 6}
                            {:name "left-kidney" :size 1}
                            {:name "left-hand" :size 2}
                            {:name "left-knee" :size 2}
                            {:name "left-thigh" :size 4}
                            {:name "left-lower-leg" :size 3}
                            {:name "left-achilles" :size 1}
                            {:name "left-foot" :size 2}])

(defn dynamic-matching-parts
  [part number]
  (map (fn [i]
         {:name (-> (:name part)
                    (clojure.string/replace #"^left-" "")
                    (str "-" i))
          :size (:size part)})
       (range 1 (inc number))))

(defn symmetrize-alien-body-parts-dynamic
  "Expects a seq of maps that have a :name and :size and an integer for the asymmetric parts to create"
  [asym-body-parts number]
  (reduce (fn [final-body-parts part]
            (if (and (clojure.string/starts-with? (:name part) "left-") (> number 0))
              (into final-body-parts (dynamic-matching-parts part number))
              (conj final-body-parts part)))
          []
          asym-body-parts))

(symmetrize-alien-body-parts-dynamic asym-alien-body-parts 2)