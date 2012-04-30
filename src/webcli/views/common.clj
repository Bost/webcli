(ns webcli.views.common
  (:require
    [noir.cljs.core :as cljs]
    [webcli.models.user :as model]
    )
  (:use
    [noir.core :only [defpartial]]
    [hiccup.page :only [include-css include-js html5]]
    [noir.validation :as vali]
    ;[noir.response :as resp]
    [hiccup.form] ;:only [label text-field]
    )
  )

; TODO use stucture like this (with html5)
(defpartial layout [ cmd-nr & content]
  ;(println "layout: cmd-nr" cmd-nr)
  (html5
    [:head
     [:title "web command line interface"]]
    ;(include-js "/CodeMirror-2.23/lib/codemirror.js")
    ;(include-css "/CodeMirror-2.23/lib/codemirror.css")
    ;(include-css "/CodeMirror-2.23/theme/lesser-dark.css")
    ;(include-js "/CodeMirror-2.23/mode/javascript/javascript.js")

;    (include-css "/css/custom-theme/jquery-ui-1.8.19.custom.css")
;    (include-js "/js/jquery-latest/jquery-1.7.2.min.js")
;    (include-js "/js/jquey-latest/jquery-ui-1.8.19.custom.min.js")

;    (include-css "/css/jquery-ui-1.8.9.custom/jquery-ui-1.8.9.custom.css")
;    (include-js "/js/jquery-multi/jquery-1.4.3.min.js")
;    (include-js "/js/jquery-multi/jquery-ui-1.8.13.custom.min.js")
;    (include-js "/js/jquery-multi/jquery.multi-open-accordion-1.5.3.min.js")

    (include-css "/css/nested-accordion/nested.css")
    (include-js "/js/jquery-nested-accordion/ga.js")
    (include-js "/js/jquery-nested-accordion/jquery.min.js")
    (include-js "/js/jquery-nested-accordion/jquery.nestedAccordion.js")
    (include-js "/js/jquery-nested-accordion/expand.js")
    ;(include-js "/js/app.js")
    ;(include-js "/js/ui_demos.js")
    ;[:style {:type "text/css"}
    ; ".CodeMirror {border: 1px solid #eee; } "
     ;".CodeMirror-scroll { height: auto }"
    ; ".CodeMirror-scroll { height: " (/ 100 cmd-nr) " % }"
    ; ]

    [:body
      [:div {:id "in-form"}
     content
       ]
       ]))

(defpartial error-item [[first-error]]
  [:p.error first-error])


(def prompt
  "TODO move the function for localhost to the model"
  (str
    ;uname -n   print the network node hostname
    ;(read-string (first (cmd "uname -n")))   ; this is bash-specific
    (let [
          localhost (java.net.InetAddress/getLocalHost) ; this is universal for JVM; TODO how is it for python-VM
          ]
      (.getHostName localhost))
    "$ "))

^{:doc "TODO input validation should be made on controler" }
(defpartial command-fields [{:keys [ cmd-str cmd-nr]}]
  (vali/on-error :cmd-str error-item)
  (label "cmd-str" prompt)

;<input id="autocomplete" style="z-index: 100; position: relative" title="type &quot;a&quot;" class="ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true">

  ;(text-field "cmd-str" "ls -la")
  ;[:div
  (text-field {:class "ui-autocomplete-input"}
              "cmd-str" "pwd")
  ; ]
  (label "cmd-nr" "cmd-nr: ")
  (text-field {:class "ui-autocomplete-input"}
    "cmd-nr" @model/glob-cmd-nr)  ;@glob-cmd-nr is the same as (deref glob-cmd-nr)
  )

(defn escape-str [s0]
  (let [
        s1 (clojure.string/replace s0 #" " "&nbsp;")
        s2 (clojure.string/replace s1 #"\n" "<br />")
        ]
    s2)
  )

^{:doc
  "Result contains a comand and response to it. I.e."
  "                     cmd         response"
  "    (\"bost-desktop$ pwd\n\" \"/home/bost/dev/webcli\n\")" }
(defpartial result-area [id result]
   [:h2
      ;[:a {:href "#"}
         (model/get-cmd result)
      ;]
    ]
   [:div
      (map #(escape-str %) (vec (model/get-response result)))
   ]
)
