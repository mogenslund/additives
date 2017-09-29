(ns dk.salza.additives.linux
  (:require [clojure.string :as str]
            [dk.salza.liq.tools.cshell :as sh]))

(defn curl
  [& {:keys [:method :url :data :headers :other]}]
  (let [parameters (concat
                     (list "curl")
                     (list "-X" (str/upper-case (name method)))
                     (when data (list "-d" (str/join "&" (for [[k v] data] (str k "=" v))) ))
                     (when headers (apply concat (for [[k v] headers] (list "-H" (str k ": " v) ))))
                     other
                     (list url))]
    (apply sh/cmd parameters)))