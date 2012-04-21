(ns webcli.views.welcome
  (:require [webcli.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers]
        ))

(comment  ; use these commands on repl
(load "../webcli/views/welcome")
(in-ns 'webcli.views.welcome)
); comment

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to webcli"]))

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

(defpage "/my-page" []
  (html
    ;(include-css "/css/embed.css")
    (include-css "/css/noir.css")
    (include-css "/css/gist.css")
    ;(include-css "https://gist.github.com/stylesheets/gist/embed.css")
    ;(include-css "/css/reset.css")
    ;[:h1 "This is my first page!"]
    ;[:h1 (str "cmd: " (cmd "ls -la"))]
    [:pre
     [:div 
      {:class "gist gist-data gist-syntax" } 
      (map #(html [:div %]) (cmd "ls -la"))]
     ]
    ))


