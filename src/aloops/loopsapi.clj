(ns aloops.loopsapi
  (:require [aloops.bd :as bd]
            [aloops.oscapi :as oscapi]))


(def ready? (atom false))

;; antropoloops API???

(defn request-clips-info []
  (println "asking ableton about all clip's info")
  (oscapi/async-request-info-for-all-clips))

(defn ask-for-further-info []
  (if @oscapi/clip-info-received?
    (do
      (println "asking for further info: loopend, tempo, volume, mute, solo")
      (oscapi/async-request-clips-loopend)
      (oscapi/async-request-tempo)
      (oscapi/async-request-volume-mute-solo)
      (oscapi/async-request-clip-state)
      (Thread/sleep 1000)
      (reset! ready? true))
    (do
      (println "waiting until the whole bundle of clip's info messages are sended before asking further information ...")
      (Thread/sleep 1000)
      (ask-for-further-info))))


;; Ask Ableton for initial info.
(future ;; creo una future para correr código en otro thread, para poderlo dormir y que no bloquee mi thread principal
  (Thread/sleep 2000) ;; espero un poco para asegurarme que el sketch está creado cuando empiece a recibir mensajes de ableton,
                      ;; ya que los voy a procesar através de :osc-event
  (request-clips-info) ;; pregunto por el track, clip y name de todos los clips que hay
  (ask-for-further-info)) ;; cuando ya se han enviado terminado de recibir todos los mensajes live/name/clip, pregunto por el resto

;; Qué pasa con el thread que abre la future? Se queda abierto?
;; Es eso un problema? Se puede cerrar él solo a sí mismo?
