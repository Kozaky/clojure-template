(ns example.infrastructure.repositories.item-store
  (:require [clojure.set :refer [rename-keys]]
            [example.application.protocols.item-service :refer [ItemService]]
            [example.domain.entities.item :refer [map->Item]]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn ^:private row->item [row]
  (-> row
      (select-keys [:item/id :item/name])
      (rename-keys {:item/id :id :item/name :name})
      map->Item))

(defn ^:private item->row [item] item)

;; wrong, this method receives a partial item without id
(defn save [db item]
  (-> (into []
            (map #(row->item %))
            (as-> {:insert-into [:items] :values [(item->row item)]} $
              (sql/format $)
              (jdbc/plan db $ {:return-keys true})))
      first))

(defn fetch-by-id [db id]
  (-> (into []
            (map #(row->item %))
            (->> {:select [:*]
                  :from [:item]
                  :where [:= :id id]}
                 sql/format
                 (jdbc/plan db)))
      first))

(defn ^:private create-store-instance
  "Convenience function to create a new instance of the store"
  [db]
  (reify ItemService
    (save [_this item] (save db item))
    (fetch-by-id [_this id] (fetch-by-id db id))))

(def create-store (memoize create-store-instance))