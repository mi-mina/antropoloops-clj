#_(ns aloops.loopsapi
  (:require [aloops.oscapi :as oscapi]))

(def ready? (atom false))

;; antropoloops API???
;; Estas funciones no hacen mucho más que las que están envolviendo.
;; Quizá debería eliminar este namespace y pasarlo todo a oscapi?
(defn request-clips-info []
  (println "asking ableton for all clip's info")
  (oscapi/async-request-info-for-all-clips))

(defn request-tempo []
  (println "asking ableton for the tempo")
  (oscapi/async-request-tempo))

(defn request-track-info []
  (println "asking ableton for track related info: volume, mute and solo")
  (oscapi/async-request-volume-mute-solo))

;; Ask Ableton for initial info.
(future ;; creo una future para correr código en otro thread, para poderlo dormir y que no bloquee mi thread principal
  (Thread/sleep 2000) ;; espero un poco para asegurarme que el sketch está creado cuando empiece a recibir mensajes de ableton,
  ;; ya que los voy a procesar através de :osc-event
  (request-clips-info) ;; pregunto por el track, clip y name de todos los clips que hay
  (request-tempo)
  (request-track-info)
  (Thread/sleep 1000) ;; para darle tiempo a procesar la última request antes de quitar la splashscreen
  (reset! ready? true))

;; Qué pasa con el thread que abre la future? Se queda abierto?
;; Es eso un problema? Se puede cerrar él solo a sí mismo?


