(ns example.system
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [example.server :as server]
            [integrant.core :as ig]
            [io.pedestal.log :as log]
            [monger.core :as mg]
            [monger.credentials :as mcred]
            [org.httpkit.server :as httpkit])
  (:gen-class)
  (:import [com.mongodb MongoOptions ServerAddress]))

(def dbname "mydb")

(def bootstrap
  {:server/httpkit {:handler (ig/ref :ring/handler) :env (ig/ref :config/environment)}
   :ring/handler {:env (ig/ref :config/environment) :db (ig/ref :database.mongo/database)}
   :database.mongo/database {:conn (ig/ref :database.mongo/connection) :dbname dbname}
   :database.mongo/connection {:env (ig/ref :config/environment)}
   :config/environment {}})

(defmethod ig/init-key :server/httpkit [_ {:keys [handler env]}]
  (let [server (httpkit/run-server handler {:port (:app-port env)})]
  (log/info :msg "Example is here to serve you!")
  server))

(defmethod ig/halt-key! :server/httpkit [_ server]
  (server))

(defmethod ig/init-key :ring/handler [_ {:keys [env db]}]
  (server/handler env db))

(defmethod ig/init-key :database.mongo/database [_ {:keys [conn dbname]}]
  (mg/get-db conn dbname))

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