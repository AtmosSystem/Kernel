(ns atmos-kernel.web.route
  (:require [clojure.string :refer [join]]
            [atmos-kernel.web.security.auth :refer [handle-request]]
            [atmos-kernel.web.response :refer [atmos-response]]
            [compojure.core :refer [GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]))

(def not-found-route (-> {} not-found atmos-response))

(defmacro atmos-route
  "Create an atmos compojure route"
  ([http-method authentication-needed? route-path args body]
   (let [route-path (let [route-path (join "/" route-path)]
                      (str "/" (if-not (empty? route-path) route-path)))
         request (if (vector? args) (last (conj args :as 'request)) 'request)]

     `(~http-method ~route-path ~args
        (atmos-response
          (handle-request ~request ~authentication-needed? ~body))))))

(defmacro atmos-GET
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? false}}]
  `(atmos-route GET ~authentication-needed? ~path ~args ~body))

(defmacro atmos-POST
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? false}}]
  `(atmos-route POST ~authentication-needed? ~path ~args ~body))

(defmacro atmos-PUT
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? false}}]
  `(atmos-route PUT ~authentication-needed? ~path ~args ~body))

(defmacro atmos-DELETE
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? false}}]
  `(atmos-route DELETE ~authentication-needed? ~path ~args ~body))

