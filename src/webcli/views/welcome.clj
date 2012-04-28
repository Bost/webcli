(ns webcli.views.welcome
  (:require
    [webcli.views.common :as common]
    [webcli.models.user :as model]
    )
  (:use
    [noir.core :only [defpage render]]
    [hiccup.core :only [html]]
    [hiccup.form] ;:only [label text-field form-to]
    [webcli.views.common]
    ))

(defpage "/" []
         (common/layout   ; i.e layout is defined in common.clj - see the (:require ...)
           ;[:p "Welcome to awsome"]
           [:div#content]
           ))

(defpage "/webcli" {:as cmd-str-nr }
 (let [
       cmd-nr (model/getnr cmd-str-nr)
       ]
   (common/layout cmd-nr
    [:div {:id "accordion" }
      (map-indexed #(result-area (str "code-" (inc %1)) %2) @model/session)
      ]
     (form-to [:post "/webcli"]
              (command-fields cmd-str-nr)
              (submit-button "exec")
              )
     (form-to [:post "/reset"]
              (submit-button "reset" )
              )
     )
   )
 )

(defpage [:post "/reset"] {:as cmd-str-nr}
  (model/reset-session)
  (render "/webcli" cmd-str-nr)
)

(defpage [:post "/webcli"] {:as cmd-str-nr}
 (if (model/valid? cmd-str-nr)
   (let [
         cmd-str (model/getstr cmd-str-nr)
         cmd-result (concat (list (str prompt cmd-str "\n"))
                        (if (model/valid? cmd-str-nr)
                          (map #(str % "\n") (model/cmd cmd-str)) ; this creates a list of strings
                          ))
         ]
     (model/add-to-session cmd-result)
     )
   )
  (render "/webcli" cmd-str-nr)
)


