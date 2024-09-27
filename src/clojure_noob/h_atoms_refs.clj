(ns clojure-noob.h_atoms-refs)

(def learn-atom (atom 0))
(+ @learn-atom 1)                                           ;; Same as: (+ (deref learn-atom) 1)

;; Updating an atom
(swap! learn-atom inc)                                      ;; Note it returns the new value of the atom
(+ @learn-atom 1)                                           ;; 2
(swap! learn-atom * 5)                                      ;; 5

;; Reset
(reset! learn-atom 2)

;; Use @ when you want to read the current value of an atom without changing it.
;; It's typically used in operations where you need the atom's value as part of a larger expression.
;; You don't use @ with functions that are designed to work with atoms directly, like swap! and reset!.


;; Refs
(def learn-ref (ref ["red" "green"]))

(def other-ref (ref ["purple" "blue"]))

(dosync                                                             ;; dosync will run atomic, isolated, retryable transactions
  (let [moving (last @other-ref)]
    (alter learn-ref conj moving)
    (alter other-ref pop)))

(dosync
  (commute learn-ref conj "orange")
  (commute other-ref conj "yellow"))

;; In alter, order matters.
; For example, incrementing a counter could often use commute because the order of increments doesn't matter,
; while updating a bank account balance should typically use alter because the order of deposits and withdrawals is important.

;; Theory
; Atoms are used for uncoordinated, synchronous, independent updates to a single identity.
; - Provide atomic (all-or-nothing) updates
; - Best for independent values that don't need to be coordinated with other references
; Refs are used for coordinated, synchronous, shared updates to multiple identities.
; - Ensure that multiple refs are modified in a single, atomic operation
; - Use Software Transactional Memory (STM) for consistency
