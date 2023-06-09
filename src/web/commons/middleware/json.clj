(ns web.commons.middleware.json
  (:require
   [clojure.java.io :refer [reader]]
   [cheshire.core :refer [parse-stream generate-string]]))

(def ^:private json-headers {"content-type" "application/json"})

(defn- byte-stream->json [stream]
  (-> stream reader (parse-stream true)))

(defn- ->json-response [res]
  (-> (update res :body #(generate-string %))
      (update-in [:headers] #(merge % json-headers))))

(defn wrap-json-request [handler]
  (fn [req]
    (handler (assoc req :body (-> req :body byte-stream->json)))))

(defn wrap-json-response [handler-or-res]
  (if (fn? handler-or-res)
    (fn [req] (-> req handler-or-res ->json-response))
    (->json-response handler-or-res)))

(defn wrap-json [handler]
  (-> handler wrap-json-request wrap-json-response))
