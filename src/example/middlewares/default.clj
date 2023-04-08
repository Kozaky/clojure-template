(ns example.middlewares.default
  (:require [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]))

(def global-config {:name ::config
                    :compile (fn [{:keys [env db]} _]
                               (fn [handler]
                                 (fn [req]
                                   (handler (assoc req :env env :db db)))))})

(def default-middlewares [;; swagger feature 
                          swagger/swagger-feature 
                          ;; query-params & form-params
                          parameters/parameters-middleware 
                          ;; content-negotiation
                          muuntaja/format-negotiate-middleware 
                          ;; encoding response body
                          muuntaja/format-response-middleware 
                          ;; exception handling
                          exception/exception-middleware
                          ;; decoding request body
                          muuntaja/format-request-middleware
                          ;; coercing response bodys
                          coercion/coerce-response-middleware
                          ;; coercing request parameters
                          coercion/coerce-request-middleware
                          ;; multipart
                          multipart/multipart-middleware
                          ;; injects global configuration
                          global-config])