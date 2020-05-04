(ns atmos-kernel.configuration
  (:require [aero.core :refer [read-config]]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

(defn read-resource
  "Load a resource configuration"
  [file extension]
  (let [source (io/resource (str (name file) "." (name extension)))]
    (read-config source)))

(s/fdef read-resource
        :args (s/and (s/cat :file :atmos-kernel.spec/non-blank-string :extension :atmos-kernel.spec/non-blank-string))
        :ret :atmos-kernel.spec/non-empty-map)

(defn read-edn
  "Load an edn resource"
  [file]
  (read-resource file :edn))

(s/fdef read-edn
        :args (s/cat :file :atmos-kernel.spec/non-blank-string)
        :ret :atmos-kernel.spec/non-empty-map)