(ns atmos-kernel.web.route
  (:require [clojure.string :refer [join]]
            [atmos-kernel.web.security.auth :refer [handle-request]]
            [compojure.core :refer [GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]))

(def not-found-route not-found)
(def not-implemented-route (let [data {:message "Not implemented method"}] (-> data not-found)))


(defmacro atmos-route
  "Create an atmos compojure route"
  ([http-method authentication-needed? route-path params body]
   (let [route-path (join "/" route-path)
         request (if (vector? params) (last (conj params :as 'request)) 'request)]
     `(~http-method ~route-path ~params
        (handle-request ~request ~authentication-needed? ~body)))))

(defmacro atmos-GET
  [path params body & {:keys [authentication-needed?]
                       :or   {authentication-needed? false}}]
  `(atmos-route GET ~authentication-needed? ~path ~params ~body))

(defmacro atmos-POST
  [path params body & {:keys [authentication-needed?]
                       :or   {authentication-needed? false}}]
  `(atmos-route POST ~authentication-needed? ~path ~params ~body))

(defmacro atmos-PUT
  [path params body & {:keys [authentication-needed?]
                       :or   {authentication-needed? false}}]
  `(atmos-route PUT ~authentication-needed? ~path ~params ~body))

(defmacro atmos-DELETE
  [path params body & {:keys [authentication-needed?]
                       :or   {authentication-needed? false}}]
  `(atmos-route DELETE ~authentication-needed? ~path ~params ~body))

