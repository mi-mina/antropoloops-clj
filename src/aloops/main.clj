(ns aloops.main
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [aloops.oscapi :as oscapi]
            [aloops.graphics :as g]
            [aloops.loopsapi :as loopsapi]))

(def initial-width 1200)
(def initial-height 750)

(defn setup []
  ;; Cargo la imagen de fondo y la vinculo a la variable mundi
  (g/load-resources)

  ;; initial state
  {:img-sz {:ix 0
            :iy 0
            :img-width initial-width
            :img-height initial-height}})

(defn update-state [state]
  ;; Esta función adapta la imagen de fondo a la proporción de la pantalla
  (g/adapt-to-frame state)
  )

(defn draw [state]
  ;(apply q/background (:bg-color state))
  (g/draw-background state)
  (g/draw-splash-screen state)
  )

(defn mouse-clicked [state event]
  (println "state" state)
  (println "loops-info" @oscapi/loops-info)
  state)

(defn osc-event [state message]
  ;(println "pasando por osc-event-fn. mensage: " message)
  (oscapi/process-osc-event state message))

(q/defsketch papplet
             :title "osc"
             :setup setup
             :draw draw
             :size [initial-width initial-height] ;; :fullscreen
             :update update-state
             :mouse-clicked mouse-clicked
             :osc-event osc-event
             ;; :diplay 1
             :features [;:keep-on-top
                        :exit-on-close
                        :no-bind-output
                        :resizable
                        ;; :present
                        ]
             :middleware [m/fun-mode])

(oscapi/init-oscP5-communication papplet)

(defn -main [& args] )
