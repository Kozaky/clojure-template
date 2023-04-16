(ns example.infrastructure.middlewares.default-middlewares
  (:require [example.infrastructure.middlewares.global-dependencies-middleware :refer [global-dependencies-middleware]]
            [example.infrastructure.middlewares.exception-handler-middleware :refer [exception-handler-middleware]]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]))

(def default-middlewares [;;global dependencies
                          global-dependencies-middleware
                          ;; query-params & form-params
                          parameters/parameters-middleware
                          ;; content-negotiation
                          muuntaja/format-negotiate-middleware
                          ;; encoding response body
                          muuntaja/format-response-middleware
                          ;; exception handling
                          exception-handler-middleware
                          ;; decoding request body
                          muuntaja/format-request-middleware])