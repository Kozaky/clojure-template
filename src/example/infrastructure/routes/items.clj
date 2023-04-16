(ns example.infrastructure.routes.items
  (:require [example.infrastructure.handlers.items-handler :as item-handlers]))

(def item-routes [["/items"
                   ["/"
                    {:post {:parameters {:body {:name string?}}
                            :handler item-handlers/create-item}}]

                   ["/:id"
                    {:get {:parameters {:path {:id int?}}
                           :handler item-handlers/get-item}}]]])