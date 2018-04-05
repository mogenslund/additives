(ns dk.salza.additives.slideshow
  (:require [clojure.string :as str]
            [dk.salza.liq.editor :as editor]))

(def slides (atom ["This is page 1" "This is page 2" "This is page 3"]))
(def index (atom 0))

(defn blob
  [keyw]
  (or (second
        (str/split
          (editor/get-content)
          (re-pattern (str "\n\\:" (name keyw) "\n?"))))
      ""))

(defn update-display
  []
  (editor/clear)
  (editor/insert (nth @slides @index))
  (editor/beginning-of-buffer))

(defn next-slide
  []
  (when (< @index (dec (count @slides)))
    (swap! index inc)
    (update-display)))

(defn previous-slide
  []
  (when (> @index 0)
    (swap! index dec)
    (update-display)))

(def keymap
  {:cursor-color :off
   "C-w" editor/kill-buffer
   "C-g" editor/previous-real-buffer
   "esc" editor/previous-real-buffer
   " " next-slide
   "right" next-slide
   "left" previous-slide
  })

(defn run
  [& keyws]
  (reset! slides (vec (map blob keyws)))
  (editor/new-buffer "-slideshow-")
  (editor/set-keymap keymap)
  (update-display))
