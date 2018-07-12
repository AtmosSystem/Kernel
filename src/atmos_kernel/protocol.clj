(ns atmos-kernel.protocol
  (:require [clojure.string :refer [lower-case]]))

(defn protocol-name
  "Create compound protocol name"
  ([entity-name type]
   (let [entity-name (name entity-name)
         type (if-not (nil? type) (name type))]
     (symbol (str "I" entity-name type "Protocol"))))
  ([entity-name]
   (protocol-name entity-name nil)))

(defn protocol-function
  "Create protocol function name"
  ([entity-name type]
   (let [entity-name (lower-case (name entity-name))
         type (name type)]
     (list (symbol (str type "-" entity-name)) '[data])))
  ([function-name]
   (list (symbol function-name) '[data])))


(defmacro defatmos-seq-record-protocol
  "Define a protocol for use in an ISeq implementation"
  [record-name record-plural-name]
  `(defprotocol ~(protocol-name record-name :Seq)

     ~(protocol-function record-plural-name :get)
     ~(protocol-function record-plural-name :remove)))

(defmacro defatmos-record-protocols
  "Define multiple common protocols"
  [record-name]
  `[(defprotocol ~(protocol-name record-name :Basic)

      ~(protocol-function record-name :add)
      ~(protocol-function record-name :update))

    (defprotocol ~(protocol-name record-name :Identity)

      ~(protocol-function record-name :get)
      ~(protocol-function record-name :remove))])


(defmacro defatmos-record-protocol
  "Define record protocol"
  [record-name function-names]
  `(let [protocol-functions# (map protocol-function ~function-names)
         protocol-named# (cons (protocol-name ~record-name) protocol-functions#)
         protocol# (cons 'defprotocol protocol-named#)]
     (eval protocol#)))