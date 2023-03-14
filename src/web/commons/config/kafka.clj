(ns web.commons.config.kafka
  (:require [environ.core :refer [env]]))

(def kafka-cfg-map {:brokers (env :kafka-brokers)})

(defn with-cfg [cfg]
  (merge kafka-cfg-map cfg))
