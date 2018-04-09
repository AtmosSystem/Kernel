(ns atmos-kernel.core)

(defn keyword-map
  "Convert the keys of map to clojure keyword"
  [data]
  (into {} (map (fn [[k v]] [(keyword k) v]) data)))