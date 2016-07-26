(ns aloops.oscapi
  (:import [oscP5 OscP5 OscMessage ]
           [netP5 NetAddress Logger])
  (:require [aloops.osc :as osc]))

(defn init-oscP5-communication [papplet]
  (osc/init-oscP5 papplet))

(defn create-and-send-test-message []
  (-> (osc/make-osc-message "/test")
      (.add "123")
      (osc/send-osc-message)))
