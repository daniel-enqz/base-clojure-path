(ns clojure-noob.a-core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello pana"))

;; simple if-else
(if false
  "By Zeus Hammer!!!"
  "By Aquaman Trident")

;; multiple actions in each branch
(if false
  (do (println "Success!")
      "By Zeus Hammer!")
  (do (println "Failure!")
      "By Aquamans trident!"))

;; where clauses
(when true
  (println "Success!")
  "abra cadabra!")

;; about nil
(nil? 1)                                                    ;; false
(nil? nil)                                                  ;; true
(if nil                                                     ;; nil is considered falsey
  "Hello"
  "nil returned")
(if "Hello"                                                 ;; this is considered logically true
  "Yes hello!")

;; equal operator
(= 1 1)
(= nil nil)
(= 1 2)

;; or
(or false :hey nil :bye)                                    ;; will return :hey as its the first true value
(or false (= nil 1) nil :hello nil)                         ;; will return :hello because its first true value

;; and
(and true :hello "pana")                                    ;; will return "pana" as its last true value
(and true (= nil 1) nil "pana" nil)                         ;; will return false

;; binding values
(def failed-panas
  ["saul" "diego" "emiliano"])

failed-panas

(defn error-message
  [severity]
  (str "OH NO! THIS IS "
       (if (= severity :low)
         "A LITTLE BAD"
         "SUPER BAD")))

(error-message :low)
