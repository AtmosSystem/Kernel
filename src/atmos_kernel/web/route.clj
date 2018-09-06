(ns atmos-kernel.web.route
  (:require [clojure.string :refer [lower-case join]]
            [atmos-kernel.web.core :refer [atmos-response]]
            [atmos-kernel.web.security.auth :refer [atmos-authenticated?
                                                    atmos-unauthorized]]
            [compojure.core :refer [GET POST PUT DELETE defroutes]]
            [compojure.route :refer [not-found]]))


(def not-found-route (-> {} not-found atmos-response))
(def not-implemented-route (let [data {:message "Not implemented method"}] (-> data not-found atmos-response)))


(defmacro atmos-route
  "Create an atmos compojure route"
  ([http-method authentication? route-path body]
   (let [route-params (vec (map #(symbol (name %)) (filter keyword? route-path)))
         route-path (let [route-path (join "/" route-path)]
                      (str "/" (if-not (empty? route-path) route-path)))
         route-params (if (seq route-params) (conj route-params :as 'request) 'request)]
     `(~http-method ~route-path
        ~route-params
        (atmos-response
          (if ~authentication?
            (if-not (atmos-authenticated? (last ~route-params))
              (atmos-unauthorized)
              ~body)
            ~body))))))

(defmacro atmos-GET
  [path body & {:keys [authentication?]
                :or   {authentication? false}}]
  `(atmos-route GET ~authentication? ~path ~body))

(defmacro atmos-POST
  [path body & {:keys [authentication?]
                :or   {authentication? false}}]
  `(atmos-route POST ~authentication? ~path ~body))

(defmacro atmos-PUT
  [path body & {:keys [authentication?]
                :or   {authentication? false}}]
  `(atmos-route PUT ~authentication? ~path ~body))

(defmacro atmos-DELETE
  [path body & {:keys [authentication?]
                :or   {authentication? false}}]
  `(atmos-route DELETE ~authentication? ~path ~body))

(defmacro atmos-main-route
  "Create the main route of web compojure application"
  ([ms-name]
   (let [ms-name (-> ms-name name lower-case)]
     `(atmos-GET [] (str "Welcome to " ~ms-name " micro-service")))))

(defmacro defatmos-route
  "Define atmos route"
  [name ms-name & routes]
  (let [route-name (symbol (str name "-internal"))]
    `(def ~name (defroutes ~route-name
                           (atmos-main-route ~ms-name)
                           ~@routes
                           not-found-route))))