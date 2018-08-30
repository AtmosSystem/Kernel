(ns atmos-kernel.security.ssl
  (:require [atmos-kernel.io :refer [copy-file]])
  (:import (java.io File)))

(defn- assoc-ssl-file [ssl-data] (assoc ssl-data :file (copy-file (:file-path ssl-data) (name ssl-data))))

(defn defssl
  [ssl-data]
  (let [key-store (assoc-ssl-file (:key-store ssl-data))
        trust-store (assoc-ssl-file (:trust-store ssl-data))

        key-store (assoc key-store
                    :path-property "javax.net.ssl.keyStore"
                    :password-property "javax.net.ssl.keyStorePassword")
        trust-store (assoc trust-store
                      :path-property "javax.net.ssl.trustStore"
                      :password-property "javax.net.ssl.trustStorePassword")]
    (doseq [ssl-configuration [key-store trust-store]]
      (do
        (System/setProperty (:path-property ssl-configuration) (.getAbsolutePath (:file ssl-configuration)))
        (System/setProperty (:password-property ssl-configuration) (:password ssl-configuration))))))
