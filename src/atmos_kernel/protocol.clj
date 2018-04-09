(ns atmos-kernel.protocol
  (:require [clojure.string :refer [lower-case]]))


(defmacro defatmos-seq-record-protocol
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