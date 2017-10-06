(ns dk.salza.additives.linux
  (:require [clojure.string :as str]
            [dk.salza.liq.tools.cshell :as sh]))

(defn curl
  [& {:keys [:method :url :data :headers :other :dry]}]
  (let [parameters (concat
                     (list "curl")
                     (when method (list "-X" (str/upper-case (name method))))
                     (when data (list "-d" (str/join "&" (for [[k v] data] (str k "=" v))) ))
                     (when headers (apply concat (for [[k v] headers] (list "-H" (str k ": " v) ))))
                     other
                     (list url))]
    (if dry
      (str/join " " parameters)
      (apply sh/cmd parameters))))


;grep -irlh "curl" --include "*.clj" /home/mogens/m
;(->> "/home/mogens/m" lsr (lrex #"curl") p)