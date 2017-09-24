(ns dk.salza.additives.blob
  (:require [dk.salza.liq.editor :as editor]
            [clojure.string :as str]))

(defn blob
  [keyw]
  (or (second
        (str/split
          (editor/get-content)
          (re-pattern (str "\n\\:" (name keyw) "\n?"))))
      ""))

(defn eval-blob
  [keyw]
  (eval (read-string (str "(do " (blob keyw) ")"))))

(defn delete-blob
  [keyw]
  (let [pos (editor/get-point)
        content (editor/get-content)]
    (when (= (count (re-seq (re-pattern (str "\n" keyw " *\n")) content)) 2)
      (editor/beginning-of-buffer)
      (editor/find-next (str "\n" keyw))
      (editor/forward-char)
      (editor/insert-line)
      (editor/selection-set)
      (editor/find-next (str "\n" keyw))
      (editor/delete-selection)
      (editor/selection-cancel)
      (editor/beginning-of-buffer)
      (editor/forward-char pos))))

(defn insert-blob
  [keyw text]
  (let [pos (editor/get-point)
        content (editor/get-content)]
    (when (= (count (re-seq (re-pattern (str "\n" keyw " *\n")) content)) 2)
      (editor/beginning-of-buffer)
      (editor/find-next (str "\n" keyw))
      (editor/forward-char)
      (editor/forward-line)
      (editor/insert text)
      (editor/beginning-of-buffer)
      (editor/forward-char pos))))

(defn replace-blob
  [keyw text]
  (delete-blob keyw)
  (insert-blob keyw text))

