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
  ; i.e layout is defined in common.clj - see the (:require ...)
  (common/layout
   ;[:p "Welcome to awsome"]
   [:div#content]
   )
)

(defpage "/webcli" {:as cmd-str-nr }
 (let [
       cmd-nr (model/getnr cmd-str-nr)
       ]
   (common/layout cmd-nr
  [:span
    [:ol {:class "message_list" }
      (map-indexed #(result-area (str "code-" (inc %1)) %2) @model/session)
    ]
[:p {:class "collapse_buttons" }
; [:a {:href "#" :class "show_all_message"} "Show all" ]
; [:a {:href "#" :class "show_recent_only"} "Show 5 only" ]
 [:a {:href "#" :class "collpase_all_message"} "Collapse all" ]
 [:a {:href "#" :class "expand_all_message"} "Expand all" ]
 ]
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
     (model/add-to-session cmd-str-nr)
     )
  (render "/webcli" cmd-str-nr)
)

