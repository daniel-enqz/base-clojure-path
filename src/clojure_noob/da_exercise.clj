(ns clojure-noob.da-exercise
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; Read File
(def filename "suspects.csv")
(def initial-suspects (slurp (io/resource filename)))

;; Base Functions
(def vamp-keys [:name :glitter-text])

(defn str->int
  [s]
  (Integer. s))

(defn map->str
  [record]
  (str (:name record) "," (:glitter-text record)))

(def conversions {:name identity :glitter-text str->int})

(defn convert
  [vamp-key value]
  ((get conversions vamp-key) value))

(defn mapify->str
  [records]
  (str/join "\n" (map map->str records)))

(defn validate
  [validations record]
  (every? (fn [[key validation-fn]]
            (and (contains? record key)
                 (validation-fn (get record key))))
          validations))

(def validations
  {:name #(and (string? %) (not (str/blank? %)))
   :glitter-text #(and (integer? %) (>= % 0))})

(defn add-suspect
  [suspects suspect-name suspect-glitter-text]
  (if (validate validations {:name suspect-name :glitter-text (str->int suspect-glitter-text)})
    (str suspects "\n" suspect-name "," suspect-glitter-text)
    suspects))

(defn remove-suspect
  [suspects suspect-name]
  (->> (str/split-lines suspects)
       (remove #(str/starts-with? % (str suspect-name ",")))
       (str/join "\n")))

;; File parsing
(defn parse
  "Convert a CSV into a seq of rows"
  [string]
  (map #(str/split % #",")
       (str/split-lines string)))

;; Return Vector Map Keys
(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-text 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))

;; Filter Values From CSV
(defn glitter-filter
  "From a list of maps, return only those with a glitter-text of at least minimum-glitter"
  [minimum-glitter records]
  (filter #(>= (:glitter-text %) minimum-glitter) records))

;; Process Suspects
(defn process-suspects
  [suspects]
  (->> suspects
       parse
       mapify
       (filter #(validate validations %))
       (glitter-filter 3)
       (map :name)))


;; Example usage
(def updated-suspects (-> initial-suspects
                          (add-suspect "Daniel" "10")
                          (remove-suspect "Bella Swan")))

(process-suspects updated-suspects)

;; Process maps back to CSV format
(mapify->str (mapify (parse updated-suspects)))