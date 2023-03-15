(ns web.commons.config.cors
  (:refer-clojure :exclude [replace])
  (:require
   [clojure.string :refer [lower-case split ends-with? replace blank? join]]
   [environ.core :refer [env]]))

(def ^:private ports-pattern (re-pattern "(.*):\\[(\\*|\\d+(,\\d+)*)]"))
(def ^:private range-ports-pattern (re-pattern "(.*):\\[(\\*|\\d+(-\\d+)*)]"))

(defn- str->kws [s]
  (->> (split s #",") (map lower-case) (map keyword) (reduce conj [])))

(defn- str->vec
  ([s] (str->vec s ","))
  ([s sep] (->> sep (re-pattern) (split s) (reduce conj []))))

(defn- gen-port-range-pattern [start end]
  (->> (range (parse-long start) (-> end parse-long inc))
       (join "|")
       (#(str ":(" % ")"))))

(defn- apply-port-pattern [value]
  (if-some [[_ pattern-value port-list] (re-matches ports-pattern value)]
    {:value pattern-value
     :port (if (= "*" port-list)
             "(:\\d+)?"
             (str ":(" (replace port-list "," "|") ")"))}
    (if-some [[_ pattern-value port-range] (re-matches range-ports-pattern value)]
      {:value pattern-value
       :port (-> (split port-range #"-")
                 (#(gen-port-range-pattern (first %) (second %))))}
      {:value value
       :port "(:\\d+)?"})))

(defn- apply-origin-pattern [value]
  (-> (apply-port-pattern value)
      (update :value #(-> (str "\\Q" % "\\E")
                          (replace "*" "\\E.*\\Q")))
      (->> (map val) (reduce str) (re-pattern))))

(defn- trim-trailing-slash [origin]
  (if (ends-with? origin "/") (subs origin 0 (dec (count origin))) origin))

(defn- check-origin [origin]
  (and
   (not (blank? origin))
   (->> (str->vec (env :cors-allowed-origins) ";")
        (map #(-> % trim-trailing-slash apply-origin-pattern))
        (filter #(re-matches % (trim-trailing-slash origin)))
        (seq))))

(def cors-cfg-map {:allowed-request-methods (-> env :cors-allowed-methods str->kws)
                   :allowed-request-headers (-> env :cors-allowed-headers str->vec)
                   :exposed-headers (-> env :cors-exposed-headers str->vec)
                   :allow-credentials? (-> env :cors-allow-credentials parse-boolean)
                   :origins check-origin
                   :max-age (-> env :cors-max-age parse-long)})
