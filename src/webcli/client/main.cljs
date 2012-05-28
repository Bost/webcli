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

(defn getId [idx]
  ;; TODO this is a kind of macro for javascript - probably not
  ;; the best approach
  (js/String. (+ "#head" idx)))

(defn doclick [id]
  ;; passing {} directly to the toggle function works - strange
  (.log js/console "doclick: " id)
  (let [elem
	(.next (jquery (str "#" id)))]
    (
     (.toggle elem "blind" {} 360))))


;; TODO replace collapse [idx] and expand [idx] with do [action idx]
(defn collapse [idx]
  ;;(.log js/console "Collapsing element " idx)
  (-> (jquery (str "#head" idx))
      (.next)
      (.hide)
   )
  )

(defn expand [idx]
  ;(.log js/console "Epanding element " idx)
  (-> (jquery (str "#head" idx))
      (.next)
      (.show)
   )
  )

(defn bind [selector action n]
  "Bind a html element specified by the selector with the action
  which is executed as a click event on n subelements"
  (->
    (jquery selector)
    (.click
      (fn []
        ;; (.log js/console "all_elems: action: " action "; n: " n)
        (doseq [i (range n)]
          (action i))
        )
      )
    ;; TODO not sure if 'return false;' is needed here
    )
  )

(defn set-sortable [id]
  ;(.log js/console "set_sortable: id: " id)
  (->
    (jquery id)
    (.sortable)
    ;(.disableSelection)  ;; this must be deactivated? Ugh!
    ;(.resizable) ;; this may apply to $(".resizable")
    ;(.draggable) ;; this may apply to $(".draggable")
    )
  )

(defn simple-bind [selector]
  (->
    (jquery selector)
    (.click
      (this-as me
        ;;(.log js/console
        ;;  "simple_bind: selector: " selector "; this-as: " me)
        ;;(fn-doclick me)
        ;;this cannot be done:
        ;; (let [target me.currentTarget]
        ;;   (fn [target]
        ;;    (doclick target.id))
        ;; )
        ;; because me.currentTarget must be processed by the
        ;; javascript engine not clojurescript compiler
        (fn [me]
          ;(.log js/console "fn_doclick: me: " me)
          ;; me.currentTarget.id is processed by js engine not cljs
          (doclick me.currentTarget.id))
        )
      )
    )
  )

(defn getids [m]
  (str (reduce str (map #(str "#head" % ", ") (range m))) "#head" m))

(comment
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
)

(jquery
  (fn []
    (set-sortable "#sortable")
    ;; webcli.client.main.maxIdx is specified by common.clj
    ;;(.log js/console "main.cljs: " maxIdx)
    (bind "#expand_all", expand, maxIdx)
    (bind "#collapse_all", collapse, maxIdx)
    ;;$(\"input:submit, a, button\", \".buttons\" ).button();
    ;;$(\"a\", \".buttons\").click(function(){return false;});
    (let [ids (getids (- maxIdx 1))]
      (simple-bind ids)
      )
    ;; let the last element opened
    (let [ids (getids (- maxIdx 2))]
      ;;(.log js/console "collapse all elems: " ids "; maxIdx: " maxIdx)
      (-> (jquery ids)
	  (.next)
          (.hide)
	  )
      )
    )
  )
