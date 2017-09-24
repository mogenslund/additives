# Introduction to additives

```clojure

    (editor/set-global-key :f5 (fn [] (replace-blob :output (str "\n\n\n" (mindmap (blob :mindmap) :indent 10) "\n"))))

:mindmap
Additives
  
  (mindmap mindmap mindmap-to-indent)

  (blob blob eval-blob delete-blob insert-blob replace-blob)

  browserapp
    (requires lynx)

  highlight
    extension
      (bdd given when then)
      (other clojure markdown)

  .liq
    require
      mindmap
      blob
      browserapp
      clojuremdbddhl

:mindmap

:output



               given ╮                                                                
                when ┼ bdd ╮                                                          
                then ╯     │                                       ╭ mindmap ┬ mindmap
                           ├ extension ╮                           │         ╰ mindmap-to-indent
           clojure ╮       │           ╰ highlight ╮               │         
          markdown ┴ other ╯                       │               │      ╭ blob
                                                   │ ╭───────────╮ │      ├ eval-blob
                                                   ├─┤ Additives ├─┼ blob ┼ delete-blob
                                                   │ ╰───────────╯ │      ├ insert-blob
                          mindmap ╮                │               │      ╰ replace-blob
                             blob ┤                │               │      
                       browserapp ┼ require ─ .liq ╯               ╰ browserapp ─ requires ─ lynx
                   clojuremdbddhl ╯                                                        
                                                                                      
                                                                                      
                                                                                      

:output


```