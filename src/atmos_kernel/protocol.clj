(ns atmos-kernel.protocol
  (:require [clojure.string :refer [lower-case]]))


(defmacro defatmos-seq-record-protocol
  "Define a protocol for use in an ISeq implementation"
  [record-name record-plural-name type]
  (let [entity-name (name record-name)
        protocol-name# (symbol (str "I" entity-name "Seq" (name type)))
        fn-name# #(list
                    (symbol (str (name %1) "-" (lower-case (name record-plural-name))))
                    '[value])]
    `(defprotocol ~protocol-name#
       ~(fn-name# :get)
       ~(fn-name# :remove))))

(defmacro defatmos-record-protocols
  "Define multiple common protocols"
  [record-name type]
  (let [entity-name (name record-name)
        entity-lower-name (lower-case entity-name)
        protocol-name# #(symbol (str "I" entity-name (name %1) (name type)))
        fn-name# #(list
                    (symbol (str (name %1) "-" entity-lower-name))
                    '[value])]
    `[(defprotocol ~(protocol-name# :Basic)
        ~(fn-name# :add)
        ~(fn-name# :update))

      (defprotocol ~(protocol-name# :Identity)
        ~(fn-name# :get)
        ~(fn-name# :remove))]))