(ns chkn.handler
  (:require
   [compojure.core :as compojure :refer [defroutes]]
   [compojure.route :as route]
   [cheshire.generate :as encoder]
   [ring.middleware.defaults :as defaults]
   [ring.middleware.json :refer [wrap-json-body]]
   [base64-clj.core :as base64]
   [clojure.spec.alpha :as s]
   [chkn.parser :as parser]
   [cheshire.core :as cheshire]
   [ring.middleware.json :refer [wrap-json-body]]))

(encoder/add-encoder org.joda.time.DateTime
                     (fn [c jsonGenerator]
                       (.writeString jsonGenerator (str c))))

(defroutes endpoints
  (compojure/POST "/" []
    (fn [r]
      (let [{{c :checkin f :format} :body} r]
        (some->> c base64/decode (parser/parse f) cheshire/encode)))))

(s/def ::request-method (partial = :post))
(s/def ::content-type (s/and string? #(clojure.string/includes? % "application/json") ))
(s/def ::checkin string?)
(s/def ::format #{"txt"})
(s/def ::body (s/keys :req-un [::format ::checkin]))
(s/def ::uri (partial = "/"))
(s/def ::request (s/keys :req-un [::uri ::request-method ::content-type ::body ]))

(defn valid? [handler]
  (fn [request]
    (if (s/valid? ::request request) (handler request)
        {:status 400
         :headers {"Content-Type" "text/plain"}
         :body (s/explain-str ::request request)
         })))

(def handler
  (-> endpoints
      (valid?)
      (defaults/wrap-defaults defaults/api-defaults)
      (wrap-json-body {:keywords? true})))
