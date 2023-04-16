(ns example.application.use-cases.items.get-item-by-id-use-case
  (:require [example.application.protocols.item-service :refer [fetch-by-id]]))

(defn execute [store id]
  (fetch-by-id store id))