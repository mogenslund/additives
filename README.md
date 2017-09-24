# Additives

Addtivies is a collection of useful extensions to [Liquid](https://github.com/mogenslund/liquid) that are too specialized to be in the core code.

It is an ongoing project where maturity of the extensiens might differ. Use what you find useful.

Interact with documents using `blob` and create mindmaps from indented lists.

```
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
```

## Blob
Blob is a function to read parts of the current document and probably perform calulations on it.

To use it add Additives to the classpath when starting Liquid and add

    [dk.salza.additives.blob :refer :all]

to the `:require` section in `.liq`.

To get the content between `:data` and `:data` acall `(blob :data)`.

In the code snippet below if you evaluate the `replace-blob...` function Liquid will read what is inside `:data` and the replace what is inside `:output` with the mindmap genereated from `:data`. Feel free to experiment.

```clojure

(replace-blob :output (str "\n" (mindmap (blob :data) :indent 6) "\n"))

:data
Center
  Topic1
    Subtopic11
    Subtopic12
  Topic2
:data

:output

               ╭────────╮ ╭ Topic1 ┬ Subtopic11
      Topic2 ──┤ Center ├─╯        ╰ Subtopic12
               ╰────────╯          

:output


```

## Mindmap
Mindmap has a function for turning a nested list and an indented list (2 spaces) into a mindmap. Also the reverse functions is available for turning a mindmap into an indented list.

To use it add Additives to the classpath when starting Liquid and add

    [dk.salza.additives.mindmap :refer [mindmap mindmap-to-indent]]

to the `:require` section in `.liq`.

Evaluating

```clojure
(mindmap '(Center (Topic1 Subtopic1 Subtopic2) Topic2))
```

Will output:

```
         ╭────────╮ ╭ Topic1 ┬ Subtopic1
Topic2 ──┤ Center ├─╯        ╰ Subtopic2
         ╰────────╯          
```

## Highlight
Highlight added for Behaviordriven development (Given, When, Then).

## Browser
Experimental browser using Lynx as engine. Requires Lynx to be installed.

## License

Copyright © 2018 Mogens Brødsgaard Lund

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.