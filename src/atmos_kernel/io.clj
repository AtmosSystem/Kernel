(ns atmos-kernel.io
  (:require [clojure.java.io :as io]))


(defn copy-file
  [source destination]
  (let [destination-file (io/file destination)]
    (with-open [in-stream (io/input-stream source)
                out-stream (io/output-stream destination-file)]
      (io/copy in-stream out-stream)
      destination-file)))