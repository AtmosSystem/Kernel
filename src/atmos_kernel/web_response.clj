(ns atmos-kernel.web-response
  (:require [atmos-kernel.web :refer [atmos-response]]))


(defmacro atmos-cond-response
  [& body]
  `(atmos-response (cond ~@body)))

(defmacro atmos-let-cond-response
  [bindings & body]
  `(let ~bindings (atmos-cond-response ~@body)))