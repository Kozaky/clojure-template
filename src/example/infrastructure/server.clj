(ns example.infrastructure.server
  (:require [example.infrastructure.middlewares.default-middlewares :as middlewares]
            [example.infrastructure.routes.items :refer [item-routes]]
            [malli.util :as mu]
            [muuntaja.core :as m]
            [reitit.coercion.malli]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]))

(def routes [["/" {:get {:handler (fn [_] {:status 200 :body "healthy"})}}]
             ["/api"
              item-routes]])

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
           :middleware middlewares/default-middlewares}}))

(defn handler [env db]
  (ring/ring-handler
   (router env db)
   (ring/routes
    (ring/create-default-handler))))