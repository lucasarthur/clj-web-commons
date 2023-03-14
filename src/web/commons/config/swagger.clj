(ns web.commons.config.swagger
  (:require [environ.core :refer [env]]))

(def swagger-cfg-map
  {:path (or (env :swagger-path) "/api-docs")
   :swagger-docs (-> env :swagger-path (or "/api-docs") (str "/swagger.json"))})
