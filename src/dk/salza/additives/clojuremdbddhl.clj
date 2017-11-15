(ns dk.salza.additives.clojuremdbddhl
  (:use [dk.salza.liq.slider :as slider :exclude [create]]))

(defn next-face
  [sl face]
  (let [ch (look-ahead sl 0)
        pch (look-behind sl 1)
        ppch (look-behind sl 2)]
    (cond (= face :string)  (cond (and (= pch "\"") (= ppch "\\")) face
                                  (and (= pch "\"") (string? (-> sl (left 2) (get-char)))) :plain
                                  (and (= pch "\"") (re-matches #"[^#\( \[{\n]" ppch)) :plain
                                  (and (= pch "\"") (re-matches #"[\)\]}]" (or ch " "))) :plain
                                  :else face)
          (= face :plain)   (cond (and (= ch "\"") (re-matches #"[#\( \[{\n]" pch)) :string
                                  (= ch ";") :comment
                                  (and (= ch "#") (or (= pch "\n") (= pch "") (= (get-point sl) 0))) :comment 
                                  (and (= pch "(") (re-find #"def(n|n-|test|record|protocol|macro)? " (string-ahead sl 13))) :type1
                                  (and (= ch ":") (re-matches #"[\( \[{\n]" pch)) :type3
                                  (and (= pch "\n") (re-matches #"(Story\:|Given|When|Then|To|As|I want) (?s).*" (string-ahead sl 9))) :type2
                                  (= ch "✔") :green
                                  (= ch "▢") :yellow
                                  (= ch "✘") :red
                                  (= ch "•") :green
                                  (= ch "∑") :yellow
                                  (= ch "∘") :yellow
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
                                  (and (= pch "\n") (re-matches #"(ok) (?s).*" (string-ahead sl 9))) :green
                                  (and (= pch "\n") (re-matches #"(na|re) (?s).*" (string-ahead sl 9))) :yellow
                                  (and (= pch "\n") (re-matches #"(fail|err) (?s).*" (string-ahead sl 9))) :red
                                  (and (= pch "\n") (re-matches #"And (?s).*" (string-ahead sl 9))) :type3
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