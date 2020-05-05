(ns atmos-kernel.serializer.coercions
  (:require [atmos-kernel.core :refer [throw-exception]]
            [clojure.string :refer [includes? split]])
  (:import (java.util Date)
           (java.lang ClassCastException)))


(defn as-int
  "Parse a string into an long."
  [data]
  (if-not (nil? data)
    (if-not (instance? Number data)
      (Long/parseLong data)
      data)))

(defn as-float
  "Parse a string into an float."
  [data]
  (if-not (nil? data)
    (if-not (instance? Float data)
      (Float/parseFloat data)
      data)))

(defn as-double
  "Parse a string into an double."
  [data]
  (if-not (nil? data)
    (if-not (instance? Double data)
      (Double/parseDouble data)
      data)))

(defn as-boolean
  "Parse a string into an boolean."
  [data]
  (if-not (nil? data)
    (if-not (instance? Boolean data)
      (Boolean/parseBoolean data)
      data)))

(defn as-date
  "Parse a string into a date."
  [data]
  (if-not (nil? data)
    (Date. (Date/parse data))))

(defn as-time
  "Parse a string into a time."
  [data]
  (if (includes? data ":")
    (let [[hours minutes seconds] (split data #":")]
      {:hours   (as-int hours)
       :minutes (as-int minutes)
       :seconds (if-not (nil? seconds)
                  (as-int seconds)
                  0)})
    (throw (ClassCastException. "Error trying parsing time"))))


(def ^:private parsers {:number as-int
                        :date   as-date
                        :time   as-time
                        :bool   as-boolean
                        :double as-double
                        :float  as-float})

(defn parse-data
  "Parse data to a class using a existent parser."
  [data class]
  (let [parse-fn (parsers class)]

    (if-not (= class :string)
      (try
        (parse-fn data)

        (catch Exception _
          (throw-exception "Parsing error"
                           (str "Trying to parse '" data "' value to " (name class)))))
      data)))