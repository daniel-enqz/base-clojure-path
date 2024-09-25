(ns clojure-noob.d_entities)

; https://github.com/nubank/tech-learning-clojure/blob/main/03-data-and-domain-entities/en/README.md
; Maps
; The most common structure used to represent an entity in Clojure is a PersistentArrayMap,
; which we usually just call a map. To construct a map, we can use the array-map function:

(def my-map (array-map :first 1 :second 2 :third 3))
(type my-map)

;; If we were solving a problem in the 'human resources' problem domain,
;; we might use the above structure to represent an employee.
;; We can see that the employee has a first name, last name, a business unit they report to, and an employee ID.

(def msilva { :first-name     "Maria"
             :last-name      "da Silva"
             :business-unit  :ctp
             :employee-id    12345 })
(get msilva :employee-id)

(:employee-id msilva)

; Records
; A way to define a new type in Clojure.
; They are similar to maps, but they have a fixed set of keys.
(defrecord Employee [first-name last-name business-unit employee-id])

; (def msilva (Employee. "Maria" "da Silva" :ctp 12345))
(def msilva (map->Employee {:first-name    "Maria"
                            :last-name     "da Silva"
                            :business-unit :ctp
                            :employee-id   12345 }))
(:employee-id msilva)
(assoc msilva :unrelated-data "this is unrelated")
(:unrelated-data msilva)

;; Modeling Relationships
;; Nested Maps
(def msilva { :first-name     "Maria"
              :last-name      "da Silva"
              :business-unit  :ctp
              :employee-id    12345
              :address        { :street "123 Main St"
                                :city   "Sao Paulo"
                                :state  "SP"
                                :zip    "01234" }})

(:zip (:address msilva))

;; One-to-One
(defrecord Employee [first-name last-name business-unit employee-id])
(defrecord Address [street city state zip])

(def msilva (map->Employee {:first-name    "Maria"
                            :last-name     "da Silva"
                            :business-unit :ctp
                            :employee-id   12345
                            :address       (map->Address {:street "123 Main St"
                                                          :city   "Sao Paulo"
                                                          :state  "SP"
                                                          :zip    "01234"})}))

(:zip (:address msilva))

;; One-to-Many
(defrecord Employee [first-name last-name business-unit employee-id])
(defrecord Address [street city state zip])

(def msilva (map->Employee {:first-name    "Maria"
                            :last-name     "da Silva"
                            :business-unit :ctp
                            :employee-id   12345
                            :addresses     [(map->Address {:street "123 Main St"
                                                           :city   "Sao Paulo"
                                                           :state  "SP"
                                                           :zip    "01234"})
                                            (map->Address {:street "456 Elm St"
                                                           :city   "Sao Paulo"
                                                           :state  "SP"
                                                           :zip    "56789"})]}))

(:zip (first (:addresses msilva)))

;; Identifiers
;; This approach will look familiar if you've every worked with a datastore of some kind--the identifiers are used to lookup and relate the data.
;; Here the nested representation can be constructed from the referenced identifiers,
;; but we maintain our ability to update each entity (or list of entities) independently.
;; When the parts of a model are managed independently, this is a good approach.
(def customers {
                12345 { :customer-id 12345
                       :first-name  "Maria"
                       :last-name   "da Silva"
                       :accounts [
                                  "12345-01"
                                  "12345-02"
                                  ] }
                12346 { :customer-id 12346
                       :first-name  "John"
                       :last-name   "Smith"
                       :accounts [
                                  "12346-01"
                                  ]}
                      })

(def accounts {
               "12345-01" { :account-id "12345-01"
                           :account-type :checking }
               "12345-02" { :account-id "12345-02"
                           :account-type :credit }
               "12346-01" { :account-id "12346-01"
                           :account-type :savings }
                          })

;; Stateful References
(def accounts {
               "12345-01" (ref { :account-id "12345-01"
                                :account-type :checking })
               "12345-02" (ref { :account-id "12345-02"
                                :account-type :credit })
               "12346-01" (ref { :account-id "12346-01"
                                :account-type :savings })})

(def customers {
                12345 (ref { :customer-id 12345
                            :first-name  "Maria"
                            :last-name   "da Silva"
                            :accounts [
                                       (get accounts "12345-01")
                                       (get accounts "12345-02")
                                       ]})
                12346 (ref { :customer-id 12346
                            :first-name  "John"
                            :last-name   "Smith"
                            :accounts [
                                       (get accounts "12346-01")
                                       ]})})

(def msilva (get customers 12345))  ; msilva is now the ref itself, not its contents
(get @msilva :first-name)  ; "Maria"
(dosync
  (alter msilva assoc :first-name "Maria da Silva"))
(get @msilva :first-name)  ; "Maria da Silva"