(ns atmos-kernel.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::non-nil (complement nil?))

(s/def ::non-blank-string (s/and string? (complement str/blank?)))

(s/def ::non-empty-map (s/and map? (complement empty?)))