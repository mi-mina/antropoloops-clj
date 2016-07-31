(ns aloops.main
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [aloops.oscapi :as oscapi]
            [aloops.graphics :as g]))

(def initial-width 1280)
(def initial-height 800)

(defn setup []
  (g/setup-graphics)

  ;; Cargo la imagen de fondo y la vinculo a la variable mundi
  (g/load-resources)

  ;; initial state
  {:img-sz {:ix 0
            :iy 0
            :img-width initial-width
            :img-height initial-height}
   :play 1})

(defn update-state [state]
  ;; Esta función adapta la imagen de fondo a la proporción de la pantalla
  (g/adapt-to-frame state)
  )

(defn draw [state]
  ;(apply q/background (:bg-color state))
  (let [img-sz (:img-sz state)
        ix (:ix img-sz)
        iy (:iy img-sz)
        iwidth (:img-width img-sz)
        iheight (:img-height img-sz)
        factor (/ iwidth 1280) ;; 1280 es el ancho de la imagen sobre la cual se han medido las coordenadas
        ]

  (g/draw-background ix iy iwidth iheight)

  (when (= 2 (:play state))
    (let [active-loops (map first (filter #(= 2 (val %)) (:loops-state state)))]
      (doseq [loop-index active-loops]
        (g/draw-abanica-in-place loop-index ix iy iwidth iheight factor)
        (g/draw-album-covers loop-index ix iy iwidth iheight))))


  (g/draw-splash-screen ix iy iwidth iheight)
  ))

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
             :size  [initial-width initial-height] ;; :fullscreen ;;
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
