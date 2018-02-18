(ns chkn.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [base64-clj.core :as base64]
            [clojure.java.io :as io]
            [chkn.handler :refer :all]
            [cheshire.core :as cheshire]))


(def request
    (-> (mock/request :post "/" )
        (mock/content-type "application/json"))
  )

(deftest routes
  (testing "main"
    (let [payload (->> "./mocks/checkin.txt" slurp base64/encode (hash-map :format "txt" :checkin) cheshire/encode)
          response (handler (assoc request :body  (io/input-stream (.getBytes payload))))]
      (is (= (:status response) 200))
      (is (map? (->> response :body cheshire/decode clojure.walk/keywordize-keys)))
      )
    )
  )

(deftest client-errors
  (testing "Wrong method"
    (let [response (handler (assoc request :request-method :get))]
      (is (= 400 (:status response))))
    )
  (testing "Missing body"
    (let [response (handler (dissoc request :body))]
      (is (= 400 (:status response)))
      )
    )
  (testing "Wrong URI"
    (let [response (handler (assoc request :uri "//"))]
      (is (= 400 (:status response)))
      )
    )
  (testing "Malformed JSON in body"
    (let [response (handler (assoc request :body  (io/input-stream (.getBytes "{"))))
          ]
      (is (= 400 (:status response)))
      )
    )
  (testing "Missing `checkin` field in body"
    (let [response (handler (assoc request :body  (io/input-stream (.getBytes "{}"))))

          ]
      (is (= 400 (:status response)))
      )
    )
  )
