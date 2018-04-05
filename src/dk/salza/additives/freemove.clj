(ns dk.salza.additives.freemove
  (:require [dk.salza.liq.editor :as editor]
            [dk.salza.liq.slider :refer :all]
            [clojure.string :as str]))

(defn move-left2
  []
  (editor/set-mark "freemove")
  (when 
    (loop [sp 0]
      (editor/backward-char)
      (let [c (editor/get-char)]
        (cond (and (= c " ") (= sp 2)) (do (editor/delete-char) true)
              (= c " ") (recur (inc sp))
              (= c "\n") false
              :else (recur 0))))
    (editor/point-to-mark "freemove")
    (loop [sp 0]
      (editor/forward-char)
      (let [c (editor/get-char)]
        (cond (and (= c " ") (= sp 1)) (editor/insert " ")
              (= c " ") (recur (inc sp))
              (or (= c "\n") (editor/end-of-buffer?)) (editor/insert " ")
              :else (recur 0)))))
  (editor/point-to-mark "freemove"))

(defn move-right2
  []
  (editor/set-mark "freemove")
  (loop [sp 0]
    (editor/backward-char)
    (let [c (editor/get-char)]
      (cond (and (= c " ") (= sp 1)) (editor/insert " ")
            (= c " ") (recur (inc sp))
            (= c "\n") (do (editor/forward-char (editor/insert " ")))
            :else (recur 0))))
  (editor/point-to-mark "freemove")
  (loop [sp 0]
    (editor/forward-char)
    (let [c (editor/get-char)]
      (cond (and (= c " ") (= sp 2)) (editor/delete)
            (= c " ") (recur (inc sp))
            (or (= c "\n") (editor/end-of-buffer?)) (do)
            :else (recur 0))))
  (editor/point-to-mark "freemove"))

(defn to-left-border
  [sl0]
  (loop [sl sl0]
    (if (or (beginning? sl)
            (= (get-char (left sl)) "\n")
            (= (get-char sl) (get-char (right sl)) " "))
      sl
      (recur (left sl)))))

(defn to-right-border
  [sl0]
  (loop [sl sl0]
    (if (or (end? sl)
            (= (get-char sl) "\n")
            (= (get-char (left sl 2)) (get-char (left sl)) " "))
      sl
      (recur (right sl)))))

(defn get-context-region
  [sl]
  (let [text (str "  "
               (-> sl
                 (set-mark "freemove1")
                 to-left-border
                 (set-mark "freemove2")
                 (point-to-mark "freemove1")
                 to-right-border
                 (get-region "freemove2")) "  ")]
    (re-find #"  [^ ].*[^ ]  " text)))

(defn delete-context-region
  [sl]
  (-> sl
    (set-mark "freemove1")
    to-left-border
    (set-mark "freemove2")
    (point-to-mark "freemove1")
    to-right-border
    (delete-region "freemove2")))

(defn pos-on-line
  [sl]
  (- (get-point sl) (get-point (beginning-of-line sl))))


;(defn get-area
;  [sl0]
;  (loop [sl sl0]
;    
;  ))

(defn move-left
  ([sl]
   (let [p (get-point sl)
         text (get-context-region sl)
         sl1 (delete-context-region sl)]
       (-> sl1 left (insert text) (set-point (dec p)))))
  ([sl amount]
    (loop [s sl n amount]
     (if (<= n 0)
       s
       (recur (move-left s) (dec n))))))

 
(defn move-right
  ([sl]
   (let [p (get-point sl)
         text (get-context-region sl)
         sl1 (delete-context-region sl)
         sl2 (if (or (= (get-char sl1) "\n") (end? sl1)) (left (insert sl1 " ")) sl1)]
       (-> sl2
           right
           (insert text) (set-point (inc p)))))
  ([sl amount]
    (loop [s sl n amount]
     (if (<= n 0)
       s
       (recur (move-right s) (dec n))))))

(defn move-up
  [sl]
  (let [text (get-context-region sl)
        sl1 (delete-context-region sl)
        offset (- (get-point sl) (get-point sl1))
        pol (pos-on-line sl1)]
    (-> sl1 beginning-of-line left beginning-of-line (insert text) beginning-of-line (right offset) (move-right pol))))

(defn move-down
  [sl]
  (let [text (get-context-region sl)
        sl1 (delete-context-region sl)
        offset (- (get-point sl) (get-point sl1))
        pol (pos-on-line sl1)]
    (-> sl1 end-of-line right (insert text) beginning-of-line (right offset) (move-right pol))))

;; Use forward-line add spaces until pos ok, the insert.

;; Load the freemove addon 
(defn run
  []
  ;(editor/set-global-key "M-j" #(editor/apply-to-slider move-left))
  ;(editor/set-global-key "M-l" #(editor/apply-to-slider move-right))
  ;(editor/set-global-key "M-i" #(editor/apply-to-slider move-up))
  ;(editor/set-global-key "M-k" #(editor/apply-to-slider move-down))
  )
         
