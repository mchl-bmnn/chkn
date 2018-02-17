(ns chkn.handler
  (:require
   [compojure.core :as compojure :refer [defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :as defaults]
            [ring.middleware.json :refer [wrap-json-body]]
            [base64-clj.core :as base64]
            [cheshire.core :as cheshire]
            [ring.middleware.json :refer [wrap-json-body]]
            ))

(defroutes endpoints
  (compojure/POST "/" request (let [{{checkin :checkin} :body} request]
                                (->> {:checkin (base64/decode checkin)} cheshire/encode) 
                                ))
  (route/not-found "Not Found"))

(def handler 
  (-> endpoints (defaults/wrap-defaults defaults/api-defaults)
      (wrap-json-body {:keywords? true})
      ))
