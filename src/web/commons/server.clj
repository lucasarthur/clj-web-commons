(ns web.commons.server
  (:require
   [aleph.http :refer [start-server]]
   [aleph.netty :as netty]
   [web.commons.log :refer [init-loggers! stop-loggers! log]]
   [web.commons.config.metrics :refer [set-health-status!]]
   [web.commons.util.app :as app]))

(defn port [server]
  (netty/port server))

(defn start-server!
  [handler
   {:keys [on-shutdown]
    :or {on-shutdown (constantly :no-op)}
    :as options}]
  (init-loggers!)
  (let [server (start-server handler (dissoc options :on-shutdown))]
    (log ::server-started :port (port server))
    (set-health-status! :up)
    (app/on-shutdown
     #(do
        (log ::app-shutdown)
        (on-shutdown)
        (.close server)
        (stop-loggers!)))))
