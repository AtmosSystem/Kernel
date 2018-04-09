(ns atmos-kernel.configuration
  (:require [aero.core :refer [read-config]]))

(defn read-resource-edn
  [file]
  (read-config (str "resources/" (name file) ".edn")))
