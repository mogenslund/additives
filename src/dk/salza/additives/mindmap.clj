(ns dk.salza.additives.mindmap
  (:require [clojure.string :as str]
            [dk.salza.liq.slider :refer [create find-next get-char forward-visual-column backward-visual-column left right
                                         left-until right-until get-visual-column get-region set-mark get-point
                                         beginning? end?]]))

(defn indent-to-list
  "Taking a string (lines of text)
  and parses to hierarchical list
  of lists."
  [s]
  (read-string
  (str/join "\n"
    (let [level (fn [s] (quot (count (re-find #"  +" s)) 2))
          lines (filter #(re-find (re-pattern "[a-zA-Z]") %) (str/split-lines s))
          shift (concat (rest lines) (list ""))]
      (map (fn [l0 l1]
             (cond (< (level l0) (level l1)) (str "(" (re-find #"[^ ].*" l0))
                   (= (level l0) (level l1)) (re-find #"[^ ].*" l0) 
                   true (str (re-find #"[^ ].*" l0) (apply str (map (fn [s] ")") (range (- (level l0) (level l1))))))))
           lines shift)))))

(defn right-tree
  [l]
  (if (or (not (seq? l)) (= (count l) 1))
    (list (str l))
    (let [topic (first l)
          children (rest l)
          spaces (str/replace topic #"." " ")
          children (concat  (apply concat (map right-tree (rest l))))
          n0 (count (take-while #(= (subs % 0 1) " ") children))
          n1 (- (count children) (count (take-while #(= (subs % 0 1) " ") (reverse children))) 1)]
      (concat
        (map (fn [child n]
               (cond (= n (quot (dec (count children)) 2))
                          (cond (> n n1) (str topic " ╯ " child)
                                (= (subs child 0 1) " ") (str topic " ┤ " child)
                                (= (count children) 1) (str topic " ─ " child)
                                (= n0 n1) (str topic " ─ " child)
                                (= (count children) 2) (str topic " ┬ " child)
                                (= n n1) (str topic " ┴ " child)
                                true (str topic " ┼ " child))
                     (< n n0) (str spaces "   " child)
                     (> n n1) (str spaces "   " child)
                     (= n n0) (str spaces " ╭ " child)
                     (= n n1) (str spaces " ╰ " child)
                     true (cond (= (subs child 0 1) " ") (str spaces " │ " child)
                                true (str spaces " ├ " child))))
          children (range (count children)))
        (when (re-find #"[a-zA-Z]" (last children)) (list (str spaces " "))))
       )))

(defn left-sub-tree
  [l]
  (if (or (not (seq? l)) (= (count l) 1))
    (list (str l))
    (let [topic (first l)
          children (rest l)
          spaces (str/replace topic #"." " ")
          children (concat  (apply concat (map left-sub-tree (rest l))) (list " "))
          n0 (count (take-while #(= (subs % (dec (count %))) " ") children))
          n1 (- (count children) (count (take-while #(= (subs % (dec (count %))) " ") (reverse children))) 1)]
      (concat
        (map (fn [child n]
               (cond (= n (quot (dec (count children)) 2))
                          (cond (> n n1) (str child " ╰ " topic)
                                (= (subs child (dec (count child))) " ") (str child " ├ " topic)
                                (= (count children) 1) (str child " ─ " topic)
                                (= n0 n1) (str child " ─ " topic)
                                (= (count children) 2) (str child " ┬ " topic)
                                (= n n1) (str child " ┴ " topic)
                                true (str child " ┼ " topic))
                     (< n n0) (str child "   " spaces)
                     (> n n1) (str child "   " spaces)
                     (= n n0) (str child " ╮ " spaces)
                     (= n n1) (str child " ╯ " spaces)
                     true (cond (= (subs child (dec (count child))) " ") (str child " │ " spaces)
                                true (str child " ┤ " spaces))))
          children (range (count children)))
        (when (re-find #"[a-zA-Z]" (last children)) (list (str spaces " ")))))))
;; ┼ ┤ ├ │ ─ ╭ ╮ ╯ ╰ ┬ ┴

(defn left-tree
  [l]
  (let [t (left-sub-tree l)
        len (apply max (map count t))]
    (map #(format (str "%" len "s") %) t)))
        

(defn spaces-list
  [rows spaces]
  (take rows (repeat (format (str "%" spaces "s") ""))))

(defn mindmap
  [l & {:keys [indent]}]
  (let [mm (if (string? l) (indent-to-list l) l)
        topic (str (first mm))
        [right0 left0] (split-at (quot (count mm) 2) (rest mm))
        right1 (right-tree (conj right0 topic))
        left1 (left-tree (conj left0 topic))
        cright (count (take-while #(= (subs % 0 1) " ") right1))
        cleft (count (take-while #(= (subs % (dec (count %))) " ") left1))
        right2 (concat (spaces-list (max 0 (- cleft cright)) (count (first right1))) right1)  
        left2 (concat (spaces-list (max 0 (- cright cleft)) (count (first left1))) left1)  
        right3 (concat right2 (spaces-list (max 0 (- (count left2) (count right2))) (count (first right2))))
        left3 (concat left2 (spaces-list (max 0 (- (count right2) (count left2))) (count (first left2))))
        right4 (map #(subs % (inc (count topic))) right3)
        left4 (map #(subs % 0 (dec (- (count %) (count topic)))) left3)
        center (concat (spaces-list (dec (max cright cleft)) (+ (count topic) 6))
                       (list (str " ╭─" (str/replace topic #"." "─") "─╮ ")
                             (str "─┤ " topic " ├─")
                             (str " ╰─" (str/replace topic #"." "─") "─╯ "))
                       (spaces-list (- (count right4) (max cright cleft) 2) (+ (count topic) 6)))
        joined (map str (spaces-list (count left4) indent) left4 center right4)
        ]
    (str/join "\n" joined)))

(defn- down
  [sl]
  (forward-visual-column sl 500 (get-visual-column sl 500)))

(defn- up
  [sl]
  (backward-visual-column sl 500 (get-visual-column sl 500)))

(defn- get-word
  [sl]
  (-> sl
      (set-mark "start")
      (right-until #"\s")
      (get-region "start")))

(defn- get-left-word
  [sl]
  (-> sl
      (right 1)
      (set-mark "start")
      (left 1)
      (left-until #"\s")
      (right 1)
      (get-region "start")))

(defn first-right-child
  [sl]
  (let [cross (-> sl (right-until #"(┼|┤|─|╮|╯|┬|┴|\n)"))]
    (when (and (not (end? cross)) (not= (get-char cross) "\n"))
      (loop [sl0 cross]
        (let [c (get-char sl0)]
          (if (re-matches #"(─|╭|╮|┬)" c)
              (right sl0 2)
              (recur (up sl0))))))))

(defn first-left-child
  [sl]
  (let [cross (-> sl (left-until #"(┼|├|─|╭|╰|┬|┴|\n)"))]
    (when (and (not (beginning? cross)) (not= (get-char cross) "\n"))
      (loop [sl0 cross]
        (let [c (get-char sl0)]
          (if (re-matches #"(─|╭|╮|┬)" c)
              (left sl0 2)
              (recur (up sl0))))))))

(defn next-right-sibling
  [sl]
  (loop [s (-> sl (left 2) down)]
    (let [c (get-char s)]
      (cond (nil? c) nil
            (re-matches #"(┤|│)" c) (recur (down s))
            (re-matches #"(┼|┴|╰|├)" c) (right s 2)
            true nil))))

(defn next-left-sibling
  [sl]
  (loop [s (-> sl (right 2) down)]
    (let [c (get-char s)]
      (cond (nil? c) nil
            (re-matches #"(├|│)" c) (recur (down s))
            (re-matches #"(┼|┴|╯|┤)" c) (left s 2)
            true nil))))

(defn- parse-right
  [sl indent]
  (let [child (first-right-child sl)
        sibling (when (not= indent "") (next-right-sibling sl))]
    (str
      indent (get-word sl) "\n"
      (when child (parse-right child (str indent "  ")))
      (when sibling (parse-right sibling indent)))))

(defn- parse-left
  [sl indent]
  (let [child (first-left-child sl)
        sibling (when (not= indent "") (next-left-sibling sl))]
    (str
      indent (get-left-word sl) "\n"
      (when child (parse-left child (str indent "  ")))
      (when sibling (parse-left sibling indent))
      )))

(defn mindmap-to-indent
  [mm]
  (let [mm0 (str/replace (str/replace-first mm #"─┤" "  ") #"├─" "  ")
        sl (-> (create mm0)
                (find-next "───")
                down
                (right 1))]
    (str (parse-right sl "")
         (subs (parse-left sl "") 2))))

