(ns atmos-kernel.web.security.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(def basic-auth backends/basic)
(def session-auth backends/session)
(def token-auth backends/token)
(def jws-auth backends/jws)
(def jwe-auth backends/jwe)

(defprotocol IAuthHandlerProtocol
  (get-authentication [request auth-data]))


(defmulti get-authentication-type (fn [entity] (keyword entity)))

(defmulti get-realm-name (fn [auth-backend] auth-backend))
(defmethod get-realm-name :default [_] :ATMOS)

(defmulti get-token-name (fn [auth-backend] auth-backend))
(defmethod get-token-name :default [_] :Token)

(defn atmos-auth-backend
  [auth-backend]
  (if auth-backend
    (auth-backend {:realm      (-> auth-backend get-realm-name name)
                   :token-name (-> auth-backend get-token-name name)
                   :authfn     (fn [request auth-data]
                                 (get-authentication request auth-data))})))


(defmacro handler-request
  [request body]
  `(if-not (authenticated? ~request)
     (throw-unauthorized)
     ~body))