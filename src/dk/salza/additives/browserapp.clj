(ns dk.salza.additives.browserapp
  (:require [clojure.java.io :as io]
            [dk.salza.liq.buffer :as buffer]
            [dk.salza.liq.slider :refer :all]
            [dk.salza.liq.editor :as editor]
            [dk.salza.liq.keys :as keys]
            [dk.salza.liq.apps.promptapp :as promptapp]
            [dk.salza.liq.extensions.headlinenavigator]
            [dk.salza.liq.extensions.linenavigator]
            [dk.salza.liq.syntaxhl.clojuremdhl :as clojuremdhl]
            [dk.salza.liq.logging :as logging]
            [dk.salza.liq.tools.cshell :refer :all]
            [dk.salza.liq.coreutil :refer :all]))

(def navigate (atom nil))

(def keymap
  {:cursor-color :blue
   :M editor/prompt-to-tmp
   :enter #(@navigate)
   :tab #(do (editor/find-next "]") (editor/forward-char))
   :space #(editor/forward-page)
   :right editor/forward-char
   :left editor/backward-char
   :up editor/backward-line
   :down editor/forward-line
   :C-s #(promptapp/run editor/find-next '("SEARCH"))
   :v editor/selection-toggle
   :g {:g editor/beginning-of-buffer
       :t editor/top-align-page
       :n editor/top-next-headline
       :c #(editor/prompt-append (str "--" (editor/get-context) "--"))
       :i dk.salza.liq.extensions.headlinenavigator/run
       :l dk.salza.liq.extensions.linenavigator/run}
   :dash editor/top-next-headline
   :C-g editor/escape
   :esc editor/escape
   :e editor/eval-last-sexp
   :E editor/evaluate-file
   :C-e editor/evaluate-file-raw
   :l editor/forward-char
   :j editor/backward-char
   :i editor/backward-line
   :k editor/forward-line
   :J editor/beginning-of-line
   :G editor/end-of-buffer
   :L editor/end-of-line
   :m editor/previous-real-buffer 
   :n editor/find-next
   :O editor/context-action
   :w editor/forward-word
   :1 editor/highlight-sexp-at-point
   :2 editor/select-sexp-at-point
   :y {:y #(do (or (editor/copy-selection) (editor/copy-line)) (editor/selection-cancel))}
   :C-w editor/kill-buffer
   :C-t (fn [] (editor/tmp-test))
   })

(defn lynx-link
  "Takes a slider and tries to resolve the context
  in relation to lynx.
  Move back until [.
  Find next number.
  Find number at the end.
  Get link related to number."
  [sl0]
  (let [text (-> sl0
                 (left-until #{"["})
                 (set-mark "start-lynx")
                 end
                 (get-region "start-lynx"))
        num (re-find #"(?<=\[)\d+(?=\])" text)]
    ;(editor/prompt-append num "\n" (re-find (re-pattern (str "(?<= " num "\\. )[^\n]+")) text))
    (re-find (re-pattern (str "(?<= " num "\\. )http[^\n]+")) text)))


;; Links:
;; [direct.md]
;;
;; [My Link][1]
;; [1]: Path to link

;; :enter should open link in mdbrowserapp
;; :tab Move cursor to next link
(defn run
  "TODO: Implementation not finished yet."
  [url]
  ;(editor/prompt-append (str "-" url "-"))
  (if (editor/get-buffer url)
    (editor/switch-to-buffer url)
    (do
      (editor/new-buffer url)
      (editor/set-keymap keymap)
      (editor/set-highlighter clojuremdhl/next-face)
      (editor/insert (cmd "lynx" "--dump" url))
      (editor/beginning-of-buffer))))

(reset! navigate
  (fn []
    (let [sl (buffer/get-slider (editor/current-buffer))
          link (lynx-link sl)]
      (logging/log "MDBROWSERLINK" link)
      ;(editor/prompt-append (str "-" link "-"))
      (run link)
      )))

