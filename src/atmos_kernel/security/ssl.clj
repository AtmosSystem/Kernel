(ns atmos-kernel.security.ssl
  (:require [pem-reader.core :as pem]
            [clojure.java.io :as io])
  (:import (java.security KeyStore)
           (java.security.cert CertificateFactory)))

(defn- create-keystore
  ([password]
   (doto (KeyStore/getInstance (KeyStore/getDefaultType))
     (.load nil password)))
  ([]
   (create-keystore nil)))

(defn- create-truststore
  ([password]
   (doto (KeyStore/getInstance (KeyStore/getDefaultType))
     (.load nil password)))
  ([]
   (create-keystore nil)))

(defn- load-key-pem-file
  [pem-file-path]
  (pem/read pem-file-path))

(defn- load-certificate-pem-file
  [pem-file-path]
  (let [certificate-factory (CertificateFactory/getInstance "X.509")]
    (.generateCertificate certificate-factory (io/input-stream pem-file-path))))

(defn- add-pem-client-key
  ([keystore alias client-pem-certificate client-pem-key password]
   (let [client-certificate-file (if (instance? String client-pem-certificate)
                                   (load-certificate-pem-file client-pem-certificate)
                                   client-pem-certificate)

         key (if (instance? String client-pem-key) (load-key-pem-file client-pem-key) client-pem-key)]

     (doto keystore
       (.setCertificateEntry (str alias "-cert") client-certificate-file)
       (.setKeyEntry alias (pem/private-key key) (char-array password) (into-array [client-certificate-file])))))

  ([keystore alias client-pem-certificate client-pem-key]
   (add-pem-client-key keystore alias client-pem-certificate client-pem-key "")))

(defn- add-trust-certificate
  [truststore alias pem-certificate]
  (let [certificate-file (if (instance? String pem-certificate)
                           (load-certificate-pem-file pem-certificate)
                           pem-certificate)]
    (doto truststore
      (.setCertificateEntry alias certificate-file))))


(defn def-ssl
  "Define SSL properties on JVM"
  [ssl-data]
  (let [{:keys [client-alias ca-alias client-pem-certificate client-pem-key ca-pem-certificate password]} ssl-data
        key-store (add-pem-client-key (create-keystore password) client-alias
                                      client-pem-certificate client-pem-key password)

        trust-store (add-trust-certificate (create-truststore password) ca-alias ca-pem-certificate)

        key-store (assoc key-store
                    :path-property "javax.net.ssl.keyStore"
                    :password-property "javax.net.ssl.keyStorePassword")
        trust-store (assoc trust-store
                      :path-property "javax.net.ssl.trustStore"
                      :password-property "javax.net.ssl.trustStorePassword")]
    (doseq [ssl-configuration [key-store trust-store]]
      (when (not (nil? (:file ssl-configuration)))
        (System/setProperty (:path-property ssl-configuration) (.getAbsolutePath (:file ssl-configuration)))
        (System/setProperty (:password-property ssl-configuration) password)))))
