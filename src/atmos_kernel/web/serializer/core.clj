(ns atmos-kernel.web.serializer.core
  (:require [atmos-kernel.web.serializer.coercions :refer :all]
            [atmos-kernel.core :refer [in?]])
  (:import (java.util List Map)))


(defprotocol FieldValueProtocol
  (field-value [this]))

(defprotocol FieldSerializationProtocol
  (response-name [this]))

(defprotocol FieldDeSerializationProtocol
  (has-option? [this option])
  (parse [this]))

(defprotocol EntitySerializationProtocol
  (serialize [this serializer-fn])
  (de-serialize [this de-serialize-fn]))

(extend-protocol EntitySerializationProtocol
  Map
  (serialize [this serialize-fn] (let [data-serialized (serialize-fn this)
                                       data-structure (keys data-serialized)
                                       data (map (fn [field]
                                                   (vector (response-name (data-serialized field))
                                                           (field-value (data-serialized field))))
                                                 data-structure)]

                                   (into {} data)))

  (de-serialize [this de-serialize-fn] (let [data-de-serialized (de-serialize-fn this)
                                             data-structure (keys data-de-serialized)
                                             data (map (fn [field]
                                                         (vector field
                                                                 (parse (data-de-serialized field))))
                                                       data-structure)]

                                         (into {} data)))

  List
  (serialize [data serialize-fn] (map (fn [record]
                                        (serialize record serialize-fn)) data))

  (de-serialize [data de-serialize-fn] (map (fn [record]
                                              (de-serialize record de-serialize-fn)) data)))


(defrecord SerializerField [value response-name]
  FieldValueProtocol
  (field-value [_] value)
  FieldSerializationProtocol
  (response-name [_] response-name))

(defrecord DeSerializerField [value class options]
  FieldValueProtocol
  (field-value [_] value)
  FieldDeSerializationProtocol
  (parse [_] (parse-data value class))
  (has-option? [_ option] (in? options option)))

(defn make-serializer-field
  [value response-name]
  (->SerializerField value (keyword response-name)))

(defn make-de-serializer-field
  ([value class options]
   (->DeSerializerField value class options))
  ([value class]
   (make-de-serializer-field value class [:required])))