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
(defmacro dbgx[x]
  `(let [x# ~x]
     (println '~x "=" x#) x#
     )
  )

; TODO use stucture like this (with html5)
(defpartial layout [ cmd-nr & content]
  ;(println "layout: cmd-nr" cmd-nr)
  (html5
    [:head
     [:title "web command line interface"]
     (include-js  "/jquery/js/jquery-1.7.2.min.js")
     (include-js  "/jquery/js/jquery-ui-1.8.20.custom.min.js")
     (include-css "/jquery/css/custom-theme/jquery-ui-1.8.20.custom.css")
     (include-css "/css/cheatsheet.css")

     ;(include-js "/js/terminal/jquery.mousewheel-min.js")
     ;(include-js "/js/terminal/jquery.terminal-0.4.15.min.js") - cannot be include because of the error message color specified at line 1673
     ;(include-js "/js/terminal/jquery.terminal-0.4.15.js")
     ;(include-css "/css/terminal/jquery.terminal.css")
     ;(include-css "/css/terminal/jquery.terminal.css")
     ];head
    [:body
     [:div#wrapper
      content
      ]
[:script {:type "text/javascript"} "
	var eMaxIdx = 12; // TODO eMaxIdx must be generated dynamically
	var idPrefix = \"head\";

	function getId(idx) {
		return \"#\"+idPrefix + idx;
	}
	function getIds(maxIdx) {
		var ids = \"\";
		for (var i = 0; i < maxIdx; i++) {
			if (i > 0) {
				ids += \", \";
			}
			ids += getId(i);
		}
		return ids;
	}
	var ids = getIds(eMaxIdx);
	$(function() {
		function runEffect(divId) {
			// other effect must be downloaded from jquery theme roller
			var selectedEffect = \"blind\";

			// most effect types need no options passed by default
			var options = {};
			// some effects have required parameters
			if ( selectedEffect === \"scale\" ) {
				options = { percent: 0 };
			} else if ( selectedEffect === \"size\" ) {
				options = { to: { width: 200, height: 60 } };
			}
			//var elem = $( \"#\"+divId +	\" .effect\" );
			var elem = $(\"#\"+divId).next();
			elem.toggle( selectedEffect, options, 360 );
		};

		$(ids).click(function() {
			runEffect(this.id);
			return false;
		});
	});
	$(function() {
		var el = $(\"#sortable\");
		el.sortable();
		el.disableSelection();
		//	$( \".resizable\" ).resizable();
		//	$( \".draggable\" ).draggable();
	});
	$(function() {
		//$( \"input:submit, a, button\", \".buttons\" ).button();
		//$( \"a\", \".buttons\" ).click(function() { return false; });
	});
	$(function() {
		$(\"#expand_all\").click(function(){
			for (var i = 0; i < eMaxIdx; i++) {
				var accId = getId(i);
				var elem = $(accId).next();
				elem.show();
			}
			return false;
		});
		$(\"#collapse_all\").click(function(){
			for (var i = 0; i < eMaxIdx; i++) {
				var accId = getId(i);
				var elem = $(accId).next();
				elem.hide();
			}
			return false;
		});
	});

/* jQuery(function($, undefined) {
    $(\"#term_demo\").terminal(function(command, term) {
        if (command !== \"\") {
            var result = window.eval(command);
            if (result != undefined) {
                term.echo(String(result));
            }
        }
    }, {
        greetings: \"Javascript Interpreter\",
        name: \"js_demo\",
        height: 200,
        prompt: \"js>\"});
});*/
"]
     ;(cljs/include-scripts :with-jquery) ;includes jquery 1.7.1 but I need 1.7.2
     (cljs/include-scripts)
     ]))

(defpartial error-item [[first-error]]
  [:p.error first-error])


^{:doc "TODO input validation should be made on controler" }
(defpartial command-fields [{:keys [ cmd-str cmd-nr]}]
  (vali/on-error :cmd-str error-item)
  (label "cmd-str" model/prompt)

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
(defpartial
  result-area [id text result stats]
  [:li {:id (str "acc" id) :class "acc ui-corner-all resizable draggable"}
   [:span {:class "ui-icon ui-icon-arrowthick-2-n-s"} ]
   [:div {:id (str "head" id) :class "head ui-widget-header ui-corner-all ui-button-text"}
     text
    ]
   [:div {:class "effect ui-corner-all"}
    (map #(escape-str %) (vec (model/get-response result)))
    ]
   ]
  )
