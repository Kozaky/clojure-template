;; (ns example.rabbitmq
;;   (:require [mount.core :refer [defstate]]
;;             [langohr.core      :as rmq]
;;             [example.environment :refer [env]]))

;; (defn create-connection []
;;   (let [host (:rabbitmq-host env)
;;         username (:rabbitmq-user env)
;;         password (:rabbitmq-password env)]
;;     (rmq/connect {:host host :username username :password password})))

;; (declare rabbitmq-publisher-connection)

;; (defstate ^{:on-reload :stop}
;;   rabbitmq-publisher-connection
;;   :start (create-connection)
;;   :stop #(rmq/close rabbitmq-publisher-connection))