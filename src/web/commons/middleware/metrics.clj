(ns web.commons.middleware.metrics
  (:require
   [web.commons.util.app :refer [add-shutdown-hook!]]
   [web.commons.config.metrics :refer [metrics-path health-path get-health-status set-health-status! health-statuses]]
   [web.commons.middleware.json :refer [wrap-json-response]]
   [iapetos.core :refer [collector-registry register histogram]]
   [iapetos.collector.jvm :as jvm]
   [iapetos.collector.ring :as ring]
   [compojure.core :refer [routes context GET]]))

(defn- health-handler [status _]
  {:status (:status status)
   :body {:message (:message status)}})

(defn- test-readiness [ready?]
  (if (ready?) (get-health-status) (:down health-statuses)))

(defn- json-health-handler [status]
  (->> status (partial health-handler) (wrap-json-response)))

(defn- health-checks [ready-check]
  (routes
   (context health-path []
     (GET "/" [] (json-health-handler (get-health-status)))
     (GET "/liveness" [] (json-health-handler (get-health-status)))
     (GET "/readiness" [] (->> (or ready-check (constantly true)) (test-readiness) (json-health-handler))))))

(defonce registry
  (-> (collector-registry)
      (register (histogram :app/duration-seconds))
      (jvm/initialize)
      (ring/initialize)))

(defn wrap-iapetos [handler]
  (ring/wrap-metrics handler registry {:path metrics-path}))

(defn wrap-health-checks [handler ready-check]
  (add-shutdown-hook! #(set-health-status! :down))
  (routes (health-checks ready-check) handler))

(defn wrap-metrics
  ([handler] (wrap-metrics handler nil))
  ([handler ready-check] (-> handler wrap-iapetos (wrap-health-checks ready-check))))
