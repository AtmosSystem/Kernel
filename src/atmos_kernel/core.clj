(ns atmos-kernel.core
  (:require [clojure.spec.alpha :as s]
            [atmos-kernel.spec :refer :all]))

(defn keyword-map
  "Convert the keys of map (and subsequent maps) to clojure keyword."
  [data]
  (into {} (map (fn [[k v]] [(keyword k) (if (map? v)
                                           (keyword-map
                                             v) v)]) data)))

(s/fdef keyword-map
        :args (s/cat :data map?)
        :ret map?)

(defn nil-or-empty?
  "Check is the collection is nil or empty."
  [collection]
  (or (nil? collection) (empty? collection)))

(s/fdef nil-or-empty?
        :args (s/cat :collection (s/coll-of any?))
        :ret boolean?)

(defn in?
  "true if collection contains element."
  [collection element]
  (some #(= element %) collection))

(s/fdef in?
        :args (s/cat :collection (s/coll-of any?)
                     :element any?)
        :ret boolean?)

(defn throw-exception
  "Throw an exception."
  ([message data]
   (throw (ex-info message data)))
  ([message]
   (throw-exception message {})))

(s/fdef throw-exception
        :args (s/cat :message string? :data (s/? map?))
        :ret :atmos-kernel.spec/exception)
