(ns example.routes.demo-routes
  (:require [clojure.java.io :as io]
            [reitit.ring.malli :as malli]))

(def demo-routes [["/files"
                   {:swagger {:tags ["files"]}}

                   ["/upload"
                    {:post {:summary "upload a file"
                            :parameters {:multipart {:file malli/temp-file-part}}
                            :responses {200 {:body {:name :string, :size :int}}}
                            :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                                       {:status 200
                                        :body {:name (:filename file)
                                               :size (:size file)}})}}]

                   ["/download"
                    {:get {:summary "downloads a file"
                           :swagger {:produces ["image/png"]}
                           :handler (fn [_]
                                      {:status 200
                                       :headers {"Content-Type" "image/png"}
                                       :body (-> "reitit.png"
                                                 (io/resource)
                                                 (io/input-stream))})}}]]
                  ["/math"
                   {:swagger {:tags ["math"]}}

                   ["/plus"
                    {:get {:summary "plus with malli query parameters"
                           :parameters {:query {:x [:int {:title "X parameter"
                                                          :description "Description for X parameter"
                                                          :json-schema/default 42}]
                                                :y :int}}
                           :responses {200 {:body {:total :int}}}
                           :handler (fn [{{{:keys [x y]} :query} :parameters}]
                                      {:status 200
                                       :body {:total (+ x y)}})}
                     :post {:summary "plus with malli body parameters"
                            :parameters {:body {:x [:int {:title "X parameter"
                                                          :description "Description for X parameter"
                                                          :json-schema/default 42}]
                                                :y :int}}
                            :responses {200 {:body {:total :int}}}
                            :handler (fn [{{{:keys [x y]} :body} :parameters}]
                                       {:status 200
                                        :body {:total (+ x y)}})}}]]])