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
    ;(include-css "/css/embed.css")
    (include-css "/css/noir.css")
    ;(include-css "/css/gist.css")
    ;(include-css "https://gist.github.com/stylesheets/gist/embed.css")
    ;(include-css "/css/reset.css")
    ;(include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js")
    ;; following line together with '(include-css "/css/noir.css")' makes the inner frame
    [:style {:type "text/css"}
     ".CodeMirror {border: 1px solid #eee; } "
     ;".CodeMirror-scroll { height: auto }"
     ".CodeMirror-scroll { height: " (/ 100 cmd-nr) " % }"
     ]
    [:script
     "
     function scrollDown () {
       var elem = document.getElementById('in-form');
       elem.scrollTop = elem.scrollHeight;
     }"
     ]
    [:body
      [:span {:onclick "alert();scrollDown()"} "aaaaaaa"]
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

(defpartial command-fields [{:keys [ cmd-str cmd-nr]}]
  "TODO input validation should be made on controler"
  (vali/on-error :cmd-str error-item)
  (label "cmd-str" prompt)
  ;(text-field "cmd-str" "ls -la")
  (text-field "cmd-str" "pwd")
  (label "cmd-nr" "cmd-nr: ")
  (text-field "cmd-nr" @model/glob-cmd-nr)  ;@glob-cmd-nr is the same as (deref glob-cmd-nr)
  )

(defpartial textarea [id result]
  ;(println "textarea: id: " id)
  ;(println "textarea: result: " result)
  ;(println "textarea: ------------")
  [:span
      [:textarea {:id id}
         (if-not (nil? result)
           (doall result)
           )
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
