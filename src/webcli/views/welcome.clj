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

(defpage "/webcli-new" {:as cmd-str-nr }
  (common/layout 1
(comment
[:ol {:class "message_list" }
 [:li
  [:p {:class "message_head"}
   [:cite "someone:" ]
   [:span {:class "timestamp"} "1 minute ago" ]
   ]
  [:div {:class "message_body"}
   [:p "hello 1 minute"]
   ]
  ]
 [:li
  [:p {:class "message_head"}
   [:cite "someone:" ]
   [:span {:class "timestamp"} "2 minutes ago" ]
   ]
  [:div {:class "message_body"}
   [:p "hello 2 minutes"]
   ]
  ]
 ]
);comment
  )
)

(defpage "/webcli" {:as cmd-str-nr }
 (let [
       cmd-nr (model/getnr cmd-str-nr)
       ]
   (common/layout cmd-nr
    [:ol {:class "message_list" }
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
         cmd-result
            (concat (list (str prompt cmd-str "\n"))
              (if (model/valid? cmd-str-nr)
                ; this creates a list of strings
                (map #(str % "\n") (model/cmd cmd-str))
                ))
         ]
     (model/add-to-session cmd-result)
     )
   )
  (render "/webcli" cmd-str-nr)
)


