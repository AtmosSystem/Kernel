(ns atmos-kernel.configuration
  (:require [aero.core :refer [read-config]]
            [clojure.java.io :as io]))

(defn read-resource
  "Load a resource configuration"
  [file extension]
  (let [source (io/resource (str (name file) "." (name extension)))]
    (read-config source)))

(defn read-edn
  "Load a edn resource"
  [file]
  (read-resource file :edn))