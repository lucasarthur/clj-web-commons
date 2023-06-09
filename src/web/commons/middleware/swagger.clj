(ns web.commons.middleware.swagger
  (:require
   [web.commons.config.swagger :refer [swagger-cfg-map]]
   [web.commons.middleware.json :refer [wrap-json-response]]
   [compojure.core :refer [routes GET]]
   [ring.swagger.swagger-ui :as ui]
   [ring.swagger.swagger2 :as rs]))

(defn- swagger-json-handler [swagger _]
  {:status 200 :body swagger})

(defn- swagger-json [swagger]
  (routes
   (GET (:swagger-docs swagger-cfg-map) []
     (->> swagger rs/swagger-json (partial swagger-json-handler) (wrap-json-response)))))

(defn wrap-swagger-ui [handler]
  (ui/wrap-swagger-ui handler swagger-cfg-map))

(defn wrap-swagger-json [handler swagger]
  (routes (swagger-json swagger) handler))

(defn wrap-swagger
  ([handler] (wrap-swagger handler {}))
  ([handler swagger] (-> handler wrap-swagger-ui (wrap-swagger-json swagger))))
