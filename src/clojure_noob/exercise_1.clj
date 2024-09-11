(ns clojure-noob.exercise-1)


;; First things first
;; What is let?
(def y 1)                                                   ;; Bind 1 to x in a global context
(let [z 4] z)                                               ;; Bind the value of 1 to x in this scope
(def x 0)
(let [x (inc x)] x)                                         ;; Here we are incrementing x by one,
(println x)                                                 ;; So in the scope of let it will be 1 but in println it will still be 0

;; The vector [pongo dalmatians] is the last expression in let, so it’s the value of the let form.
(def dalmatian-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"])
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])

;; Usages of let
; First, they provide clarity by allowing you to name things.
; Second, they allow you to evaluate an expression only once and reuse the result. (Like API Call)

;; LOOP
(loop [iteration 0]
  (if (> iteration 3)
    (do (println (str "Sorry User No. " iteration " not registered")))
    (do (println (str "Welcome Back User No. " iteration))
        (recur (inc iteration)))))

;; Understanding Regexp
(re-find #"^left-" "left-eye")


(def asym-hobbit-body-parts [{:name "head" :size 3}
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

(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})
(matching-part {:name "left-eye" :size 3})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))

(defn better-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts
                  (set [part (matching-part part)])))
          []
          asym-body-parts))

(defn hit
  [asym-body-parts]
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    (loop [[part & remaining] sym-parts
            accumulated-size (:size part)]
       (if (> accumulated-size target)
         part
         (recur remaining (+ accumulated-size (:size (first remaining))))))))

(hit asym-hobbit-body-parts)
