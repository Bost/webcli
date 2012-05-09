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
     [:title "web command line interface"]
    (include-css "/css/collapsible-panels/style.css")
    (include-css "/css/custom-theme/jquery-ui-1.8.19.custom.css")
    (include-js "/js/jquery-latest/jquery-1.7.2.min.js")
    (include-js "/js/jquery-ui-1.8.19.custom.min.js")

[:script {:type "text/javascript"} "
$(document).ready(function(){

	//hide message_body after the first one
	//$(\".message_list .message_body:gt(0)\").hide();

	//hide message li after the 5th
	//$(\".message_list li:gt(4)\").hide();

	//toggle message_body
	$(\".message_head\").click(function(){
		$(this).next(\".message_body\").slideToggle(500)
		return false;
	});

	$(\".expand_all_message\").click(function(){
		$(\".message_body\").slideDown()
		return false;
	}); 

	$(\"#system-env\").slideToggle(0);

	$(\".collpase_all_message\").click(function(){
		$(\".message_body\").slideUp(500)
		return false;
	});

	//show all messages
	$(\".show_all_message\").click(function(){
		$(this).hide()
		$(\".show_recent_only\").show()
		$(\".message_list li:gt(4)\").slideDown()
		return false;
	});

	//show recent messages only
	$(\".show_recent_only\").click(function(){
		$(this).hide()
		$(\".show_all_message\").show()
		$(\".message_list li:gt(4)\").slideUp()
		return false;
	});

});
"]
     ];head

    [:body
     content
       ]))

(defpartial error-item [[first-error]]
  [:p.error first-error])


^{:doc "TODO input validation should be made on controler" }
(defpartial command-fields [{:keys [ cmd-str cmd-nr]}]
  (vali/on-error :cmd-str error-item)
  (label "cmd-str" model/prompt)

;<input id="autocomplete" style="z-index: 100; position: relative" title="type &quot;a&quot;" class="ui-autocomplete-input" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true">

  ;(text-field "cmd-str" "ls -la")
  ;[:div
  (text-field {:class "ui-autocomplete-input"}
              "cmd-str" "pwd")
  ; ]
  (label "cmd-nr" "cmd-nr: ")
  (text-field {:class "ui-autocomplete-input"}
    "cmd-nr" @model/glob-cmd-nr)  ;@glob-cmd-nr is the same as (deref glob-cmd-nr)
  )

(defn escape-str [s0]
  (let [
        s1 (clojure.string/replace s0 #" " "&nbsp;")
        s2 (clojure.string/replace s1 #"\n" "<br />")
        ]
    s2)
  )

^{:doc
  "Result contains a comand and response to it. I.e."
  "                     cmd         response"
  "    (\"bost-desktop$ pwd\n\" \"/home/bost/dev/webcli\n\")" }
(defpartial result-area [id result stats]
 [:li
  [:p {:class "message_head"}
   [:cite (model/get-cmd result) ]
   [:span {:class "timestamp"} stats ]
   ]
  [:div {:id id :class "message_body"}
   [:p
     (map #(escape-str %) (vec (model/get-response result)))
    ]
   ]
  ]
)
