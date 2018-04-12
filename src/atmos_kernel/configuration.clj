(ns atmos-kernel.configuration
  (:require [aero.core :refer [read-config]]))

(def ^:private resource-dir "resources")

(defn read-resource
  "Load a resource configuration"
  [file extension]
  (read-config (str resource-dir "/" (name file) "." (name extension))))

(defn read-edn
  "Load a edn resource"
  [file]
  (read-resource file :edn))