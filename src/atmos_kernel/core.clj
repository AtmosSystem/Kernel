(ns atmos-kernel.core
  (:import (org.slf4j LoggerFactory)))

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


(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn throw-exception
  "Throw an exception"
  [message cause]
  (throw (ex-info message {:cause cause})))


(defn log
  [logger-name log-type log-data]
  (let [logger (LoggerFactory/getLogger (name logger-name))
        log-type (keyword log-type)
        log-data (str log-data)]
    (case log-type
      :info (.info logger log-data)
      :debug (.debug logger log-data)
      :trace (.trace logger log-data)
      :warn (.warn logger log-data)
      :error (.error logger log-data)
      logger)))

(defmacro log-data
  [logger-name log-type data & body]
  `(do
     (log ~logger-name ~log-type ~data)
     ~@body))