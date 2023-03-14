(ns web.commons.util.app)

(defn on-shutdown [f]
  (->> f Thread. (.addShutdownHook (Runtime/getRuntime))))
