(ns datomic.modeling
  (:require [datomic.api :as d]
            [datomic.setup :refer [hello-datomic-conn]]))

(def conn (hello-datomic-conn))

(defn install-attributes [conn]
  @(d/transact conn [
                     {:db/ident :person/first-name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A person's name"
                      :db/unique :db.unique/identity}

                     {:db/ident :person/last-name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A person's last name"
                      :db/unique :db.unique/identity}

                     {:db/ident :likes/drink
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A favorite drink"}

                     {:db/ident :likes/food
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A favorite food"}
                     ]))

(install-attributes conn)

(defn load-data [conn]
  @(d/transact conn [
                     {:person/first-name "Helena"
                      :person/last-name "Almeida"
                      :likes/food "pizza"
                      :likes/drink "beer"}

                     {:person/first-name "Alice"
                      :person/last-name "Campos"
                      :likes/food "sushi"
                      :likes/drink "wine"}

                     {:person/first-name "Laura"  :person/last-name "Ferreira" :likes/food "pizza" :likes/drink "water"}
                     {:person/first-name "Miguel" :person/last-name "Melo"     :likes/food "pizza" :likes/drink "water"}
                     {:person/first-name "Arthur" :person/last-name "Ramos"    :likes/food "tacos" :likes/drink "beer"}
                     {:person/first-name "Noah"   :person/last-name "Silva"    :likes/food "curry" :likes/drink "beer"}
                     ])
  )

(load-data conn)

;; ?n means the name of the person
;; ?e means the entity
(d/q '[:find ?n
       :where [?e :likes/food "pizza"]
       [?e :person/first-name ?n]]
     (d/db conn))

;; Query all
(d/q '[:find ?n
       :where [?e :person/first-name ?n]]
     (d/db conn))

;; Query with specific food and drink
(d/q '[:find ?e
       :where [?e :person/first-name ?n]
       [?e :likes/drink ?l]
       [?e :likes/food ?f]
       [(= ?f "pizza")]
       [(= ?l "beer")]]                                     ;; Here we are using a predicate, examples: (=, <, >, <=, >=) or even a function
     (d/db conn))

;; PARAMETERIZED QUERY
(d/q '[:find ?n
       :in $ ?f
       :where [?e :likes/food ?f]
       [?e :person/first-name ?n]]
     (d/db conn) "pizza")

;; PARAMETERIZED QUERY WITH TUPLES
(d/q '[:find ?n
       :in $ [[?f ?l]]
       :where [?e :likes/food ?f]
       [?e :likes/drink ?l]
       [?e :person/first-name ?n]]
     (d/db conn) [["pizza" "beer"]])

;; Relationships example
;[:find ?title ?box-office
; :in $ ?director [[?title ?box-office]]
; :where
; [?p :person/name ?director]
; [?m :movie/director ?p]
; [?m :movie/title ?title]]

;[
; ...
; ["Die Hard" 140700000]
; ["Alien" 104931801]
; ["Lethal Weapon" 120207127]
; ["Commando" 57491000]
; ...
; ]

;; FIND ATTRIBUTES
(d/q '[:find ?attr
       :where [_ :db/ident ?attr]]
     (d/db conn))

;; TRANSACTION INFO
(d/q '[:find ?tx ?tx-time
       :where
       [?tx :db/txInstant ?tx-time]]
     (d/db conn))


;; RELATIONSHIP DB SCHEMA
;[:find ?attr ?type ?card
; :where
; [_ :db.install/attribute ?a]
; [?a :db/valueType ?t]
; [?a :db/cardinality ?c]
; [?a :db/ident ?attr]
; [?t :db/ident ?type]
; [?c :db/ident ?card]]


;; TRANSFORMATION FUNCTIONS
;; A transformation function clause has the shape
;; [(<fn> <arg1> <arg2> ...) <result-binding>]
;; where <result-binding> can be:

;; Scalar: ?age
;; Tuple: [?foo ?bar ?baz]
;; Collection: [?name ...]
;; Relation: [[?title ?rating]]

;; NOTE: Transformation Functions cant be nested
;(defn age [birthday today]
;  (quot (- (.getTime today)
;           (.getTime birthday))
;        (* 1000 60 60 24 365)))
;
;[:find ?age
; :in $ ?name ?today
; :where
; [?p :person/name ?name]
; [?p :person/born ?born]
; [(tutorial.fns/age ?born ?today) ?age]]

;; AGGREGATES (min, max, sum, avg, etc.)
(d/q '[:find (count ?e)
       :where [?e :likes/food "pizza"]]
     (d/db conn))

;; RULES
;; Rules are a way to define a query that can be reused

;; Define a rule
;[(actor-movie ?name ?title)
; [?p :person/name ?name]
; [?m :movie/cast ?p]
; [?m :movie/title ?title]]
;
;[:find ?name
; :in $ %
; (actor-movie ?name "The Terminator")]

;; The % symbol in the :in clause represent the rules.
;; You can write any number of rules.

;[[(rule-a ?a ?b)
;  ...]
; [(rule-b ?a ?b)
;  ...]
; ...]


;; EXAMPLE (Find directors and cast members):
; Rule:
;[[(associated-with ?person ?movie)
;  [?movie :movie/cast ?person]]
; [(associated-with ?person ?movie)
;  [?movie :movie/director ?person]]]
;
;[:find ?name
; :in $ %
; :where
; [?m :movie/title "Predator"]
; (associated-with ?p ?m)
; [?p :person/name ?name]]

; More here: https://datomic.learn-some.com/chapter/8
