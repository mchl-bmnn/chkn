(ns chkn.parser.txt
  (:require
   [clj-time.core :as time]
   [clj-time.format :as time-format]
   ))

(defrecord FA [timestamp vin series istep build colour-code fabric-code time-criterion type-key sa-codes e-words])
(defrecord Unit [address component process-class sgbm-id version])

(defn ->token [matcher] (fn [string] (last (re-find matcher string))))

(defn parse-timestring [format timestring] (time-format/parse (time-format/formatter format) timestring))

(defn split [token] (into (sorted-set) (clojure.string/split token #"\s+")))

(defn- ->sa-codes [checkin] (->> checkin (re-seq #"(?:SA's\s+:\s+)(.+)") (map (comp split last)) flatten
                                 (apply clojure.set/union) (filter #(re-matches #"[A-Z\d]{3}" %)) (into (sorted-set))))

(defn- ->e-words [checkin] (->> checkin ((->token #"(?:E_Woerter\s+:\s+)([\w ]+)")) split))

(defn- ->timestamp [checkin]
  (parse-timestring "dd.MM.yyyy HH:mm:ss"
                    ((->token #"(?:Datum\s+:\s+)(\d{2}.\d{2}.\d{4} \d{2}:\d{2}:\d{2})") checkin)))

(defn ->fa [checkin]
  (apply ->FA ((juxt #(->timestamp %)
                           (->token #"(?:FGNR\s+:\s+)([\w]{7})")
                           (->token #"(?:Baureihe\s+:\s+)(\w{4})")
                           (->token #"(?:I-Stufe HO\s+:\s+)([\w]{4}-\d{2}-\d{2}-\d{3})")
                           (->token #"(?:I-Stufe Werk\s+:\s+)([\w]{4}-\d{2}-\d{2}-\d{3})")
                           (->token #"(?:Lackcode\s+:\s+)(\w{4})")
                           (->token #"(?:Polstercode\s+:\s+)(\w{4})")
                           (->token #"(?:FA-Auftrag\s+:\s+)(\d{4})")
                           (->token #"(?:FA-Auftrag\s+:\s+\d{4} )(\w{4})")
                           #(->sa-codes %)
                           #(->e-words %)) checkin)))

(def PROCESS_CLASSES {"0" "ERROR" "1" "HWEL" "2" "HWAP" "3" "HWFR" "4" "GWTB" "5" "CAFD" "6" "BTLD" "7" "FLSL" "8" "SWFL" "9" "SWFF" "10" "SWPF" "11" "ONPS" "12" "IBAD" "13" "SWFK" "15" "FAFP" "16" "FCFA" "26" "TLRT" "27" "TPRG" "28" "BLUP" "29" "FLUP" "160" "ENTD" "161" "NAVD" "162" "FCFN" "192" "SWUP" "193" "SWIP" "255" "-"})

(defn ->svt [checkin] (->> checkin
                           (re-seq #"(?im)^([A-F\d]{2})  ([\w/]+)  .+  (\d{1,3}) .+ ([\w]{8})  (\d{3}\.\d{3}\.\d{3}).+$")
                           (map #(apply ->Unit (rest %)))
                           (mapv #(-> % (update :address (partial str "0x")) (update :process-class (partial PROCESS_CLASSES))))
                           (group-by #(select-keys % #{:address :component}))
                           (mapv (fn [[k v]]
                                   (assoc k :units
                                          (mapv #(select-keys % #{:process-class :sgbm-id :version}) v))))
                           (sort-by :address)))

