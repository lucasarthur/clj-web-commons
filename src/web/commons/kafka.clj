(ns web.commons.kafka
  (:require
   [web.commons.util.app :refer [on-shutdown]]
   [web.commons.config.kafka :refer [with-cfg]]
   [clojure.core.async :refer [chan close!]]
   [ketu.async.source :refer [source stop!]]
   [ketu.async.sink :refer [sink]]
   [manifold.stream :refer [->source connect stream]]))

(defn create-consumer [options]
  (let [channel (chan)
        stream (->source channel)
        consumer (source channel (with-cfg options))
        stop-fn #(stop! consumer)]
    (on-shutdown stop-fn)
    {:consumer consumer
     :channel channel
     :stream stream
     :stop! stop-fn}))

(defn create-producer [options]
  (let [buffer-size (-> options :channel-size (or 64))
        channel (chan buffer-size)
        stream (stream buffer-size)
        producer (sink channel (-> options (dissoc :channel-size) with-cfg))
        stop-fn #(close! channel)]
    (connect stream channel)
    (on-shutdown stop-fn)
    {:producer producer
     :channel channel
     :sink stream
     :stop! stop-fn}))
