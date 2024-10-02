(ns datomic.setup
  (:require [datomic.api :as d]))

(defn hello-datomic-conn
  "Create a connection to an anonymous, in-memory database."
  []
  (let [uri "datomic:mem://hello-datomic"]
    (d/delete-database uri)
    (d/create-database uri)
    (d/connect uri)))

