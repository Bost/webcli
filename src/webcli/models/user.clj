^{:doc "This namespace contains business logic"} 
(ns webcli.models.user
  (:use
    [noir.validation :as vali]
    ))

; session must be a vector: the order of commands cannot be changed over time, the command can repeat several times. It contains for example:
; [("bost-desktop$ pwd\n" "/home/bost/dev/webcli\n") ("bost-desktop$ date\n" "Sat Apr 28 02:46:22 CEST 2012\n")]
(def session (atom []))

(defn reset-session []
  "Reset session to its init value"
  (reset! session []))

(defn add-to-session [cmd-result]
  (swap! session conj cmd-result))  ; add new command to the list

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
(def glob-cmd-nr (atom 1))

(defn getnr [cmd-str-nr]
  "Gets 3 from {:cmd-str \"pwd\" :cmd-nr 3}"
  (let [ prm-cmd-nr (get cmd-str-nr :cmd-nr) ]
    ;(println "getnr: " cmd-str-nr "; prm-cmd-nr: " prm-cmd-nr)
    (let [ ret-nr (if (nil? prm-cmd-nr)
                    @glob-cmd-nr ; TODO this should happen only when the page is first time openedyy
                    prm-cmd-nr   ; this needs to be converted to a number
                    )
          ]
      (read-string (str ret-nr))
      )
    )
  )

(defn getstr [cmd-str-nr]
  "Gets \"pwd\" from {:cmd-str \"pwd\" :cmd-nr 3}"
  (str (get cmd-str-nr :cmd-str)))

(defn valid? [{:keys [ cmd-str cmd-nr]}]
  ;(vali/rule (vali/has-value? cmd-str)
  ;           [:cmd-str "The command is empty."])
  (vali/rule (vali/has-value? cmd-nr)
             [:cmd-nr "The command nr is empty."])
  (not (vali/errors? :cmd-str :cmd-nr))
  )

(defn show-session []
  "Print the session on repl"
  (for [c @session] (print (first c))))

(defn get-cmd [result]
  (doall (first result)))

(defn get-response [result]
  (doall (rest result)))

