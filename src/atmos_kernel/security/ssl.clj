(ns atmos-kernel.security.ssl
  (:require [pem-reader.core :as pem]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s])
  (:import (java.security KeyStore)
           (java.security.cert CertificateFactory Certificate)))

(s/def ::key-store #(instance? KeyStore %))
(s/def ::trust-store #(instance? KeyStore %))
(s/def ::keystore-or-truststore (s/or :key ::key-store :trust ::trust-store))
(s/def ::PEM-file map?)
(s/def ::certificate #(instance? Certificate %))

(defn create-keystore
  "Create a Java KeyStore with(out) password."
  ([password]
   (doto (KeyStore/getInstance (KeyStore/getDefaultType))
     (.load nil (if password (char-array password)))))
  ([]
   (create-keystore nil)))

(s/fdef create-keystore
        :args (s/alt :without-password (s/cat)
                     :with-password (s/nilable :atmos-kernel.spec/non-blank-string))
        :ret ::key-store)


(defn create-truststore
  "Create a Java TrustStore with(out) password."
  ([password]
   (doto (KeyStore/getInstance (KeyStore/getDefaultType))
     (.load nil (if password (char-array password)))))
  ([]
   (create-truststore nil)))

(s/fdef create-truststore
        :args (s/alt :no-params (s/cat)
                     :with-params (s/cat :password (s/nilable :atmos-kernel.spec/non-blank-string)))
        :ret ::trust-store)

(defn save-keystore
  "Persist KeyStore/TrustStore."
  ([store store-file-path password]
   (with-open [output-stream (io/output-stream store-file-path)]
     (.store store output-stream (if password (char-array password)))))
  ([store store-file-path]
   (save-keystore store store-file-path nil)))

(s/fdef save-keystore
        :args (s/cat :store ::keystore-or-truststore
                     :store-file-path :atmos-kernel.spec/file-path
                     :password (s/nilable :atmos-kernel.spec/non-blank-string))
        :ret nil?)

(defn read-key-pem-file
  "Read PEM file."
  [pem-file-path]
  (pem/read pem-file-path))

(s/fdef read-key-pem-file
        :args (s/cat :pem-file-path :atmos-kernel.spec/file-path)
        :ret ::PEM-file)

(defn read-certificate-pem-file
  "Read Certificate from PEM file."
  [pem-file-path]
  (let [certificate-factory (CertificateFactory/getInstance "X.509")]
    (.generateCertificate certificate-factory (io/input-stream pem-file-path))))

(s/fdef read-certificate-pem-file
        :args (s/cat :pem-file-path :atmos-kernel.spec/file-path)
        :ret ::certificate)

(defn add-pem-client-key
  "Add client key to Java KeyStore."
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

(s/fdef add-pem-client-key
        :args (s/cat :keystore ::key-store
                     :alias :atmos-kernel.spec/non-blank-string
                     :client-pem-certificate :atmos-kernel.spec/file-path
                     :client-pem-key string?
                     :password (s/nilable string?))
        :ret ::key-store)

(defn add-trust-certificate
  "Add certificate to Java TrustStore."
  [truststore alias pem-certificate]
  (let [certificate-file (if (instance? String pem-certificate)
                           (read-certificate-pem-file pem-certificate)
                           pem-certificate)]
    (doto truststore
      (.setCertificateEntry (name alias) certificate-file))))

(s/fdef add-trust-certificate
        :args (s/cat :truststore ::trust-store
                     :alias :atmos-kernel.spec/non-blank-string
                     :pem-certificate :atmos-kernel.spec/file-path)
        :ret ::trust-store)