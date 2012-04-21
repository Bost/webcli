(ns webcli.views.welcome
  (:require [webcli.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core
         ;:only [defpage]
         ]
        [hiccup.core
         ;:only [html]
         ]
        [hiccup.page-helpers]
        [hiccup.form-helpers]
        ))

(comment  ; use these commands on repl
(load "../webcli/views/welcome")
(in-ns 'webcli.views.welcome)
); comment

(import '(java.io BufferedReader InputStreamReader)) 

(defn exec-cmd [str-cmd]
  (.. Runtime getRuntime (exec (str str-cmd))))

;; URL url = new URL(elem.toString());
;; URLConnection con = url.openConnection();
;; BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream(), con.getContentEncoding()));
;; or try this
;; BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));  
(defn get-buff-reader [url-connection]
  ;(println "Encoding: " (.getContentEncoding url-connection))
  (BufferedReader. 
      (InputStreamReader. 
        (.getInputStream url-connection))))


(defn cmd [str-cmd]
  (let [
        java-lang-process (exec-cmd str-cmd)
        buff-reader (get-buff-reader java-lang-process)
        ]
    (line-seq buff-reader))
  )

(defpage "/webcli" []
  (html
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
    ;; following line together with '(include-css "/css/noir.css")' makes the inner frame
    [:style {:type "text/css"} ".CodeMirror {border: 1px solid #eee; } .CodeMirror-scroll { height: 100% }" ]
    [:body
     [:form
      [:textarea {:id "code"}
       (map #(str % "\n") (cmd "ls -la"))
       ;"function getCompletions(token, context) {}"
       ]
      ]
     [:script
      "
      var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {
      lineNumbers: true,
      extraKeys: {\"Ctrl-Space\": function(cm) {CodeMirror.simpleHint(cm, CodeMirror.javascriptHint);}}
      });
      editor.setOption(\"theme\", \"lesser-dark\");
      //editor.setOption(\"theme\", \"default\");   // this theme does not work properly
      "
      ]
     ]
    )
  )

(comment
(defpartial layout [& content]
  (html5
    [:head
     [:title "Forms"]]
    [:body
     content]))

(defpartial user-fields [{:keys [firstname lastname]}]
  (label "firstname" "First name: ")
  (text-field "firstname" firstname)
  (label "lastname" "Last name: ")
  (text-field "lastname" lastname))

(defpage "/user/add" {:as user}
  (layout
    (form-to [:post "/user/add"]
            (user-fields user)
            (submit-button "Add user"))))
);comment
