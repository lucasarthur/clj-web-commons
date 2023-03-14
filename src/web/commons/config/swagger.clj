(ns web.commons.config.swagger
  (:require [environ.core :refer [env]]))

(def swagger-path (-> :swagger-path env (or "/api-docs")))

(def swagger-cfg-map
  {:path swagger-path
   :swagger-docs (str swagger-path "/swagger.json")})
