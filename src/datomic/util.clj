;; Copy-paste from https://github.com/Datomic/day-of-datomic/blob/master/src/datomic/samples/repl.clj

(ns datomic.util
  (:import datomic.Util)
  (:require
    [clojure.java.io :as io]
    [datomic.api :as d]))

(def db-uri-base "datomic:mem://")

(def resource io/resource)

(defn scratch-conn
  "Create a connection to an anonymous, in-memory database."
  []
  (let [uri (str db-uri-base (d/squuid))]
    (d/delete-database uri)
    (d/create-database uri)
    (d/connect uri)))

(defn read-all
  "Read all forms in f, where f is any resource that can
   be opened by io/reader"
  [f]
  (Util/readAll (io/reader f)))

(defn transact-all
  "Load and run all transactions from f, where f is any
   resource that can be opened by io/reader."
  [conn f]
  (loop [n 0
         [tx & more] (read-all f)]
    (if tx
      (recur (+ n (count (:tx-data  @(d/transact conn tx))))
             more)
      {:datoms n})))

