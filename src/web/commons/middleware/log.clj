(ns web.commons.middleware.log
  (:require [web.commons.log :refer [log log-on-kafka]]))

(defn log-http-requests
  ([handler] (log-http-requests handler {}))
  ([handler {:keys [on-kafka?]
             :or {on-kafka? false}}]
   (fn [req]
     (let [event :http-request
           method (:request-method req)
           path (:uri req)
           remote-addr (:remote-addr req)]
       (if on-kafka?
         (log-on-kafka event :method method :path path :remote-addr remote-addr)
         (log event :method method :path path :remote-addr remote-addr)))
     (handler req))))
