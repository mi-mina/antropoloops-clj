(ns aloops.main
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [aloops.oscapi :as oscapi]
            [aloops.graphics :as g]
            [aloops.util :as u]
            [quil.helpers.seqs :as h]))

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
   :play 1
   :tempo 120
   :tracks-info {}
   :loops-state {}
   :last-loop :00 ;;pongo un valor inicial para que no pete. Da igual si no es el último loop, porque tiene que pasar
                  ;; por la comprobación de estar entre los loops activos
   :diam (h/seq->stream (h/range-incl 10 400 20))})

(defn update-state [state]
  ;; Esta función adapta la imagen de fondo a la proporción de la pantalla
  (g/adapt-to-frame state))

(defn draw [state]
  ;(apply q/background (:bg-color state))
  (let [img-sz (:img-sz state)
        ix (:ix img-sz)
        iy (:iy img-sz)
        iwidth (:img-width img-sz)
        iheight (:img-height img-sz)
        factor (/ iwidth 1280)
        last-loop (:last-loop state)] ;; 1280 es el ancho de la imagen sobre la cual se han medido las coordenadas

    ;; (println (q/current-frame-rate)) El frame-rate debería ser cercano a 60 y no llega a 8.
    (g/draw-background ix iy iwidth iheight)

    (when (= 2 (:play state))
      (let [clips-play-on (map first (filter #(= 2 (val %)) (:loops-state state)))
            tracks-mute-on (->> (filter #(= 1 (:mute (val %))) (:tracks-info state))
                                (map (comp read-string name first)))
            tracks-solo-on (->> (filter #(= 1 (:solo (val %))) (:tracks-info state))
                                (map (comp read-string name first)))
            active-loops (if (empty? tracks-solo-on)
                           (reduce
                             #(if (some (fn [x] (= x (u/get-track-int %2))) tracks-mute-on) % (conj % %2))
                             [] clips-play-on)
                           (filter #(= (first tracks-solo-on) (u/get-track-int %)) clips-play-on))]
        (doseq [loop-index active-loops]
          (g/draw-abanica-in-place loop-index ix iy factor state)
          (g/draw-album-covers loop-index ix iy iheight factor state))
        (g/draw-last-loop last-loop active-loops ix iy iwidth iheight state)
        ))

    (g/draw-splash-screen ix iy iwidth iheight)))

(defn mouse-clicked [state event]
  (println "state" state)
  ;(println "loops-info" @oscapi/loops-info)
  ;(println "loopends" @oscapi/loopends)
  (println "wave" @oscapi/wave)

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
