(ns example.system
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [example.server :as server]
            [integrant.core :as ig]
            [io.pedestal.log :as log]
            [org.httpkit.server :as httpkit]
            [next.jdbc.connection :as connection])
  (:import (com.zaxxer.hikari HikariDataSource))
  (:gen-class))

(def bootstrap
  {:server/httpkit {:handler (ig/ref :ring/handler) :env (ig/ref :config/environment)}
   :ring/handler {:env (ig/ref :config/environment) :db (ig/ref :database/postgres)}
   :database/postgres {:env (ig/ref :config/environment)}
   :config/environment {}})

(defmethod ig/init-key :server/httpkit [_ {:keys [handler env]}]
  (let [server (httpkit/run-server handler {:port (:app-port env)})]
    (log/info :msg "Example is here to serve you!")
    server))

(defmethod ig/halt-key! :server/httpkit [_ server]
  (server))

(defmethod ig/init-key :ring/handler [_ {:keys [env db]}]
  (server/handler env db))

(defmethod ig/init-key :database/postgres [_ {:keys [env]}]
  (->> {:dbtype "postgres"
        :dbname (:database-name env)
        :host (:database-host env)
        :username (:database-user env)
        :password (:database-password env)
        :maximumPoolSize (:database-pool-size env)
        :dataSourceProperties {:socketTimeout (:database-timeout env)}}
       (connection/->pool HikariDataSource)))

(defmethod ig/init-key :config/environment [_ _]
  (load-config
   :merge
   [(source/from-system-props)
    (source/from-env)]))

(defn -main []
  (ig/init bootstrap))