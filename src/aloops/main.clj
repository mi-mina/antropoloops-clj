(ns aloops.main
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [aloops.oscapi :as oscapi]
            [aloops.graphics :as g]
            [aloops.loopsapi :as loopsapi]))



(def initial-width 1200)
(def initial-height 750)


(def ready? (atom false))

#_(defn ask-ready? []
  (if @ready?
    (loopsapi/request-clips-loopend)
    (do
      (Thread/sleep 500)
      (ask-ready?))))



(future ;; creo una future para correr código en otro thread, para poderlo parar y que no bloquee mi thread principal
  (Thread/sleep 500) ;; espero un poco para asrgurarme que el sketch está creado cuando empiece a recibir mensajes de ableton,
                     ;; ya que los voy a procesar através de :osc-event
  (loopsapi/request-clips-info) ;; pregunto por el track, clip y name de todos los clips que hay
  ;(ask-ready?)
  )



(defn setup []
  ;; Cargo la imagen de fondo y la vinculo a la variable mundi
  (g/load-resources)

  ;; Pregunto por la información básica de cada clip, pero lo hago a través de una future
  ;; (en otro thread) durmiéndolo primero medio segundo para asegurarme de que el estado inicial
  ;; existe antes de que ableton empieze a responder, ya que voy a guardar el procesado de las repuestas
  ;; en el estado de la aplicación, dentro de :antropoloops


  ;; initial state
  ;; TODO podría meter todo lo relacionado con la imagen de fondo (:ix :iy ...) en un mapa asociado a una sola key
  {:img-sz {:ix 0
            :iy 0
            :img-width initial-width
            :img-height initial-height}
   :loops-info {}}
  )



(defn update-state [state]
  ;; Esta función adapta la imagen de fondo a la proporción de la pantalla
  (g/adapt-to-frame state)
  )


(defn draw [state]
  ;(apply q/background (:bg-color state))
  (g/draw-background state)
  )

(defn mouse-clicked [state event]
  (println "state" state)
  (println "loops-info" oscapi/loops-info)
  state)

(defn osc-event [state message]
  (println "pasando por osc-event-fn. mensage: " message)
  (oscapi/process-osc-event message)
  state
  )

(q/defsketch papplet
             :title "osc"
             :setup setup
             :draw draw
             :size [initial-width initial-height] ;; :fullscreen
             :update update-state
             :mouse-clicked mouse-clicked
             :osc-event osc-event
             ;; :diplay 1
             :features [:keep-on-top
                        ;:exit-on-close
                        :no-bind-output
                        :resizable
                        ;; :present
                        ]
             :middleware [m/fun-mode])

(oscapi/init-oscP5-communication papplet)

(defn -main [& args] )
