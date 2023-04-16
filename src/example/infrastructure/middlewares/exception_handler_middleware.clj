(ns example.infrastructure.middlewares.exception-handler-middleware
  (:require [io.pedestal.log :as log]
            [reitit.ring.middleware.exception :as exception]))

(defn handler [message exception request]
  {:status 500
   :body {:message message
          :exception (.getClass exception)
          :data (ex-data exception)
          :uri (:uri request)}})

(def exception-handler-middleware
  (exception/create-exception-middleware
   (-> {::exception/default (partial handler "Internal Server Error")
        ::exception/wrap (fn [handler e request]
                           (log/error :error e)
                           (handler e request))}
       (merge exception/default-handlers))))