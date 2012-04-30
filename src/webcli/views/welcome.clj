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
  (common/layout 1
[:span
[:script {:type "text/javascript"} "
$(\"html\").addClass(\"js\")
$(function() {
  $(\"#side\").accordion({initShow : \"#current\"})
//  $(\"#main\").accordion({
//      objID: \"#acc1\",
//      el: \".h\",
//      head: \"h4, h5\",
//      next: \"div\",
//      initShow : \"div.shown\",
//      standardExpansible : true
//  })
  $(\"#main\").accordion({
      objID: \"#acc2\",
      obj: \"div\",
      wrapper: \"div\",
      el: \".h\",
      head: \"h4, h5\",
      next: \"div\",
      initShow : \"div.shown\",
      standardExpansible : true
    })
//  $(\"#main .accordion\").expandAll({
//      trigger: \".h\",
//      ref: \"h4.h\",
//      cllpsEl : \"div.outer\",
//      speed: 200,
//      oneSwitch : false,
//      instantHide: true
  })
//  /* -----------------------
//  $(\"#side ul.accordion\").expandAll({
//      trigger: \"li\",
//      ref: \"\",
//      cllpsEl : \"ul\",
//      state : '',
//      oneSwitch : false
//  })
//  ------------------------ */
  $(\"html\").removeClass(\"js\")
"]

[:div {:id "wrapper" }
 [:div {:id "content"}
  [:div {:id "container"}
   [:div {:id "main"}
    [:div {:class "accordion" :id "acc2"}

     [:div {:class "new"}
      [:h4 {:class "h"}
       [:a {:class "trigger" :style "display:block" :href "#"}
        "Heading x3"
        ]
       ]
      [:div {:class "outer" :style "display: none "}
       [:div {:class "inner shown" }
        [:p "3. - shown" ]
        ]
       ]
      ]

     [:div {:class "new"}
      [:h4 {:class "h" }
       [:a {:class "trigger" :style "display:block" :href "#"}
        "Heading x4"
        ]
       ]
      [:div {:class "outer" :style "display: none " }
       [:div {:class "inner shown" }
        [:p "4. - shown" ]
        ]
       ]
      ]
     ]
    ]
   ]
  ]
 ]
];span
  )
)

(defpage "/webcli-old" {:as cmd-str-nr }
 (let [
       cmd-nr (model/getnr cmd-str-nr)
       ]
   (common/layout cmd-nr
    [:div {:id "multiOpenAccordion" :style "width: 50%"}
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


