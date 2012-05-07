^{:doc "This namespace contains business logic"} 
(ns webcli.models.user
  (:use
    [noir.validation :as vali]
    ))

"Print evaluated expression and return its result"
(defmacro dbg[x]
  `(let [x# ~x]
     (println '~x "=" x#) x#
     )
  )

(defrecord Command [text result stats])

; session must be a vector: the order of commands cannot be changed over time, the command can repeat several times. It contains for example:
; [("bost-desktop$ pwd\n" "/home/bost/dev/webcli\n") ("bost-desktop$ date\n" "Sat Apr 28 02:46:22 CEST 2012\n")]
(def session (atom []))

(defn reset-session []
  "Reset session to its init value"
  (reset! session []))

(defn add-full-cmd-to-session [text result stats]
  "At first ignore the params text and stats"
  (def full-cmd (Command. text result stats))
  (swap! session conj full-cmd)  ; add new full command to the list
  )

(import '(java.io BufferedReader InputStreamReader)) 

(defn get-exec-time [start-time stop-time]
  "start/stop-time is in nano seconds"
  (str (- stop-time start-time) " [nano sec]")
  )

(defn exec-on-host [str-cmd]
  (try
    (.. Runtime getRuntime (exec str-cmd))
    (catch Exception e (str "caught exception: " (.getMessage e))))
  )

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

(defn exec [str-cmd]
  (let [
        start-time (System/nanoTime)
        java-lang-process (exec-on-host str-cmd)
        stop-time (System/nanoTime)
        buff-reader (get-buff-reader java-lang-process)
        ]
    {:result (line-seq buff-reader) :stats (get-exec-time start-time stop-time) }
  )
)

; initial value must be 1
(def glob-cmd-nr (atom 1))

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

(defn get-stats [full-cmd]
  (:stats full-cmd))

(defn get-result [full-cmd]
  (:result full-cmd))

(def prompt
  "TODO move the function for localhost to the model"
  (str
    ;uname -n   print the network node hostname
    ;(read-string (first (exec "uname -n")))   ; this is bash-specific
    (let [
          ; this is universal for JVM; TODO how is it for python-VM?
          localhost (java.net.InetAddress/getLocalHost)
          ]
      (.getHostName localhost))
    "$ "))

(defn add-to-session [cmd-str-nr]
   (let [
         cmd-str (:cmd-str cmd-str-nr)
         ret (exec cmd-str)
         cmd-stats (:stats ret)
         cmd-result
            (concat (list (str prompt cmd-str "\n"))
              (if (valid? cmd-str-nr)
                ; this creates a list of strings
                (map #(str % "\n") (:result ret))
                ))
         ]
     (add-full-cmd-to-session cmd-str cmd-result cmd-stats)
     )
  )
