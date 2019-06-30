(ns atmos-kernel.web.security.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [atmos-kernel.configuration :refer [read-edn]]))

(def basic-auth backends/basic)
(def session-auth backends/session)
(def token-auth backends/token)
(def jws-auth backends/jws)
(def jwe-auth backends/jwe)

(def cors-config (read-edn :cors))

(defmulti cors-access-control (fn [type-to-allow] type-to-allow))

(defmethod cors-access-control :origins [_] (map #(re-pattern %) (cors-config :origins)))
(defmethod cors-access-control :methods [_] (cors-config :methods))

(defprotocol AuthHandlerProtocol
  (get-authentication [request auth-data]))

(defn atmos-auth-backend
  [auth-backend]
  (if auth-backend
    (auth-backend {:realm  "ATMOS"
                   :authfn (fn [request auth-data]
                             (get-authentication request auth-data))})))


(defmacro handle-request
  [request authentication-needed? body]
  `(if ~authentication-needed?
     (if-not (authenticated? ~request)
       (throw-unauthorized)
       ~body)
     ~body))