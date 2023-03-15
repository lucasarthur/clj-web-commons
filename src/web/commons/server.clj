(ns web.commons.server
  (:require
   [aleph.http :refer [start-server]]
   [aleph.netty :as netty]
   [web.commons.log :refer [init-loggers!]]
   [web.commons.config.metrics :refer [set-health-status!]]
   [web.commons.util.app :refer [set-server-shutdown-hook!]]))

(defonce server (atom nil))

(defn port []
  (netty/port @server))

(defn stop-server! []
  (when (not (nil? @server))
    (.close @server)
    (swap! server (constantly nil))))

(defn start-server!
  [handler
   {:keys [on-start on-shutdown]
    :or {on-start (constantly :no-op)
         on-shutdown (constantly :no-op)}
    :as options}]
  (init-loggers!)
  (swap! server (constantly (start-server handler (dissoc options :on-start :on-shutdown))))
  (set-health-status! :up)
  (on-start)
  (set-server-shutdown-hook! #(do (on-shutdown) (stop-server!))))
