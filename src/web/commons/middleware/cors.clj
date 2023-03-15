(ns web.commons.middleware.cors
  (:require
   [clojure.string :refer [join]]
   [web.commons.config.cors :refer [cors-cfg-map]]
   [manifold.deferred :refer [success-deferred chain' chain]]
   [simple-cors.core :refer [compile-cors val-at get-origin add-headers-to-response]]))

(defn- fix-vary-header [res]
  (if (get-in res [:headers "vary"])
    (update-in res [:headers "vary"] (constantly "Origin"))
    res))

(defn- fix-expose-header [res]
  (let [header-name "access-control-expose-headers"]
    (if (get-in res [:headers header-name])
      (update-in res [:headers header-name] #(join ", " %))
      res)))

(defn wrap-cors [handler]
  (let [{:keys [cors preflight-handler]} (compile-cors {:cors-config cors-cfg-map})]
    (fn [req]
      (if (identical? :options (:request-method req))
        (-> (success-deferred (preflight-handler req))
            (chain fix-vary-header))
        (let [request-origin (get-origin req)]
          (chain'
           (handler req)
           #(if-let [cors-handler (val-at cors request-origin)]
              (add-headers-to-response cors-handler % request-origin)
              %)
           fix-vary-header
           fix-expose-header))))))
