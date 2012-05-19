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

"Print evaluated expression and return its result"
(defmacro dbg[x]
  `(let [x# ~x]
     (println '~x "=" x#) x#
     )
  )

(defpage "/" []
  ; i.e layout is defined in common.clj - see the (:require ...)
  (common/layout
   ;[:p "Welcome to awsome"]
   [:div#content]
   )
)

(defn getnr [cmd-str-nr]
  "Gets 3 from {:cmd-str \"pwd\" :cmd-nr 3}"
  (let [ prm-cmd-nr (:cmd-nr cmd-str-nr) ]
    ;(println "getnr: " cmd-str-nr "; prm-cmd-nr: " prm-cmd-nr)
    (let [ ret-nr
          (if (nil? prm-cmd-nr)
              ; TODO this should happen only when the page is 1st time opened
              @model/glob-cmd-nr
              prm-cmd-nr   ; this needs to be converted to a number
              )
          ]
      (read-string (str ret-nr))
      )
    )
  )

(defpage "/webcli" {:as cmd-str-nr }
 (common/layout (getnr cmd-str-nr)
   [:span
    [:div {:class "collapse_buttons" }
     [:button {:id "collapse_all"} "Collapse all" ]
     [:button {:id "expand_all"} "Expand all" ]
     ]
    [:ul {:id "sortable" :class "ui-sortable" }
     (result-area
       0
       (:text (model/cmd-env))
       (:result (model/cmd-env))
       (:stats (model/cmd-env))
       )

     (map-indexed
       ;#(result-area (str "code-" (inc %1)) (model/get-result %2) (model/get-stats %2))
       #(result-area
          (inc %1)
          (model/get-cmd (model/get-result %2))
          (model/get-result %2) (model/get-stats %2))
       @model/session)
     ]
    (form-to [:post "/webcli"]
             (command-fields cmd-str-nr)
             (submit-button "exec")
             )
    (form-to [:post "/reset"]
             (submit-button "reset" )
             )
    ]
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

