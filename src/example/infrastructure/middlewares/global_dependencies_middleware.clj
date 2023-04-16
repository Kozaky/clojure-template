(ns example.infrastructure.middlewares.global-dependencies-middleware)

(def global-dependencies-middleware
  "Middleware to inject global dependencies into the request so they can be used downstream"
  {:name ::config
   :compile (fn [{:keys [env db]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :env env :db db)))))})