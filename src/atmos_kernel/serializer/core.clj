(ns atmos-kernel.serializer.core
  (:require [atmos-kernel.core :refer [in? throw-exception]]
            [clojure.spec.alpha :as s])
  (:import (java.util List Map)))


(defn vectorize-map*
  "Create a vector from a data map using new keys map."
  [data-map field new-keys-map]
  (let [[key value] field]                                  ; field: [key value]
    (if-let [serialized-key (key new-keys-map)]             ; Trying to get new data key from new keys map.
      (if (vector? serialized-key)
        (let [[key value-data] serialized-key]              ; If value of keys map is a vector the first element is the new key.

          (vector key
                  (if (fn? value-data)
                    (value-data data-map)                   ; The value of the serialized-key could be a function, if so, apply the function against the data map.
                    value-data)))

        (vector serialized-key value))                      ; Vectorize the new key with the value of data map.

      (vector key value))))                                 ; If serializer map does not have the desired key, the result vector will be the same key and value of data map.

(s/fdef vectorize-map*
        :args (s/cat :data-map (s/map-of keyword? any?)
                     :field (s/tuple keyword? any?)
                     :new-keys-map (s/map-of keyword? (s/or :new-key-name keyword?
                                                            :new-key-name-and-transform-fn (s/tuple keyword? fn?)
                                                            :new-key-name-and-value (s/tuple keyword? any?))))
        :ret (s/tuple keyword? any?))


(defn de-vectorize-map*
  "Create a vector from a data map using new keys map."
  [field new-keys-map]
  (let [[key value] field                                   ; field: [key value].
        new-key (key new-keys-map)]                         ; Getting new key from new keys map.

    (if (map? new-key)                                      ; If the new key is a map the key element is the value of new key.
      (let [[new-key spec] (-> new-key vec first)]          ; Getting the spec from the key vector.

        (if (s/valid? spec value)                           ; Applying the spec against the value.
          (vector new-key value)                            ; If validation was success, vectorize the new key with the value.
          (throw-exception (s/explain-str spec value)
                           {:key key})))                    ; Throw an exception when the spec is not valid.

      (vector new-key value))))                             ; Vectorize the new key with value.

(s/fdef de-vectorize-map*
        :args (s/cat :field (s/tuple keyword? any?)
                     :new-keys-map (s/map-of keyword? (s/or :new-key-name keyword?
                                                            :new-key-name-with-spec (s/map-of keyword? fn?))))
        :ret (s/tuple keyword? any?))

(defprotocol EntitySerializationProtocol
  (serialize [data serializer-map] "Serialize data map using serializer map.")
  (de-serialize [data de-serialize-map] "De-serialize data map using a de-serializer map."))

(extend-protocol EntitySerializationProtocol
  nil
  (serialize [_ _] nil)
  (de-serialize [_ _] nil)
  Map
  (serialize [data serializer-map] (let [data (map (fn [field] (vectorize-map* data field serializer-map)) data)]
                                     (into {} data)))

  (de-serialize [data de-serializer-map] (let [{:keys [data-spec record-fn fields]} de-serializer-map]

                                           (if (s/valid? data-spec data)
                                             (let [data (map (fn [field] (de-vectorize-map* field fields)) data)]
                                               (record-fn data))
                                             (throw-exception (s/explain-str data-spec data)))))
  List
  (serialize [data serializer-map] (map (fn [record] (serialize record serializer-map)) data))
  (de-serialize [data de-serializer-map] (map (fn [record] (de-serialize record de-serializer-map)) data)))