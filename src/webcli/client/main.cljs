^{:doc "Handling the UI events (like mouseDown, keyDown, etc" }
(ns webcli.client.main
  (:require 
            [noir.cljs.client.watcher :as watcher]
            [clojure.browser.repl :as repl]
            [crate.core :as crate]
            [fetch.remotes :as remotes]
            )
  (:use 
        [jayq.core :only [$ append delegate data]]
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




(def $body ($ :body))
(def $content ($ :#content))

(defpartial
  button [{:keys [label action params]}]
  [:div#header
   [:ul
    [:li
     [:a.button {:href "#" :data-action action :data-param param} label]
     ]
    ]
   ]
  )

(defpartial divide []
  [:p.alert "divide"])

(defpartial up-and-running []
  [:p.alert "divide"])

(append $content
        (button {:label "play-note" :action "play-note" :param "40"})
        )

(delegate $body button :click
  (fn [e]
    (. e -prevendDefault)
    (js/alert "clicked!")))
