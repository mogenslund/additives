(ns dk.salza.additives.linux
  (:require [clojure.string :as str]
            [dk.salza.liq.editor :as editor]
            [dk.salza.liq.tools.cshell :as sh]))

(defn curl
  [& args]
  (let [sp " " ; ""
        inputmap (if (map? (first args)) (first args) (apply hash-map args))
        {:keys [:method :url :data :headers :other :dry]} inputmap
        parameters (concat
                     (list "curl")
                     (when method (list "-X" (str/upper-case (name method))))
                     (when data (list "-d" (str/join "&" (for [[k v] data] (str (name k) "=" (str/replace v #"&" "%26"))))))
                     (when headers (apply concat (for [[k v] headers] (list "-H" (str k ":" sp v) ))))
                     other
                     (list url))]
    (if dry
      (str/join sp parameters)
      (apply sh/cmd parameters))))

(defn git
  [& args]
  (let [gitdir (first (reverse args))
        gitargs (reverse (rest (reverse args)))]
    (apply sh/cmdseq (concat (list "git" "--work-tree" gitdir "--git-dir" (str gitdir "/.git")) gitargs))))

(defn grep
  [& args]
  (apply sh/cmdseq (concat (list "grep") args)))

(defn bash
  ([line]
   (sh/cmd "bash" "-c" line))
  ([] (bash (editor/get-line))))


;grep -irlh "curl" --include "*.clj" /home/mogens/m
;(->> "/home/mogens/m" lsr (lrex #"curl") p)