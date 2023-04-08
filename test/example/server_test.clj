(ns example.server-test
  (:require [clojure.test :refer [deftest testing is]]
            [example.server :refer [handler]]
            [ring.mock.request :refer [request json-body]]))

(deftest example-server

  (testing "GET"
    (is (= (-> (request :get "/math/plus?x=20&y=3")
               (handler {}) :body slurp)
           (-> {:request-method :get :uri "/math/plus" :query-string "x=20&y=3"}
               (handler {}) :body slurp)
           (-> {:request-method :get :uri "/math/plus" :query-params {:x 20 :y 3}}
               (handler {}) :body slurp)
           "{\"total\":23}")))

  (testing "POST"
    (is (= (-> (request :post "/math/plus") (json-body {:x 40 :y 2})
               (handler {}) :body slurp)
           (-> {:request-method :post :uri "/math/plus" :body-params {:x 40 :y 2}}
               (handler {}) :body slurp)
           "{\"total\":42}")))

  (testing "Download"
    (is (= (-> {:request-method :get :uri "/files/download"}
               (handler {}) :body (#(slurp % :encoding "ascii")) count)  ;; binary
           (.length (clojure.java.io/file "resources/reitit.png"))
           506325)))

  (testing "Upload"
    (let [file (clojure.java.io/file "resources/reitit.png")
          multipart-temp-file-part {:tempfile file
                                    :size (.length file)
                                    :filename (.getName file)
                                    :content-type "image/png;"}]
      (is (= (-> {:request-method :post :uri "/files/upload" :multipart-params {:file multipart-temp-file-part}}
                 (handler {}) :body slurp)
             "{\"name\":\"reitit.png\",\"size\":506325}")))))
