(ns atmos-kernel.io
  (:require [atmos-kernel.core :refer [nil-or-empty?]]
            [clojure.java.io :as io]))



(defn copy-file
  "Copy content from source to destination using stream"
  [source destination]
  (if-not (and (nil-or-empty? source) (nil-or-empty? destination))
    (let [destination-file (io/file destination)]
      (with-open [in-stream (io/input-stream source)
                  out-stream (io/output-stream destination-file)]
        (io/copy in-stream out-stream)
        destination-file))))