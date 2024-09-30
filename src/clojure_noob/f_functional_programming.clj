(ns clojure-noob.f-functional-programming)


;; In functional programming aim for: function composition
;; In general, functional programming encourages you to build more complex functions by combining simpler functions.


;; About Pure Functions
;; Always return same output for same input
;; No side effects


;; Referential Transparency
;; Relay on their own arguments and immutable data
;; To be referential transparent, a function should not access any data that is not passed as arguments and should not perform I/O directly. 
;; When performing I/O the consulted state can change during the execution, changing the result of the function even if the same parameters are used.

(defn analysis
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  [filename]
  (analysis (slurp filename)))

;; The first function is being affected externally. Therefore, the output could change for the same input.
;; The second function is pure, it will always return the same output for the same input.

;; Have no side effects
;; When you call a function that doesn’t have side effects,
;; you only have to consider the relationship between the input and the output.
;; You don’t have to worry about other changes that could be rippling through your system.

;; Note: Altough not consider an exception, avoid using Exception to inform errors

;; Immutable data structures
;; Clojure has a lot of immutable data structures,
;; For instance, there's no assignment, we cannot change the value of a variable, unless we create a scope or a new variable
;; Example:
(def great-baby-name "Anthony")                             ;; great-baby-name = "Anthony"
(let [great-baby-name "Blood thunder"]                      ;; great-baby-name = "Blood thunder"
  great-baby-name)
great-baby-name                                             ;; great-baby-name = "Anthony"

;; Recursion
(defn sum
  ([vals]
   (sum vals 0))
  ([vals accumulating-total]
         (if (empty? vals)
            accumulating-total
            (recur (rest vals) (+ (first vals) accumulating-total)))))
;; In this previous function, we are updating accumulating-total recursively, as well as vals.
;; This means we are never changing anything outside the function, everything is happening inside the function itself.
;; Each recursive call to sum creates a new scope where vals and accumulating-total are bound to different values


;; Comp
;; Combines functions
;; The resulting function applies the given functions from right to left
(def greet-loudly (comp clojure.string/upper-case #(str "Hello, " % "!")))
(greet-loudly "world")


;; Memoization
;; Caches the result of a function call

;; Unmemorized example
(defn sleepy-square
  [n]
  (Thread/sleep 3000)
  (* n n))
(sleepy-square 3)                                           ;; It will take 3 seconds to return the result
(sleepy-square 3)                                           ;; It will take 3 seconds to return the result

;; Memorized example
(def memo-sleepy-square (memoize sleepy-square))
(memo-sleepy-square 3)                                      ;; It will take 3 seconds to return the result
(memo-sleepy-square 3)                                      ;; It will take no time to return the result
