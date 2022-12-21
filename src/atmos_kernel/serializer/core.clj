(ns atmos-kernel.serializer.core
  (:require [atmos-kernel.core :refer [throw-exception]]
            [clojure.spec.alpha :as s])
  (:import (java.util List Map)))


(s/def ::transformation-functions (s/map-of keyword? fn?))
(s/def ::data-spec keyword?)
(s/def ::serializer-map ::transformation-functions)
(s/def ::fields ::transformation-functions)
(s/def ::de-serializer-map (s/keys :req-un [::data-spec]
                                   :opt-un [::fields]))

(defprotocol EntitySerializationProtocol
  (serialize [data serializer-map] "Serialize data map using serializer map.")
  (de-serialize [data de-serialize-map] "De-serialize data map using a de-serializer map."))


(defn vectorize-map*
  "Create a vector from a data map using new keys map field."
  [data-map field]
  (let [[new-key transform-fn] field]
    (vector new-key
            (if (fn? transform-fn)
              (transform-fn data-map)))))                   ; Vectorize the new key with the value of data map.

(s/fdef vectorize-map*
        :args (s/cat :data-map (s/map-of keyword? any?)
                     :field (s/cat :new-key keyword? :transform-fn fn?))
        :ret (s/tuple keyword? any?))

(defn is-valid-serializer-map?
  [serializer-map]
  (if-not (s/valid? ::serializer-map serializer-map) (s/explain-str ::serializer-map serializer-map) true))

(defn de-vectorize-map*
  "Create a vector from a data map using new keys map field."
  [data-map field]
  (let [[new-key transform-fn] field]
    (vector new-key
            (if (fn? transform-fn)
              (transform-fn data-map)))))                   ; Return the value from data map applying the transform fn or using keyword.


(s/fdef de-vectorize-map*
        :args (s/cat :data-map (s/map-of keyword? any?)
                     :field (s/cat :new-key keyword? :transform-fn fn?))
        :ret (s/tuple keyword? any?))

(defn is-valid-de-serializer-map?
  [de-serializer-map]
  (if-not (s/valid? ::de-serializer-map de-serializer-map) (s/explain-str ::de-serializer-map de-serializer-map) true))

(extend-protocol EntitySerializationProtocol
  nil
  (serialize [_ _] nil)
  (de-serialize [_ _] nil)
  Map
  (serialize [data serializer-map] (let [result-data (map (fn [field] (vectorize-map* data field)) serializer-map)]
                                     (merge data (into {} result-data))))

  (de-serialize [data de-serializer-map] (let [{:keys [data-spec fields] :or {data-spec false fields false}} de-serializer-map
                                               result-data (if fields (map (fn [field] (de-vectorize-map* data field)) fields))
                                               data (if result-data (merge data (into {} result-data)) data)]

                                           (if (and data-spec (is-valid-de-serializer-map? de-serializer-map))
                                             (if (s/valid? data-spec data)
                                               data
                                               (throw-exception (s/explain-str data-spec data)))
                                             data)))
  List
  (serialize [data serializer-map] (map (fn [record] (serialize record serializer-map)) data))
  (de-serialize [data de-serializer-map] (map (fn [record] (de-serialize record de-serializer-map)) data)))