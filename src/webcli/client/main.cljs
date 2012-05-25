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

;"Print evaluated expression and return its result"
;(defmacro dbgs[x]
;  `(let [x# ~x]
;     (println '~x "=" x#) x#
;     )
;  )


(def $content ($ :#content))

(def jquery (js* "$"))

(jquery
   (fn []
     (-> (jquery "div.meat")
         (.html "This is a test.")
         (.append "<div>Look here!</div>"))))

(defn goyx [idx]
  (js/alert (str "#head" idx "-y")))

(defn getId [idx]
  ;; TODO this is a kind of macro for javascript - probably not the best approach
  (js/String. (+ "#head" idx)))

(defn doclick [divId]
  ;; passing {} directly to the toggle function works - strange
  (let [elem
	(.next (jquery (str "#" divId)))]
    (
     (.toggle elem "blind" {} 360))))


;; TODO replace collapse [idx] and expand [idx] with do [action idx]
(defn collapse [idx]
  ;;(.hide (.next (jquery (str "#head" 0))))
  (.log js/console "Collapsing element " idx)
  (-> (jquery (str "#head" idx))
      (.next)
      (.hide)
   )
  )

(defn expand [idx]
  ;;(.show (.next (jquery (str "#head" 0))))
  (.log js/console "Epanding element " idx)
  (-> (jquery (str "#head" idx))
      (.next)
      (.show)
   )
  )

;; TODO replace collapse-all [n] and expand-all [n] with do-all [action n]
(defn collapse-all [n]
  "n is a number of items to collapse; Using (.length @model/sddession) "
  "gives me a javascript error: Uncaught ReferenceError: webcli is not defined"
  "passing n as an argument makes this is better anyway"
  (doseq [i (range n)]
    (collapse i)))

(defn expand-all [n]
  "n is a number of items to collapse; Using (.length @model/sddession) "
  "gives me a javascript error: Uncaught ReferenceError: webcli is not defined"
  "passing n as an argument makes this is better anyway"
  (doseq [i (range n)]
    (expand i)))


(defn full-doclick [divId]
  ;; this method does not work - seems like the {} are interpreted
  ;; other effect must be downloaded from jquery theme roller
  (let [
	selectedEffect "blind"
	;; most effect types need no options passed by default
	;; some effects have required parameters
	options
	(if (= selectedEffect "scale")
	  "{ percent: 0 }"
	  (if (= selectedEffect "size" )
	    "{ to: { width: 200, height: 60 } }"
	    "{}"
	    )
	  )
	elem (.next
	      (jquery (str "#" divId)))
	]
    (
     (.toggle elem selectedEffect options 360)
     )
    )
  )