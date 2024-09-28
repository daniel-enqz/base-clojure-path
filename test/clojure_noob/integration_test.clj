(ns clojure-noob.integration-test
  (require '[state-flow.api :refer [flow defflow match?]]
           '[state-flow.helpers.component.kafka :as helpers.kafka]
           '[state-flow.helpers.component.servlet :as servlet]))


;; state-flow.api
(defflow my-flow
         (flow "using match"
               (match? 1 1)
               (match? {:a 1} {:a 1 :b 2})))

;; state-flow.helpers.servlet
;; You can use state-flow.helpers.servlet to test HTTP requests.
;; The example below tests if getting /api/hello-world returns {:status 200 :body {:greeting "Hello World!"}}.

(defflow hello-world
         (flow "route /api/hello-world returns a greeting"
               (match?
                 {:status 200
                  :body   {:greeting "Hello World!"}}
                 (servlet/request {:method :get
                                   :uri    "/api/hello-world"}))))

;; state-flow.helpers.kafka
;; You can use state-flow.helpers.servlet to test Kafka queues.
(defflow example
         (flow "consume-message on the :update-accumulator topic ... updates the accumlator"
               (helpers.kafka/consume-message {:topic :update-accumulator
                                               :message {:message "message content"}})

               (match? {:status 200
                        :body [{:message "message content"}]}
                       (servlet/request {:method :get
                                         :uri "/api/accumulator"}))))

