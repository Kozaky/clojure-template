(ns example.application.use-cases.items.create-item-use-case
  (:require [example.application.protocols.item-service :as item-service]))

(defn execute [item-service item]
  (item-service/save item-service item))