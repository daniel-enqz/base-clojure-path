(ns datomic.exercise-1
  (:require [datomic.api :as d]
            [datomic.util :as util]
            [clojure.pprint :as pp]))

;; Output explanation can be found here: https://github.com/nubank/learn-datomic/blob/main/src/learn_datomic/d_02_hello_world.md

(comment
  ;; create a connection to an anonymous, in-memory database
  (def conn (util/scratch-conn))

  ;; Let's start creating a list of people
  ;; For now, we only care about their first names

  ;; install a new attribute :person/first-name of type string and
  ;; cardinality one that is unique
  @(d/transact conn [{:db/ident :person/first-name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A person's name"
                      :db/unique :db.unique/identity}])

  ;; create an entity with attribute :person/first-name Alex
  @(d/transact conn [{:person/first-name "Alex"}])

  ;; List all of the :person/first-name values
  (d/q '[:find ?n
         :where [?e :person/first-name ?n]]
       (d/db conn))

  ;; Now let's keep track of their favorite food.

  ;; install a new attribute :favorite/food of type string and
  ;; cardinality one that is not unique
  @(d/transact conn [{:db/ident :favorite/food
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "A favorite food"}])

  ;; create a new entity in our database using the :person/first-name attribute
  ;; and the :favorite/food attribute
  @(d/transact conn [{:person/first-name "Sophia",
                      :favorite/food "Sushi"}])

  ;; list everyone that likes Sushi
  (d/q '[:find ?n
         :where
         [?e :person/first-name ?n]
         [?e :favorite/food "Sushi"]]
       (d/db conn))

  ;; interestingly, a favorite food is _not_ required here --
  ;; let's add a person with no favorite food
  @(d/transact conn [{:person/first-name "Mia"}])

  ;; list everyone that has no favorite food
  (d/q '[:find ?n
         :where
         [?e :person/first-name ?n]
         [(missing? $ ?e :favorite/food)]]
       (d/db conn))

  ;; let's define the :favorite/food for Alex
  @(d/transact conn [{:db/id [:person/first-name "Alex"],
                      :favorite/food "Pizza"}])

  ;; list what everyone likes
  (d/q '[:find ?n ?likes
         :where
         [?e :person/first-name ?n]
         [?e :favorite/food ?likes]]
       (d/db conn))

  ;; let's update Alex's :favorite/food to Salad
  @(d/transact conn [{:db/id [:person/first-name "Alex"],
                      :favorite/food "Salad"}])

  ;; query the history to get historical :favorite/food values
  (pp/pprint
    (sort-by first
             (d/q '[:find ?txI ?n ?likes ?op
                    :where
                    [?e :person/first-name ?n]
                    [?e :favorite/food ?likes ?tx ?op]
                    [?tx :db/txInstant ?txI]]
                  (d/history (d/db conn)))))

  ;; ✅ Install a new attribute for :favorite/drink,
  ;;    set and query the favorite drink for Alex and Sophia
  )
