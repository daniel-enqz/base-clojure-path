(ns clojure-noob.c-functions)


;; Calling functions have the same syntax (operator, operands)
(or + -)                                                    ;; Will return the first truthy value '+' function
((or + -) 1 2 3)                                            ;; The operator returned is +
((and (= 1 1) +) 1 2 3)                                     ;; Remember the return value of 'and'
((first [+ 0]) 1 2)                                         ;; is the first false value or the last truthy value

;; Numbers and strings aren't functions
;;(1 2 3 4)
;;("test" 1 2 3)

;; Functions that can either take a function as an argument or return a function are called higher-order functions.
;; We treat functions passed to functions as normal values (like data types, structures, etc..), these are called first-called functions
;; For example, map is a higher-order function, it takes to arguments, a function and a collection

(map inc [1 2 3 4 5])
;; POWER of First Class-Functions
;; Instead of operating only on data, we can operate on behaviors (functions)
;; In this previous map example, "map" allows tou to operate over the incremental behavior,
;; but that behavior could be actually anything.
;; map abstracts the process of transforming a collection, regardless of the specific transformation or collection

;; Clojure solves all functions recursively, before applying them to a function
(+ (inc 199) (/ 100 (- 7 2)))

;; DEFINING FUNCTIONS
(defn too-enthusiastic
  "Return a cheer for the given name"
  [name]
  (str "Hello " name " you are a great person!"))

(too-enthusiastic "Daniel")

;; You can pass 0 or more arguments to a function.
;; The number of parameters in a function is the 'arity'
(defn nba-player-hello
  "Return the NBA player and its team greeting"
  ([player]
   (nba-player-hello player "default-team" "age: 31" 12 90))
  ([player team & data]
  (str "Hello! " player " your team is " team " and here's additional data " (clojure.string/join ", " data))))

(nba-player-hello "Giannis")
(nba-player-hello "Steph Curry" "Golden State Warriors" "age: 31" 30 2)

;; What's happening in the previous function is that we are doing arity-overloading,
;; meaning it will run a different block depending on the arguments passed
;; If we only pass one argument, like Giannis it will run first block and call the function again but with correct arguments.
;; Then, the function will run the second block, that has a variable arity behavior.
;; Receives 2 params and one last one using rest parameter (&),
;; it always needs to be in the last position, and it will be treated as a list.


;; DESTRUCTING
;; Associate names with values in a list, vector, set or map
(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices))))

(chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])
; => Your first choice is: Marmalade
; => Your second choice is: Handsome Jack
; => We're ignoring the rest of your choices. Here they are in case \

(defn announce-treasure-location
  [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(announce-treasure-location {:lat 28.22 :lng 81.33})

;; Function Body
(defn number-comment-inclusiveness
  "Returns appropriate string depending on number inclusiveness.
   A number is considered inclusive if it's between 50 and 100 (inclusive)."
  [number]
  (if (and (>= number 50) (<= number 100))
    "Your number is inclusive"
    "Your number is not inclusive"))

(println (number-comment-inclusiveness 70))

;; Anonymous Functions
(map (fn [name] (str "Hi, " name)) ["Darth Vader" "Pana"])  ;; the anonymous function acts like "inc", telling map what to do with the list
(map #(str "Hi, " %)
     ["Darth Vader" "Pana Rabbit"])                         ;; Alternative


((fn [x] (* x 3)) 8)
(filter (fn [x] (> x 5)) [1 2 3 4 5 6 7 8 9 10])
;; Anonymous function as an argument to another function
(def multiply-by (fn [y] (fn [x] (* x y))))
((multiply-by 5) 3)

;; We can treat anonymous functions as normal functions (defn), also:
;Assigned to variables
;Passed as arguments to other functions
;Returned as values from functions
;Created on the fly where functions are expected

;; For example, here we can assign an anonymous function to a variable name called: my-special-multiple
(def my-special-multiple (fn [x] (* x 3)))
(my-special-multiple 8)
(#(* % 3) 8)                                                ;; Alternative

;; Pass multiple arguments to anonymous functions
(#(str %1 " and " %2) "Daniel" "Pablo")
(#(identity %&) 1 2 3)


;; Returning Functions
;; These are called closures,
;; means they can access all the variables from the scope it was created.

(defn inc-marker
  "Crate a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-marker 3))
(inc3 7)

;; Function Explanation:
;1. We create a function called inc-marker hat receives one argument: inc-by
;2. This function returns an anonymous function or a closure (has access to inc-by)
;3. This returned function is an anonymous function that takes an argument (%) and adds inc-by
;4. Therefore, we call inc-marker, sending 3 as argument. This "3" integer will be used by the anonymous function.
;5. Now we bind this to inc3, meaning inc3 is actually an anonymous function like: #(+ % 3) or in normal form: (fn [x] (+ x 3))
;6. Finally, as we know that inc3 is the anonymous function previously mentioned, we can call it and send an argument.


;To deal with Side Effects, you can bookend them. This means that they run before or after a block of Pure Functions. You can refactor the code to bookend side effects and, at the same time, make "hello-text" a pure function.
(defn fetch-name
  "Reads and returns the contents of a hard coded text file"
  []
  (slurp "~/name.txt"))

(defn hello-text
  "Says Hello, Name"
  [name]
  (println "Hello" name))

(hello-text (fetch-name))