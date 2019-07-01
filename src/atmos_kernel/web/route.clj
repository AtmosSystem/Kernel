(ns atmos-kernel.web.route
  (:require [clojure.string :refer [join lower-case]]
            [atmos-kernel.core :refer [log-data]]
            [atmos-kernel.web.security.auth :refer [handle-request]]
            [atmos-kernel.web.response :refer [atmos-response handle-exception]]
            [compojure.core :refer [GET POST PUT DELETE]]))


(defmacro atmos-route
  "Create an atmos compojure route"
  ([http-method authentication-needed? route-path args body]
   (let [route-path (let [route-path (join "/" route-path)]
                      (str "/" (if-not (empty? route-path) route-path)))
         request (if (vector? args) (last (conj args :as 'request)) 'request)]

     `(~http-method ~route-path ~args
        (try
          (handle-request ~request ~authentication-needed?
                          (try
                            (atmos-response ~body)

                            (catch Exception inner-exception#
                              (log-data :atmos-kernel :error inner-exception#
                                        (handle-exception inner-exception# ~request)))))

          (catch Exception external-exception#
            (log-data :atmos-kernel :warn external-exception#
                      (handle-exception external-exception# ~request))))))))

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

(defmacro atmos-main-route
  "Create the main route of web compojure application"
  ([ms-name]
   (let [ms-name (-> ms-name name lower-case)]
     `(atmos-GET [] ~'request (str "Welcome to " ~ms-name " micro-service")))))

