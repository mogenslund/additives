(ns dk.salza.additives.clojuremdbddhl
  (:require [clojure.string :as str])
  (:use [dk.salza.liq.slider :as slider :exclude [create]]))

(defn next-face
  [sl face]
  (let [ch (get-char sl)
        pch (-> sl left get-char)
        ppch (-> sl (left 2) get-char)
        s9 (if (= pch "\n") (string-ahead sl 9) "")
        sw (if (= pch "\n") (fn [s] (str/starts-with? s9 s)) (fn [s] false))] 
    (cond (= face :stringst) :string
          (= face :string)  (cond (and (= pch "\"") (= ppch "\\")) face
                                  (= pch "\"") :plain
                                  :else face)
          (= face :plain)   (cond (and (= ch "\"") (not= (get-char (right sl)) "\n")) :stringst
                                  (= ch ";") :comment
                                  (and (= ch "#") (or (= pch "\n") (= pch "") (= (get-point sl) 0))) :comment 
                                  (and (= pch "(") (re-find #"def(n|n-|test|record|protocol|macro)? " (string-ahead sl 13))) :type1
                                  (and (= ch ":") (re-matches #"[\( \[{\n]" pch)) :type3
                                  (or (sw "Story: ") (sw "Given ") (sw "When ") (sw "Then ") (sw "To ") (sw "As ") (sw "I want ")) :type2
                                  (= ch "✔") :green
                                  (= ch "▢") :yellow
                                  (= ch "✘") :red
                                  (= ch "•") :green
                                  (= ch "∑") :yellow
                                  (= ch "∘") :yellow
                                  (= ch "⋅") :yellow
                                  (= ch "√") :yellow
                                  (= ch "∪") :yellow
                                  (= ch "∩") :yellow
                                  (= ch "∅") :yellow
                                  (= ch "×") :yellow
                                  (= ch "²") :yellow
                                  (= ch "¬") :yellow
                                  (= ch "∧") :yellow
                                  (= ch "∨") :yellow
                                  (= ch "≥") :yellow
                                  (= ch "≤") :yellow
                                  (= ch "≠") :yellow
                                  (sw "ok ") :green
                                  (sw "na ") :yellow
                                  (sw "re ") :yellow
                                  (sw "fail ") :red
                                  (sw "err ") :red
                                  (sw "And ") :type3
                                  (or (= ch "┼") (= ch "┤") (= ch "├") (= ch "│") (= ch "─") (= ch "╭")
                                      (= ch "╮") (= ch "╰") (= ch "╯") (= ch "┬") (= ch "┴")) :type2 
                                  :else face)
          (= face :green)   (cond (= ch " ") :plain :else face)
          (= face :yellow)   (cond (= ch " ") :plain :else face)
          (= face :red)   (cond (= ch " ") :plain :else face)
          (= face :type1)   (cond (= ch " ") :type2 :else face)
          (= face :type2)   (cond (and (= ch " ") (not= pch "I")) :plain
                                  :else face)
          (= face :type3)   (cond (re-matches #"[\)\]}\s]" (or ch " ")) :plain
                                  :else face)
          (= face :comment) (cond (= ch "\n") :plain
                                  :else face)
                            :else face)))