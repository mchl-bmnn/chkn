(ns chkn.parser
  (:require
   [clojure.core.async :as async]
   [chkn.parser.txt :as txt]))

(defmulti ->fa (fn [_ __] _))
(defmethod ->fa "txt" [_ content] (txt/->fa content))

(defmulti ->svt (fn [_ __] _))
(defmethod ->svt "txt" [_ content] (txt/->svt content))

(defn parse [format content]
  (let [;f (async/thread (->fa format content))
        ;s (async/thread (->svt format content))
]
    {:fa (txt/->fa content) :svt (txt/->svt content)}))

;(parse "txt" (slurp "./checkin.txt"))
