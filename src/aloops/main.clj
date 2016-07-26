(ns aloops.main
  (:import [oscP5 OscP5 OscMessage]
           [netP5 NetAddress])
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [aloops.osc :as osc]
            [aloops.oscapi :as oscapi]))


(def bg [25 25 25])

(defn setup []
  ;; initial state
  {:bg-color bg})

;; No need of update function in this example
;; (defn update [state]   )

(defn draw [state]
  (apply q/background (:bg-color state)))

(defn mouse-clicked [state event]
  (oscapi/create-and-send-test-message)
  state)

(defn alter-bg-color []
  (map #(* % (rand-int 10)) bg))

(defn osc-event [state message]
  (println "mensage recibido en la aplicación: " message)
  (println (.addrPattern message))
  (-> state
      (assoc :bg-color (alter-bg-color))))

(q/defsketch papplet
             :title "osc"
             :setup setup
             :draw draw
             :size :fullscreen
             :mouse-clicked mouse-clicked
             :osc-event osc-event
             :features [:keep-on-top
                        :exit-on-close
                        :no-bind-output
                        :present]
             :middleware [m/fun-mode])

(oscapi/init-oscP5-communication papplet)

(defn -main [& args] )
