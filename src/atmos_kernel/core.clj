(ns atmos-kernel.core)

(defn keyword-map
  "Convert the keys of map (and subsequent maps) to clojure keyword"
  [data]
  (into {} (map (fn [[k v]] [(keyword k) (if (map? v)
                                           (keyword-map
                                             v) v)]) data)))


(defn nil-or-empty?
  "Check is the coll is nil or empty"
  [coll]
  (or (nil? coll) (empty? coll)))