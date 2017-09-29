(ns dk.salza.additives.flowdiagram
  (:require [clojure.string :as str]
            [dk.salza.liq.slider :refer [create find-next get-char forward-visual-column backward-visual-column left right
                                         left-until right-until get-visual-column get-region set-mark get-point
                                         beginning? end?]]))





;; http://csbruce.com/software/utf-8.html
;; ┼ ┤ ├ │ ─ ╭ ╮ ╯ ╰ ┬ ┴ ⭠ ⭡ ⭢⭣⭦⭧⭨⭩╱╲ ⯅ ⯆ ⯇ ⯈ ∧ > < ∨


;;<─────>
;; ─⯈──⭢


; Full
;            ╭───────╮                   ╭───────╮
;     ╭ ab ─>│ Start │──── Some text ───>│ First │
;     │      ╰───────╯                   ╰───────╯
; A ──╯         ∧                            ∧
;               │                            │
;                                            │
;             Initial ───────────────────────╯


; Relations
;              
;     ╭ ab ─ Start ── Some text ──── First 
;     │      
; A ──╯         │                      │     
;               │                      │
;                                      │
;             Initial ─────────────────╯


; Notation
; A: A --> B: ab
; B --> Start
; Start -- D: Some text
; D --> First
; Initial --> Start