(ns atmos-kernel.core
  (:require [clojure.string :refer [lower-case capitalize]]))

(defmacro defatmos-record-protocol
  [record-name type]
  (let [entity-lower-name# (lower-case record-name)
        protocol-name# (symbol (str "I" (name record-name) (name type)))
        fn-name# #(list
                    (symbol (str (name %1) "-" entity-lower-name#))
                    '[value])]
    `(defprotocol ~protocol-name#
       ~(fn-name# :add)
       ~(fn-name# :update)
       ~(fn-name# :remove)
       ~(fn-name# :get))))

