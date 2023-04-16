(ns example.infrastructure.handlers.items-handler
  (:require [example.application.use-cases.items.create-item-use-case :as create-item-use-case]
            [example.application.use-cases.items.get-item-by-id-use-case :as get-item-by-id-use-case]
            [example.infrastructure.repositories.item-store :as item-store]
            [io.pedestal.log :as log]))

(defn ^:private item-service [db] (item-store/create-store db))

(defn create-item [{db :db, body :body-params}]
  (let [saved-item (create-item-use-case/execute (item-service db) body)]
    (log/info :di/store (hash item-service))
    {:status 200 :body {:msg "created" :item saved-item}}))

(defn get-item [{db :db, {id-str :id} :path-params}]
  (let [id (parse-long id-str)
        item (get-item-by-id-use-case/execute (item-service db) id)]
    {:status 200 :body {:msg "got" :item item}}))