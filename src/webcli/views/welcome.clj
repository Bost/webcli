(ns webcli.views.welcome
  (:require
            [webcli.views.common :as common]
            [noir.content.getting-started]
            [noir.validation :as vali]
            [noir.response :as resp]
    )
  (:use [noir.core
         ;:only [defpage]
         ]
        [hiccup.core
         ;:only [html]
         ]
        [hiccup.page-helpers]
        [hiccup.form-helpers]
        )
  )

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
  ;(println (str "cmd: " str-cmd))
  (let [
        java-lang-process (exec-cmd str-cmd)
        buff-reader (get-buff-reader java-lang-process)
        ]
    (line-seq buff-reader))
  )

; TODO use stucture like this (with html5)
(defpartial layout [& content]
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
    [:style {:type "text/css"} ".CodeMirror {border: 1px solid #eee; } .CodeMirror-scroll { height: 98% }" ]
    [:body
     content]))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpartial command-fields [{:keys [ str-cmd ]}]
  (vali/on-error :command error-item)
  (label "command" "Command: ")
  ;(text-field "command" str-cmd)
  (text-field "command" "ls -la")
  )

(defn valid? [{:keys [ command ]}]
  (vali/rule (vali/min-length? command 1)
             [:command "Your command " (getstr command) " must have at least 1 letter."])
  (not (vali/errors? :command))
  )
(defn getstr [command]
  (str (get command :command)))

(defpage [:post "/webcli"] {:as command}
 (if (valid? command)
   (layout
     ;(println (str "Showing: " (getstr command) "\n"))
     [:p (str "Command: " (getstr command) " is valid") ]
     )
   )
 (render "/webcli" command)
 )

(defpage "/webcli" {:as command}
 (layout
   [:form
    [:textarea#code     ; #code makes the same as {:id "code"}
     (let [
           strcmd (getstr command)
           ]
       (concat (list (str "$ " strcmd "\n"))
               (if (valid? command)
                 (map #(str % "\n") (cmd strcmd)) ; this creates a list of strings
                 )
               )
       )
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
   (form-to [:post "/webcli"]
            (command-fields command)
            (submit-button "Execute command"))
   )
 )

