(ns clojure-noob.cb-core-functions-depth)

;; The collection of abstraction
;; The sequence abstraction is about operating on members individually,
;; whereas the collection abstraction is about the data structure as a whole.

;; For example, the collection functions
;; count, empty?, and every? aren’t about any individual element; they’re about the whole:

(empty? [])
(empty? ["no!"])

(map identity {:sunlight-reaction "Glitter!"})
; => ([:sunlight-reaction "Glitter!"])

(into {} (map identity {:sunlight-reaction "Glitter!"}))
; => {:sunlight-reaction "Glitter!"}

(conj [0] 1 2 3 4)
; => [0 1 2 3 4]

(conj {:time "midnight"} [:place "ye olde cemetarium"])
; => {:place "ye olde cemetarium" :time "midnight"}

;; Function Functions (accepts and return functions)
(apply max [0 1 2])


;; Partials
;; partial is a higher-order function in Clojure that allows you to partially apply a function.
;; It takes a function and some arguments, and returns a new function with those arguments "pre-filled".

(def add10 (partial + 10))
(add10 3)
; => 13
(add10 5)
; => 15

(def add-missing-elements
  (partial conj ["water" "earth" "air"]))

(add-missing-elements "unobtainium" "adamantium")
; => ["water" "earth" "air" "unobtainium" "adamantium"]


;; In the next function, we are creating a partial, (partial) receives a function, in this case "lousy-logger".
;; But with the prefilled value ':warn'
;; So lousy-logger returns a new function that has :warn pre-filled as the log-level argument.
;; This new function is bound to the name warn.

(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))

(warn "Red light ahead")
; => "red light ahead"

;; Complement
;; create the logical opposite of any predicate function
;; vampire? is presumably a function that returns true if a record represents a vampire, and false otherwise.
;; (complement vampire?) creates a new function that returns true when vampire? would return false, and vice versa.
;; This new function is bound to the name not-vampire?.

(def not-vampire? (complement vampire?))
(defn identify-humans
  [social-security-numbers]
  (filter not-vampire?
          (map vampire-related-details social-security-numbers)))


