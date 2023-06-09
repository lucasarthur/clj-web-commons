(ns web.commons.log
  (:require
   [web.commons.util.app :refer [set-log-shutdown-hook!]]
   [com.brunobonacci.mulog.core :refer [log* *default-logger*]]
   [com.brunobonacci.mulog.buffer :refer [ring-buffer]]
   [com.brunobonacci.mulog :as u :refer [start-publisher!]]
   [org.slf4j.impl.mulog :refer [set-level!]]
   [environ.core :refer [env]])
  (:import [org.slf4j LoggerFactory]))

(defonce ^:private stoppers (atom ()))
(defonce ^:dynamic *kafka-logger* (atom (ring-buffer 1000)))

(defn- add-stoppers! [& -stoppers]
  (swap! stoppers #(into % -stoppers)))

(defn stop-loggers! []
  (doseq [s @stoppers] (s)))

(defmacro log [event-name & pairs]
  `(log* *default-logger* ~event-name (list :mulog/namespace ~(str *ns*) ~@pairs)))

(defmacro log-on-kafka [event-name & pairs]
  `(do
     (log* *kafka-logger* ~event-name (list :mulog/namespace ~(str *ns*) ~@pairs))
     (log ~event-name ~@pairs)))

(defn slf4j-logger
  ([] (slf4j-logger (str *ns*)))
  ([log-ns] (LoggerFactory/getLogger log-ns)))

(defn init-loggers!
  ([] (init-loggers! (-> env :log-level keyword)))
  ([level]
   (add-stoppers!
    (start-publisher! {:type :console :pretty? true})
    (start-publisher! *kafka-logger*
                      {:type :kafka
                       :kafka {:bootstrap.servers (env :kafka-brokers)}
                       :topic (env :kafka-logging-topic)}))
   (set-log-shutdown-hook! stop-loggers!)
   (set-level! level)))
