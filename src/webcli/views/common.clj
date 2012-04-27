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
    (include-js "/CodeMirror-2.23/lib/codemirror.js")
    (include-css "/CodeMirror-2.23/lib/codemirror.css")
    (include-css "/CodeMirror-2.23/theme/lesser-dark.css")
    (include-js "/CodeMirror-2.23/mode/javascript/javascript.js")

    (include-css "/css/custom-theme/jquery-ui-1.8.19.custom.css")
    (include-js "/js/app.js")
    (include-js "/js/ui_demos.js")
    (include-js "/js/jquery-1.7.2.min.js")
    (include-js "/js/jquery-ui-1.8.19.custom.min.js")
    [:style {:type "text/css"}
     ".CodeMirror {border: 1px solid #eee; } "
     ;".CodeMirror-scroll { height: auto }"
    ; ".CodeMirror-scroll { height: " (/ 100 cmd-nr) " % }"
     ]
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

(defpartial textarea [id result]
(comment
[:div {:id "accordion" :class "ui-accordion ui-widget ui-helper-reset ui-accordion-icons" :role "tablist" }
  [:h3 {:class "ui-accordion-header ui-helper-reset ui-state-default ui-corner-all" :role "tab" :aria-expanded "false" :aria-selected "false" :tabindex "-1"}
    [:span {:class "ui-icon ui-icon-triangle-1-e"} ]
    [:a {:href "#" } "Section 1" ]
   ]
[:div {:class "ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom" :style "display: none; height: 122px; " :role "tabpanel" }
	[:p "Mauris mauris ante, blandit et" ]
]
[:h3 {:class "ui-accordion-header ui-helper-reset ui-state-active ui-corner-top" :role "tab" :aria-expanded "true" :aria-selected "true" :tabindex "0"}
 [:span {:class "ui-icon ui-icon-triangle-1-s"} ]
 [:a {:href "#" } "Section 2"]
]
[:div {:class "ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active" :style "display: block; height: 122px; padding-top: 11px; padding-bottom: 11px; overflow-x: auto; overflow-y: auto; " :role "tabpanel" }
 [:p "Sed non urna. Donec et ante." ]
 ]
 [:h3 {:class "ui-accordion-header ui-helper-reset ui-state-default ui-corner-all" :role "tab" :aria-expanded "false" :aria-selected "false" :tabindex "-1"}
  [:span {:class "ui-icon ui-icon-triangle-1-e"} ]
  [:a {:href "#" } "Section 3" ]
  ]
[:div {:class "ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom" :style "height: 122px; display: none; " :role "tabpanel" }
 [:p "Nam enim risus, motie et" ]
 [:ul
  [:li "List item one"]
  [:li "List item two"]
  [:li "List item three"]
  ]
 ]
 ]

;;;;;;
;;;;;;

[:div {:class "ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active" :style "display: block; height: 122px; padding-top: 11px; padding-bottom: 11px; overflow-x: auto; overflow-y: auto; " :role "tabpanel"}
  [:p 
         (if-not (nil? result)
           (doall result))
   ]
 ]
);comment
  [:span
      [:textarea {:id id}
         (if-not (nil? result)
           (doall result))
       ]
     ;[:div {:onclick "alert(onclick)"} "undo"]
     [:script
      "
      var editor = CodeMirror.fromTextArea(document.getElementById(\"" id "\"), {
      lineNumbers: true,
      extraKeys: {\"Ctrl-Space\": function(cm) {CodeMirror.simpleHint(cm, CodeMirror.javascriptHint);}}
      });
      editor.setOption(\"theme\", \"lesser-dark\");
      //editor.setOption(\"theme\", \"default\");   // this theme does not work properly
      "
      ]
   ]
)
