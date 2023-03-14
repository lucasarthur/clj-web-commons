(ns web.commons.kafka
  (:require
   [web.commons.config.kafka :refer [with-cfg]]
   [clojure.core.async :refer [chan close!]]
   [ketu.async.source :refer [source stop!]]
   [ketu.async.sink :refer [sink]]
   [manifold.stream :refer [->source connect stream]]))

(defn create-consumer [options]
  (let [channel (chan)
        stream (->source channel)
        consumer (source channel (with-cfg options))]
    {:consumer consumer
     :channel channel
     :stream stream
     :stop! #(stop! consumer)}))

(defn create-producer [options]
  (let [buffer-size (-> options :channel-size (or 64))
        channel (chan buffer-size)
        stream (stream buffer-size)
        producer (sink channel (-> options (dissoc :channel-size) with-cfg))]
    (connect stream channel)
    {:producer producer
     :channel channel
     :sink stream
     :stop! #(close! channel)}))
