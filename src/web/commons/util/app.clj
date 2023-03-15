(ns web.commons.util.app
  (:require [clojure.repl :refer [set-break-handler!]]))

(defonce ^:private hooks (atom []))
(defonce ^:private server-shutdown-hook (atom (constantly :no-op)))
(defonce ^:private log-shutdown-hook (atom (constantly :no-op)))

(defn add-shutdown-hook! [hook]
  (swap! hooks conj hook))

(defn set-server-shutdown-hook! [hook]
  (swap! server-shutdown-hook (constantly hook)))

(defn set-log-shutdown-hook! [hook]
  (swap! log-shutdown-hook (constantly #(do
                                          (Thread/sleep 1000)  ;; wait for gracefully loggy termination of app
                                          (hook)))))

(defn- shutdown []
  (doseq [f @hooks] (f))
  (@server-shutdown-hook)
  (@log-shutdown-hook))

(def ^:private do-shutdown
  (do
    (set-break-handler! (fn [_] (shutdown)))
    (->> shutdown Thread. (.addShutdownHook (Runtime/getRuntime)))))
