^{:doc "Handling the UI events (like mouseDown, keyDown, etc" }
(ns webcli.client.main
  (:require 
            [noir.cljs.client.watcher :as watcher]
            [clojure.browser.repl :as repl]
            [crate.core :as crate]
            [fetch.remotes :as remotes]
            )
  (:use 
        [jayq.core :only [$ append delegate data css inner]]
        )
  (:use-macros [crate.macros :only [defpartial]]))

;;************************************************
;; Dev stuff
;;************************************************

;; display that 3 buttons on the bottom right
(watcher/init)
;;(repl/connect "http://localhost:9000/repl")

;;************************************************
;; Code
;;************************************************

"Print evaluated expression and return its result"
(defmacro dbgs[x]
  `(let [x# ~x]
     (println '~x "=" x#) x#
     )
  )


(def $content ($ :#content))

(def jquery (js* "$"))

(jquery
   (fn []
     (-> (jquery "div.meat")
         (.html "This is a test.")
         (.append "<div>Look here!</div>"))))

(defn goy [idx]
  (js/alert (str "#head" idx "-y")))

(defn getId [idx]
  (str "#head" idx))


(defn doclick []
   ;(runEffect this.id)
    ; other effect must be downloaded from jquery theme roller
  ;(let [
  ;      selectedEffect"blind"
  ;      options {}
   ;     ]

    ; most effect types need no options passed by default
    ; some effects have required parameters
    ;if ( selectedEffect === \"scale\" ) {
    ;    options = { percent: 0 };
    ;} else if ( selectedEffect === \"size\" ) {
    ;    options = { to: { width: 200, height: 60 } };
    ;}
    ;var elem = $( \"#\"+divId +	\" .effect\" );
    ;var elem = $(\"#\"+divId).next();
    ;elem.toggle( selectedEffect, options, 360 );
  ;  )
  nil
  )

