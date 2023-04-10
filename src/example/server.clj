(ns example.server
  (:require [example.middlewares.default :as mdlw]
            [example.routes.demo-routes :as demo-routes]
            [honey.sql :as sql]
            [io.pedestal.log :as log]
            [malli.util :as mu]
            [muuntaja.core :as m]
            [next.jdbc :as jdbc]
            [reitit.coercion.malli]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]))

(defn create-document [{:keys [db]}]
  (->> {:select [:*]
        :from [:company]}
       sql/format
       (jdbc/execute! db)
       (assoc-in {:status 200} [:body :companies])))

(def routes [demo-routes/demo-routes
             ["/db"
              ["/create"
               {:handler create-document}]]
             ["/test" {:handler (fn [_] {:status 200 :body {:msg "hola"}})}]])

(defn router [env db]
  (ring/router
   routes
   {:exception pretty/exception
    :data {:env env
           :db db
           :coercion (reitit.coercion.malli/create
                      {;; set of keys to include in error messages
                       :error-keys #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                        ;; schema identity function (default: close all map schemas)
                       :compile mu/closed-schema
                        ;; strip-extra-keys (effects only predefined transformers)
                       :strip-extra-keys true
                        ;; add/set default values
                       :default-values true
                        ;; malli options
                       :options nil})
           :muuntaja m/instance
           :middleware mdlw/default-middlewares}}))

(defn handler [env db]
  (ring/ring-handler
   (router env db)
   (ring/routes
    (ring/create-default-handler))))

;; (defn message-handler
;;   [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
;;   (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
;;                    (String. payload "UTF-8") delivery-tag content-type type))
;;   (lb/publish ch "my-exchange" "hola-jobs.my-test-result" (String. payload "UTF-8")))

;; (defn subscribe-to-queues []
;;   (let [ch    (lch/open rabbitmq-publisher-connection)
;;         qname "hola-jobs.my-test"]
;;     (lc/subscribe ch qname message-handler {:auto-ack true})))
