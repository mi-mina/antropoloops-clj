(ns aloops.oscapi
  (:import [oscP5 OscP5 OscMessage ]
           [netP5 NetAddress Logger])
  (:require [aloops.osc :as osc]))

(defn substring? [sub st]
  (if (not= (.indexOf st sub) -1)
    true
    false))

(defn event-to-keyword [message]
  (let [path (.addrPattern message)]
   (condp substring?  path
     "/live/name/clip" :clip
     "/live/clip/info" :info
     "/live/play" :play
     "/live/clip/loopend" :loopend
     "/live/volume" :volume
     "/live/solo" :solo
     "/live/tempo" :tempo
     (do (println "OSC-EVENT NOT FILTERED" path)))))

(defn process-osc-event [message]
  (let [osc-event (event-to-keyword message)]
    (condp = osc-event
      :clip (println "pasando por process-osc-event: clip")
      :info (println "pasando por process-osc-event: info")
      :play (println "pasando por process-osc-event: play")
      :volume (println "pasando por process-osc-event: volume")
      (do (println "not mapped. path: " (.addrPattern message)))))) ;; Important: If no default expression is provided and no clause matches, an
                                               ;; IllegalArgumentException is thrown.


(defn async-request-info-for-all-clips []
    (osc/send-osc-message (osc/make-osc-message "/live/name/clip")))

(defn init-oscP5-communication [papplet]
  (osc/init-oscP5 papplet))

#_(defn create-and-send-test-message []
  (-> (osc/make-osc-message "/test")
      (.add "123")
      (osc/send-osc-message)))
