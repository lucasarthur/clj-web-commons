(ns web.commons.config.metrics
  (:require [environ.core :refer [env]]))

(def health-statuses {:up   {:status 200 :message "up"}
                      :down {:status 503 :message "down"}})
(def metrics-path
  (-> :metrics-path env (or "/metrics")))

(def health-path
  (str metrics-path "/health"))

(defonce health-status (atom (:down health-statuses)))

(defn get-health-status []
  @health-status)

(defn set-health-status! [status]
  (swap! health-status (constantly (status health-statuses))))
