(ns example.system
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [example.server :as server]
            [integrant.core :as ig]
            [io.pedestal.log :as log]
            [monger.core :as mg]
            [monger.credentials :as mcred]
            [ring.adapter.jetty :as jetty])
  (:gen-class)
  (:import [com.mongodb MongoOptions ServerAddress]))

(def dbname "mydb")

(def bootstrap
  {:adapter/jetty {:handler (ig/ref :ring/handler) :env (ig/ref :config/environment)}
   :ring/handler {:env (ig/ref :config/environment) :db (ig/ref :database.mongo/database)}
   :database.mongo/database {:conn (ig/ref :database.mongo/connection) :dbname dbname}
   :database.mongo/connection {:env (ig/ref :config/environment)}
   :config/environment {}})

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler env]}]
  (jetty/run-jetty handler {:join? false :port (:app-port env)}))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :ring/handler [_ {:keys [env db]}]
  (server/handler env db))

(defmethod ig/init-key :database.mongo/database [_ {:keys [conn dbname]}]
  (let [db (mg/get-db conn dbname)] (log/info :db db) db))

(defmethod ig/init-key :database.mongo/connection [_ {:keys [env]}]
  (let [host (:database-host env)
        port (:database-port env)
        user (:database-user env)
        pool-size (:database-pool-size env)
        ^MongoOptions opts (mg/mongo-options {:connections-per-host pool-size})
        ^ServerAddress address (mg/server-address host port)
        password (:database-password env)
        credentials (mcred/create user dbname password)]
    (mg/connect address opts credentials)))

(defmethod ig/init-key :config/environment [_ _]
  (load-config
   :merge
   [(source/from-system-props)
    (source/from-env)]))

(defn -main []
  (ig/init bootstrap))