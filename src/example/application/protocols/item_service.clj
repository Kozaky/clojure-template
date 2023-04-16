(ns example.application.protocols.item-service)

(defprotocol ItemService
  "Service to manage items"
  (save [this item] "put the item to the datasource")
  (fetch-by-id [this id] "retrieves the item from the datasource by id"))