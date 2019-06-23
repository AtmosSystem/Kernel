(ns atmos-kernel.serializer.core
  (:require [atmos-kernel.serializer.coercions :refer :all]
            [atmos-kernel.core :refer [in?]])
  (:import (java.util List Map)))


(defrecord SerializerField [value response-name-value])
(defrecord DeSerializerField [value class options])

(defprotocol FieldValueProtocol
  (field-value [field]))

(defprotocol FieldSerializationProtocol
  (response-name [field]))

(defprotocol FieldDeSerializationProtocol
  (has-option? [this option])
  (parse [this]))

(defprotocol EntitySerializationProtocol
  (serialize [data serializer-fn])
  (de-serialize [data de-serialize-fn]))

(extend-protocol EntitySerializationProtocol
  nil
  (serialize [_ _] nil)
  (de-serialize [_ _] nil)
  Map
  (serialize [data serialize-fn] (let [data-serialized (if-not (nil? serialize-fn)
                                                         (serialize-fn data)
                                                         data)

                                       data-structure (keys data-serialized)
                                       data (map (fn [field]
                                                   (vector (response-name (data-serialized field))
                                                           (field-value (data-serialized field))))
                                                 data-structure)]

                                   (into {} data)))

  (de-serialize [data de-serialize-fn] (let [data-de-serialized (if-not (nil? de-serialize-fn)
                                                                  (de-serialize-fn data)
                                                                  data)

                                             data-structure (keys data-de-serialized)
                                             data (map (fn [field]
                                                         (let [data (field data-de-serialized)]
                                                           (vector field (if (record? data)
                                                                           (parse data)
                                                                           (de-serialize data nil)))))
                                                       data-structure)]

                                         (into {} data)))

  List
  (serialize [data serialize-fn] (map (fn [record]
                                        (serialize record serialize-fn)) data))

  (de-serialize [data de-serialize-fn] (map (fn [record]
                                              (de-serialize record de-serialize-fn)) data)))


(extend-protocol FieldValueProtocol
  Map
  (field-value [field] (:value field))
  SerializerField
  (field-value [field] (:value field))
  DeSerializerField
  (field-value [field] (:value field)))

(extend-protocol FieldSerializationProtocol
  Map
  (response-name [field] (:response-name-value field))
  SerializerField
  (response-name [field] (:response-name-value field)))

(extend-protocol FieldDeSerializationProtocol
  DeSerializerField
  (parse [field] (parse-data (:value field) (:class field)))
  (has-option? [field option] (in? (:options field) option))
  nil
  (parse [_] nil))

(defn serializer-field
  [value response-name]
  (->SerializerField value (keyword response-name)))

(defn de-serializer-field
  ([value class options]
   (->DeSerializerField value class options))
  ([value class]
   (de-serializer-field value class [:required]))
  ([value]
   (de-serializer-field value :string)))


(defn make-fields
  [record make-field-fn fields]
  (apply record (map
                  (fn
                    [field]
                    (let [property (first field)
                          property-metadata (second field)]
                      (make-field-fn property property-metadata)))
                  fields)))

(defn mapping [record] (into {} record))