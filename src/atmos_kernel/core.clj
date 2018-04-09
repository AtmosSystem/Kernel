(ns atmos-kernel.core)

(defn keyword-map
  [data]
  (into {} (map (fn [[k v]] [(keyword k) v]) data)))