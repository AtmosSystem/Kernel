(ns atmos-kernel.serializer.core
  (:require [atmos-kernel.core :refer [in? throw-exception]]
            [clojure.spec.alpha :as s])
  (:import (java.util List Map)))

(defprotocol EntitySerializationProtocol
  (serialize [data serializer-map] "Serialize data map using serializer map.")
  (de-serialize [data de-serialize-map] "De-serialize data map using a de-serializer map."))

(defn vectorize-map*
  "Create a vector from a data map using new keys map field."
  [data-map field]
  (let [[new-key no-serialized-data-key-or-fn] field]
    (vector new-key
            (if (or (keyword? no-serialized-data-key-or-fn) (fn? no-serialized-data-key-or-fn))
              (no-serialized-data-key-or-fn data-map)))))   ; Vectorize the new key with the value of data map.

(s/fdef vectorize-map*
        :args (s/cat :data-map (s/map-of keyword? any?)
                     :field (s/tuple keyword? (s/or :no-serialized-data-key keyword?
                                                    :transform-fn fn?)))
        :ret (s/tuple keyword? any?))

(defn de-vectorize-map*
  "Create a vector from a data map using new keys map field."
  [data-map field]
  (let [[new-key serialized-data-key-or-with-spec] field]
    (vector new-key
            (cond
              (or (keyword? serialized-data-key-or-with-spec) (fn? serialized-data-key-or-with-spec))
              (serialized-data-key-or-with-spec data-map)   ; Return the value from data map applying the transform fn or using keyword.

              (map? serialized-data-key-or-with-spec)
              (let [[serialized-data-key data-spec] (-> serialized-data-key-or-with-spec vec first) ; Getting the data map key and spec.
                    data-map-value (serialized-data-key data-map)]

                (if (s/valid? data-spec data-map-value)     ; Applying the spec against the value.
                  data-map-value                            ; If validation was success, return the data map value.
                  (throw-exception (s/explain-str data-spec data-map-value)
                                   {:key serialized-data-key}))))))) ; Throw an exception when the spec is not valid.

(s/fdef de-vectorize-map*
        :args (s/cat :data-map (s/map-of keyword? any?)
                     :field (s/map-of keyword? (s/or :serialized-data-key keyword?
                                                     :transform-fn fn?
                                                     :serialized-data-key-with-spec (s/map-of keyword? fn?))))
        :ret (s/tuple keyword? any?))

(extend-protocol EntitySerializationProtocol
  nil
  (serialize [_ _] nil)
  (de-serialize [_ _] nil)
  Map
  (serialize [data serializer-map] (let [data (map (fn [field] (vectorize-map* data field)) serializer-map)]
                                     (into {} data)))

  (de-serialize [data de-serializer-map] (let [{:keys [data-spec fields] :or {data-spec false}} de-serializer-map
                                               result-data (map (fn [field] (de-vectorize-map* data field)) fields)
                                               result-data-map (into {} result-data)]

                                           (if data-spec
                                             (if (s/valid? data-spec data)
                                               result-data-map
                                               (throw-exception (s/explain-str data-spec data)))
                                             result-data-map)))
  List
  (serialize [data serializer-map] (map (fn [record] (serialize record serializer-map)) data))
  (de-serialize [data de-serializer-map] (map (fn [record] (de-serialize record de-serializer-map)) data)))