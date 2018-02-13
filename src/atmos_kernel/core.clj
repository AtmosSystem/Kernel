(ns atmos-kernel.core
  (:require [clojure.string :refer [lower-case capitalize]]))

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


