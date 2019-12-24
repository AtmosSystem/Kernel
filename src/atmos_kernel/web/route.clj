(ns atmos-kernel.web.route
  (:require [clojure.string :refer [join lower-case]]
            [clojure.tools.logging :as log]
            [atmos-kernel.web.security.auth :refer [handle-request]]
            [atmos-kernel.web.response :refer [atmos-response handle-exception]]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]))


(defmacro atmos-route
  "Create an atmos compojure route"
  ([http-method authentication-needed? route-path args body]
   (let [route-path (let [route-path (join "/" route-path)]
                      (str "/" (if-not (empty? route-path) route-path)))
         args (if (vector? args) (conj args :as 'request) args)
         request-obj (if (vector? args) (last args) 'request)]

     `(~http-method ~route-path ~args
        (try
          (handle-request ~request-obj ~authentication-needed?
                          (try
                            (atmos-response ~body)

                            (catch Exception inner-exception#
                              (log/error :error inner-exception# "Exception occurred in the body of request")
                              (handle-exception inner-exception# ~request-obj))))

          (catch Exception external-exception#
            (log/warn external-exception# "Exception occurred trying handle request")
            (handle-exception external-exception# ~request-obj)))))))

(def default-authentication-needed? (Boolean/parseBoolean
                                      (System/getProperty "atmos-kernel.web.route.authentication-needed" "true")))

(defmacro atmos-GET
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? default-authentication-needed?}}]
  `(atmos-route GET ~authentication-needed? ~path ~args ~body))

(defmacro atmos-POST
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? default-authentication-needed?}}]
  `(atmos-route POST ~authentication-needed? ~path ~args ~body))

(defmacro atmos-PUT
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? default-authentication-needed?}}]
  `(atmos-route PUT ~authentication-needed? ~path ~args ~body))

(defmacro atmos-DELETE
  [path args body & {:keys [authentication-needed?]
                     :or   {authentication-needed? default-authentication-needed?}}]
  `(atmos-route DELETE ~authentication-needed? ~path ~args ~body))

(defmacro atmos-main-route
  "Create the main route of web compojure application"
  ([ms-name]
   (let [ms-name (-> ms-name name lower-case)]
     `(atmos-GET [] ~'request (str "Welcome to " ~ms-name " micro-service")))))

(def #^{:macro true} defatmos-routes #'defroutes)

