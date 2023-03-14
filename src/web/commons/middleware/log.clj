(ns web.commons.middleware.log
  (:require [web.commons.log :refer [log log-on-kafka]]))

(defn- -log [event-name & pairs]
  (log event-name pairs))

(defn- -log-on-kafka [event-name & pairs]
  (log-on-kafka event-name pairs))

(defn log-http-requests
  ([handler] (log-http-requests handler {}))
  ([handler {:keys [on-kafka?]
             :or {on-kafka? false}}]
   (fn [req]
     (-> (if on-kafka? -log-on-kafka -log)
         (apply [:http-request
                 :method (:request-method req)
                 :path (:uri req)
                 :remote-addr (:remote-addr req)]))
     (handler req))))
