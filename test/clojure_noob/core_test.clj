(ns clojure-noob.core-test
  (:require [clojure.test :refer :all]
            [clojure-noob.a-core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(is (= 4 (+ 2 2)))
(is (= 5 (+ 2 2)))
(is (instance? Long 256))
(is (.startsWith "abcde" "ab"))
(is (= 5 (+ 2 2)) "Making sure + works")

;; Nesting
(testing "Arithmetic"
  (testing "with positive integers"
    (is (= 4 (+ 2 2)))
    (is (= 7 (+ 3 4))))
  (testing "with negative integers"
    (is (= -5 (+ -2 -2))) ; fails
    (is (= -1 (+ 3 -4)))))

;; Correct way (use deftest):
;; run-tests runs all tests in the given namespaces; prints results.
;; Defaults to current namespace if none given. Returns a map summarizing test results.
(deftest my-test
  (is (.startsWith "hello" "goodbye")) ; fails
  (is (thrown? ArithmeticException (/ 1 0))))

;; (run-tests)
(comment
  (run-tests))                                              ;; Will be ignored by the reader, but can be evaluated in a REPL.

;; comment macro: The code inside comment is read by the Clojure reader
;; but not evaluated when the file is loaded. However, it can be easily evaluated in a REPL.


;; Closer Look

;; are
;; Usage: (are argv expr & args)
;; Checks multiple assertions.
(deftest my-are-test
  (are [x y] (= x y)
             2 (+ 1 1)
             4 (* 2 2)
             8 (* 4 2)))
(run-test my-are-test)

;; is
;; Usages: (is form) (is form msg)
;; Checks one assertion.
(deftest my-is-test
  (is (= 4 (+ 2 2)) "Two plus two should be 4"))
(run-test my-is-test)


;; is thrown?
;; Usage: (is (thrown? c body))
;;; Checks that an instance of c is thrown from body, fails if not; then returns the thing thrown.
(deftest my-is-thrown-test
  (is (thrown? ArithmeticException (/ 1 0))))
(run-test my-is-thrown-test)

;; is thrown-with-msg?
;; Usage: (is (thrown-with-msg? c re body))
;; Checks that an instance of c is thrown from body and that the message on the exception matches the regular expression re.
(deftest my-is-thrown-with-msg-test
  (is (thrown-with-msg? ArithmeticException #"^Divide by ze" (/ 1 0))))
(run-test my-is-thrown-with-msg-test)


;; Usage: (run-test test-symbol)
;; Runs a single test.
(deftest my-is-test
  (is (= 4 (+ 2 2)) "Two plus two should be 4"))
(run-test my-is-test)


;; successful?
;; Usage: (successful? summary)
;; Returns true if the given test summary indicates all tests were successful, false otherwise.
(def summary {:test 2, :pass 4, :fail 0, :error 0, :type :summary})
(successful? summary) ; true

(def summary {:test 2, :pass 3, :fail 1, :error 0, :type :summary})
(successful? summary) ; false


;; testing
;;Usage: (testing string & body)
;; Adds a new string to the list of testing contexts.
;; May be nested, but must occur inside a test function (deftest).
(deftest my-test
  (testing "Arithmetic"
    (testing "with positive integers"
      (is (= 4 (+ 2 2)))
      (is (= 7 (+ 3 4))))
    (testing "with negative integers"
      (is (= -5 (+ -2 -2))) ; fails
      (is (= -1 (+ 3 -4))))))
(run-test my-test)
; {:test 1, :pass 3, :fail 1, :error 0, :type :summary}


;; use-fixtures
;; Usage: (use-fixtures :fixtyre-type fixture-fn1 fixture-fn2 ... fixture-fnN)
;; Wrap test runs in a fixture function to perform setup and tear-down.

;; :each fixture type (Fixtures run around each test)
(defn my-fixture [f]
  (println "@TODO Setup")
  (f)
  (println "@TODO Tear-down"))

(deftest test-a
  (is (= 4 (+ 2 2))))

(deftest test-b
  (is (= 8 (+ 2 6))))

;;(use-fixtures :each my-fixture)

;; :once fixture type
;; Fixtures run once around all tests.
(defn my-fixture [f]
  (println "@TODO Setup")
  (f)
  (println "@TODO Tear-down"))

(deftest test-a
  (is (= 4 (+ 2 2))))
(deftest test-b
  (is (= 8 (+ 2 6))))
(use-fixtures :once my-fixture)

;; Matcher Combinators
(require '[clojure.test :refer :all]
         '[matcher-combinators.test]
         '[matcher-combinators.matchers :as m])

;; example that does not use matcher combinators
(deftest test-without-matcher-combinators
  (is (= 37 (+ 29 8))))

;; example that uses matcher combinators
(deftest test-with-matcher-combinators
  (is (match? 37 (+ 29 8))))


;; Practical Example
(defn person-with-calculated-age
  "Returns a map with an added :age calculated
  based on :birth/year and current-year"
  [person current-year]
  (let [age (- current-year (:birth/year person))]
    (assoc person :age age)))

(person-with-calculated-age {:birth/year 2000} 2024)

(deftest test-person-with-calculated-age
  (let [person {:name/first  "Alfredo"
                :name/last   "da Rocha Viana"
                :name/suffix "Jr."
                :birth/year 1956}]
    (is (match? {:age 68} (person-with-calculated-age person 2024)))))
(run-test test-person-with-calculated-age)

;; When used in maps, the embeds matcher ignores all the un-specified keys.

(run-tests)
1
