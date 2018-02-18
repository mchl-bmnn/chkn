(defproject chkn "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.5.1"]
                 [cheshire "5.8.0"]
                 [base64-clj "0.1.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [clj-time "0.14.2"]
                 ]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler chkn.handler/handler :port 8080}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
