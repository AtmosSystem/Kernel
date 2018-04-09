(ns atmos-kernel.web.response
  (:require [atmos-kernel.web.core :refer [atmos-response]]))


(defmacro atmos-cond-response
  "Create a atmos response with cond"
  [& body]
  `(atmos-response (cond ~@body)))

(defmacro atmos-let-cond-response
  "Create a atmos cond response with let and use the bindings inside"
  [bindings & body]
  `(let ~bindings (atmos-cond-response ~@body)))