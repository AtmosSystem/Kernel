(ns atmos-kernel.io
  (:require [atmos-kernel.core :refer [nil-or-empty?]]
            [clojure.java.io :as io]))



(defn copy-file
  "Copy content from source to destination using stream"
  [source destination]
  (if (and (not (nil-or-empty? source)) (not (nil-or-empty? destination)))
    (let [destination-file (io/file destination)]
      (with-open [in-stream (io/input-stream source)
                  out-stream (io/output-stream destination-file)]
        (io/copy in-stream out-stream)
        destination-file))))