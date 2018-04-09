(ns atmos-kernel.web-route
  (:require [clojure.string :refer [lower-case join]]
            [atmos-kernel.web :refer [atmos-response]]
            [compojure.core :refer [GET]]
            [compojure.route :refer [not-found]]))


(def not-found-route (-> {} not-found atmos-response))
(def not-implemented-route (let [data {:message "Not implemented method"}]
                             (-> data not-found atmos-response)))


(defn atmos-main-route
  ([ms-name system]
   (let [ms-name (-> ms-name name lower-case)]
     (GET "/" [] (str "Welcome to " system " " ms-name " micro-service"))))
  ([ms-name]
   (atmos-main-route ms-name "atmos")))

(defn atmos-route
  [ms-name & params]
  (let [ms-name (-> ms-name name lower-case)
        params (join "/" params)]
    (str "/"
         ms-name
         (if-not (empty? params)
           (str "/" params)))))