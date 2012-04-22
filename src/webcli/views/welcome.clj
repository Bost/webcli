(ns webcli.views.welcome
  (:require
            [webcli.views.common :as common]
            [noir.content.getting-started]
            [noir.validation :as vali]
            [noir.response :as resp]
            [noir.session :as session]
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
(in-ns 'webcli.views.welcome)
(load "../../webcli/views/welcome")
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

; initial value must be 1
(def *cmd-nr* (atom 1))

; TODO use stucture like this (with html5)
(defpartial layout [ cmd-nr & content]
  (println "layout: cmd-nr" cmd-nr)
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
    [:style {:type "text/css"} ".CodeMirror {border: 1px solid #eee; } .CodeMirror-scroll { height: " (/ 100 (read-string cmd-nr)) " % }" ]
    [:body
     content]))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpartial command-fields [{:keys [ cmd-str cmd-nr]}]
  (vali/on-error :cmd-str error-item)
  (label "cmd-str" "Command: ")
  (text-field "cmd-str" "ls -la")
  (label "cmd-nr" "cmd-nr: ")
  (text-field "cmd-nr" @*cmd-nr*) ;@*cmd-nr* is the same as (deref *cmd-nr*)
  )

(defn getnr [cmd-str-nr]
  (let [
        cmd-nr (get cmd-str-nr :cmd-nr)
        ]
    (if (nil? cmd-nr)
      (str @*cmd-nr*)
      cmd-nr
      )
    )
  )

(defn getstr [cmd-str-nr]
  (str (get cmd-str-nr :cmd-str)))

(defn valid? [{:keys [ cmd-str ]}]
  (vali/rule (vali/min-length? cmd-str 1)
             [:cmd-str "Your command " (getstr cmd-str) " must have at least 1 letter."])
  (not (vali/errors? :cmd-str))
  )

(defpage [:post "/webcli"] {:as cmd-str-nr}
 (let [
       cmd-nr  (getnr  cmd-str-nr)
       ]
   (if (valid? cmd-str-nr)
     (layout cmd-nr
             ;(println (str "Showing: " (getstr cmd-str-nr) "\n"))
             [:p (str "Command: " (getstr cmd-str-nr) " is valid") ]
             )
     )
   (render "/webcli" cmd-str-nr)
   )
 )

;(def s (hash-map :cmd-0 (hash-map :orig "orr-0" :curr "curr-0")))
(def my-session #{}) ; session is sorted set
;(def session (sorted-set :cmd-0 (hash-set :orig "" :curr (atom ""))))

(defpage "/webcli" {:as cmd-str-nr }
 (let [
       cmd-nr  (getnr  cmd-str-nr)
       ]
   (layout cmd-nr
     [:form
      [:textarea#code     ; #code makes the same as {:id "code"}
       (let [
             cmd-str (getstr cmd-str-nr)
             result (concat (list (str "$ " cmd-str "\n"))
                            (if (valid? cmd-str-nr)
                              (map #(str % "\n") (cmd cmd-str)) ; this creates a list of strings
                              ))
             ]
         (swap! *cmd-nr* inc)
         ;@(get (get @session :cmd-0) :curr)
         ; add new item to the session
         ;(conj s @c (hash-set :orig "" :curr (atom "")))
         ;(conj my-session @*counter* (hash-set :orig "" :curr (atom "")))

         ;(reset! session :cmd-0 (hash-set :orig result :curr (atom result)))
         (doall result)
         )
       ]
      ]
     ;[:div {:onclick "alert(onclick)"} "undo"]
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
              (command-fields cmd-str-nr)
              (submit-button "Execute command"))
     )
   )
 )

