(ns dk.salza.additives.slideshow
  (:require [clojure.string :as str]
            [dk.salza.liq.keymappings.normal]
            [dk.salza.liq.keymappings.insert]
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
  {:id "dk.salza.additives.slideshow.page"
   :cursor-color :off
   "\t" #(editor/set-keymap "dk.salza.additives.slideshow.normal")
   "i" #(editor/set-keymap "dk.salza.additives.slideshow.insert")
   "o" #(do (editor/insert-line) (editor/set-keymap "dk.salza.additives.slideshow.insert"))
   "C-w" editor/kill-buffer
   "C-g" editor/previous-real-buffer
   "esc" editor/previous-real-buffer
   " " next-slide
   "l" next-slide
   "j" #(editor/set-keymap "dk.salza.additives.slideshow.normal")
   "right" next-slide
   "left" previous-slide
   "h" previous-slide
   "k" #(editor/set-keymap "dk.salza.additives.slideshow.normal")
  })

(defn run
  [& keyws]
  (reset! slides (vec (map blob keyws)))
  (editor/new-buffer "-slideshow-")
  (editor/add-keymap (assoc dk.salza.liq.keymappings.normal/keymapping
                       :id "dk.salza.additives.slideshow.normal"
                       "i" #(editor/set-keymap "dk.salza.additives.slideshow.insert")
                       "o" #(do (editor/insert-line) (editor/set-keymap "dk.salza.additives.slideshow.insert"))
                       "esc" #(editor/set-keymap "dk.salza.additives.slideshow.page")
                       "\t" #(editor/set-keymap "dk.salza.additives.slideshow.page")))
  (editor/add-keymap (assoc dk.salza.liq.keymappings.insert/keymapping
                       :id "dk.salza.additives.slideshow.insert"
                       "esc" #(editor/set-keymap "dk.salza.additives.slideshow.normal")
                       "\t" #(editor/set-keymap "dk.salza.additives.slideshow.normal")))
  (editor/add-keymap keymap)
  (editor/set-keymap "dk.salza.additives.slideshow.page")
  (update-display))
