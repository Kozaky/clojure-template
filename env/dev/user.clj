(ns user
  (:require [integrant.repl :as ig-repl]
            [ragtime.jdbc :as jdbc]
            [example.system :as system]))

(ig-repl/set-prep! (fn [] system/bootstrap))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))

(def config
  {:datastore  (jdbc/sql-database {:connection-uri "jdbc:postgresql://127.0.0.1/mydb?user=postgres&password=secret"})
   :migrations (jdbc/load-resources "migrations")})

(comment config)
