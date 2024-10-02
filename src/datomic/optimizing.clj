(ns datomic.optimizing)
;; Optimizing Queries
;; In this module, you will:
;;   * Use query-stats and io-context to optimize a query
;;
;; Connect to your REPL and run the commands below in order.
;;
;; If you need help, check the Help section in this repos README.md

(require
  '[datomic.api :as d]
  '[clojure.pprint :as pp])


;; In the previous exercises, we used Datomic with in-memory storage.
;; For this exercise, we will connect to a Datomic Transactor
;; that writes data to disk.
;;
;; If you have already run `bin/setup` and `bin/restore-db`, make sure
;; you start the transactor with the command below, and you are good to continue
;; with the exercise:
;;
;; * `bin/start-transactor`
;;
;; If this is the first time you work on this exercise, you need to run a couple
;; of additional steps
;;
;; 1. `bin/setup`            # One-time download and setup (takes a few minutes)
;; 2. `bin/start-transactor` # Start the transactor
;; 3. `bin/restore-db`       # In a separate terminal, restore the database

;; Let's connect to the database
(def db (d/db (d/connect "datomic:dev://localhost:4334/mbrainz-1968-1973")))

;; 🛟 If you get a "Connection refused" error,
;;    likely the transactor is not running.
;;    You can start the transctor by running
;;    `bin/start-transactor` in a separate terminal

;; Let's query for the count of unique album names per year in our db
(d/q '[:find ?year (count ?album-name)
       :where
       [?release :release/name ?album-name]
       [?release :release/year ?year]]
     db)

;; Now let's run the same query using d/query instead of d/q
;; this will allow us to later get io-context and query-stats information
(d/query {:query '[:find ?year (count ?album-name)
                   :where
                   [?release :release/name ?album-name]
                   [?release :release/year ?year]]
          :args [db]})

;; The query below asks for the io-context.
;; It is also wrapped in a pprint to make it easier to read
(pp/pprint (d/query {:query '[:find ?year (count ?album-name)
                              :where
                              [?release :release/name ?album-name]
                              [?release :release/year ?year]]
                     :args [db]
                     :io-context :learndatomic/context}))

;; The output should look like this
;;
;; {:ret
;;  [[1968 1497]
;;   [1969 1633]
;;   [1970 1742]
;;   [1971 1670]
;;   [1972 1872]
;;   [1973 1871]],
;;  :io-stats
;;  {:io-context :learndatomic/context,
;;   :api :query,
;;   :api-ms 33.6,
;;   :reads {:aevt 19, :ocache 19}}}
;;
;; If you get a significantly different :reads map, it is likely due to caching.
;; Run the query above again and you should get a similar result.

;; The content of the :reads map under :io-context tells us how many segments
;; from which indexes had to be retrieved to complete our query.
;;
;; Data in Datomic is stored as shallow trees of segments, where each segment
;; typically contains thousands of datoms.
;;
;; :reads {:aevt 19, :ocache 19} tells us that 19 segments were retrieved from
;; the AEVT-sorted index and that all of them were retrieved from
;; the Object Cache

;; Our result set has data from 1968 to 1973. Say that we are only
;; interested in the years before 1970. We can update our query to add this
;; filter

(pp/pprint (d/query {:query '[:find ?year (count ?album-name)
                              :where
                              [?release :release/name ?album-name]
                              [?release :release/year ?year]
                              [(< ?year 1970)]]
                     :args [db]
                     :io-context :learndatomic/context}))

;; The result of the query above should look like this
; {:ret [[1968 1497] [1969 1633]],
;  :io-stats
;  {:io-context :learndatomic/context,
;   :api :query,
;   :api-ms 22.02,
;   :reads {:aevt 18, :ocache 18}}}

;; We know that with this filter, our result set is calculated based on
;; roughly one third of the data from the previous queries, but when we look
;; at the :read map we see that we are retrieving data from 18 segments!
;; Looks like we are loading more segments than needed.

;; To help us optimize this query, we will enable :query-stats for our query
(pp/pprint (d/query {:query '[:find ?year (count ?album-name)
                              :where
                              [?release :release/name ?album-name]
                              [?release :release/year ?year]
                              [(< ?year 1970)]]
                     :args [db]
                     :io-context :learndatomic/context
                     :query-stats true}))

;; If you look to the results under {:query {:phases {:clauses ...}}},
;; you'll see something like this:
;;     [{:clause [?release :release/name ?album-name],
;;       :rows-in 0,
;;       :rows-out 11434,
;;       :binds-in (),
;;       :binds-out [?album-name ?release],
;;       :expansion 11434,
;;       :warnings {:unbound-vars #{?album-name ?release}}}
;;      {:clause [?release :release/year ?year],
;;       :rows-in 11434,
;;       :rows-out 3130,
;;       :binds-in [?album-name ?release],
;;       :binds-out [?year ?album-name],
;;       :preds ([(< ?year 1970)])}]}]
;;
;; Pay attention to :rows-in and :rows-out in the two :clause maps above.
;;
;; The first clause [?release :release/name ?album-name] significantly
;; expands our result set
;;   :rows-in 0,
;;   :rows-out 11434,
;;
;; The second clause [?release :release/year ?year] significantly reduces
;; our result set
;;   :rows-in 11434,
;;   :rows-out 3130,
;;
;; Note that we still retrive 18 segments
;; :reads {:aevt 18, :ocache 18}

;; What happens if we change the order of the clauses? If we make sure that the
;; more restrictive clause comes first?

(pp/pprint (d/query {:query '[:find ?year (count ?album-name)
                              :where
                              [?release :release/year ?year]
                              [?release :release/name ?album-name]
                              [(< ?year 1970)]]
                     :args [db]
                     :io-context :learndatomic/context
                     :query-stats true}))

;; Now the first clause [?release :release/year ?year] doesn't increase our
;; result set as much
;;   :rows-in 0,
;;   :rows-out 3486,
;;
;; And the second clause [?release :release/name ?album-name] slightly reduces
;; our result set
;;   :rows-in 3486,
;;   :rows-out 3130,
;;
;; With this optimization we now read 5 segments!
;;  :reads {:avet 1, :aevt 4, :ocache 5}

;; Resources
;; * How to leverage query-stats to optimize clause ordering
;;   https://nubank.atlassian.net/wiki/pages/viewpage.action?pageId=262922436717