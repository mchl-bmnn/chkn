(ns chkn.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [base64-clj.core :as base64]
            [chkn.handler :refer :all]
            [cheshire.core :as cheshire]))

(deftest routes
  (testing "main"
    (let [payload (->> "Checkin" base64/encode (hash-map :checkin) cheshire/encode)
          response (handler (-> (mock/request :post "/" payload)
                                (mock/content-type "application/json")))]
      (is (= (:status response) 200))
      (is (= "Checkin" (->> response :body cheshire/decode clojure.walk/keywordize-keys :checkin)))))

  (testing "404"
    (let [response (handler (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
