(ns web.commons.middleware.exception
  (:require
   [web.commons.log :refer [log]]
   [cheshire.core :refer [generate-string]]
   [clojure.string :refer [split]])
  (:import [clojure.lang ExceptionInfo]))

(defn- exception->json [e]
  (-> (.toString e)
      (split #": " 2)
      (#(hash-map :error (first %) :message (second %)))
      (generate-string)))

(defn- exception-info->json [e]
  (->> e ex-message (hash-map :message) (generate-string)))

(defn- extract-status [e]
  (or (-> e ex-data :status) 500))

(defn wrap-exceptions [handler]
  (fn [req]
    (try (handler req)
         (catch ExceptionInfo e
           (log :error :exception e :data (ex-data e))
           {:status (extract-status e)
            :headers {"content-type" "application/json"}
            :body (exception-info->json e)})
         (catch Exception e
           (log :error :exception e)
           {:status 500
            :headers {"content-type" "application/json"}
            :body (exception->json e)}))))
