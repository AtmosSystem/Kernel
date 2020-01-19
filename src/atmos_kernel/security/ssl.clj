(ns atmos-kernel.security.ssl
  (:require [pem-reader.core :as pem]
            [clojure.java.io :as io])
  (:import (java.security KeyStore)
           (java.security.cert CertificateFactory)))

(defn create-keystore
  ([password]
   (doto (KeyStore/getInstance (KeyStore/getDefaultType))
     (.load nil (if password (char-array password)))))
  ([]
   (create-keystore nil)))

(defn create-truststore
  ([password]
   (doto (KeyStore/getInstance (KeyStore/getDefaultType))
     (.load nil (if password (char-array password)))))
  ([]
   (create-truststore nil)))

(defn save-keystore
  ([store store-file-path password]
   (with-open [output-stream (io/output-stream store-file-path)]
     (.store store output-stream (if password (char-array password)))))
  ([store store-file-path]
   (save-keystore store store-file-path nil)))

(defn read-key-pem-file
  [pem-file-path]
  (pem/read pem-file-path))

(defn read-certificate-pem-file
  [pem-file-path]
  (let [certificate-factory (CertificateFactory/getInstance "X.509")]
    (.generateCertificate certificate-factory (io/input-stream pem-file-path))))

(defn add-pem-client-key
  ([keystore alias client-pem-certificate client-pem-key password]
   (let [client-certificate-file (if (instance? String client-pem-certificate)
                                   (read-certificate-pem-file client-pem-certificate)
                                   client-pem-certificate)

         key (if (instance? String client-pem-key) (read-key-pem-file client-pem-key) client-pem-key)]

     (doto keystore
       (.setCertificateEntry (str (name alias) "-cert") client-certificate-file)
       (.setKeyEntry (name alias) (pem/private-key key) (char-array password) (into-array [client-certificate-file])))))

  ([keystore alias client-pem-certificate client-pem-key]
   (add-pem-client-key keystore alias client-pem-certificate client-pem-key "")))

(defn add-trust-certificate
  [truststore alias pem-certificate]
  (let [certificate-file (if (instance? String pem-certificate)
                           (read-certificate-pem-file pem-certificate)
                           pem-certificate)]
    (doto truststore
      (.setCertificateEntry (name alias) certificate-file))))