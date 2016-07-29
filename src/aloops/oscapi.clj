(ns aloops.oscapi
  (:require [quil.core :as q]
            [aloops.osc :as osc]
            [aloops.bd :as bd]))

;; Aquí en teoría no debería haber nada que use oscP5 directamente no?
;; No tengo muy clara cuál sería la diferencia entre este ns y loopsapi

(def loops-info (atom {}))
;; De momento he definido loops-info como un atom. No sé si hubiera gran cantidad de clips, por ejemplo, en una sesión entera
;; estaría garantizado que se añaden todos los clips o podría quedarse alguno fuera si el mensaje de respuesta de ableton
;; llega cuando todavía el atom no ha terminado de actualizarse por el mensaje anterior.

;; En loops-info guardo toda la información que pido una vez al comienzo de la sesión y no la vuelvo a pedir más a no
;; ser que se cambie de sesión. (habría que resetear la aplicación entera)

(defn load-loops-info [message]
  ;; TODO: check that exist a place and a song if not throw an exception
  (let [[track clip nombre] (.arguments message)
        bd-song (first (filter #(= (:nombreArchivo %) nombre) bd/loops)) ;; pido el first porque el resultado es una secuencia de un elemento ({})
        bd-lugar (first (filter #(= (:lugar %) (:lugar bd-song)) bd/lugares))
        aloop {:track track
               :clip clip
               :nombre nombre
               :titulo (:titulo bd-song)
               :album (:album bd-song)
               :artista (:artista bd-song)
               :fecha (:fecha bd-song)
               :color-s (q/random 50 100 )
               :color-b (q/random 80 100)
               :color-h (condp = (int track) ;; Hay que pasarlo a integer?
                          0 (q/random 105 120)
                          1 (q/random 145 160)
                          2 (q/random 300 315)
                          3 (q/random 330 345)
                          4 (q/random 195 210)
                          5 (q/random 230 245)
                          6 (q/random 25 40)
                          7 (q/random 50 65))
               :image (q/load-image (str "resources/0_portadas/" nombre ".jpg"))
               :lugar (:lugar bd-lugar)
               :x (:coordX bd-lugar)
               :y (:coordY bd-lugar)}]
    (swap! loops-info assoc (select-keys aloop [:track :clip]) aloop)))




;; cada key debería estar asociada a una función definida fuera de process-osc-event
;; para que process-osc-event se pueda leer bien

;; Como no voy a usar event-to-keyword para pasar los mensajes a keyword hago cambios en esta función
(defn process-osc-event [message]
  (let [path (osc/get-address-pattern message)]
    (condp = path
      "/live/name/clip"        (load-loops-info message)
      "/live/name/clip/done"   (println ":done")
      "/live/clip/info"        (println "track" (first (.arguments message)) "clip" (second (.arguments message)) "state" (last (.arguments message)))
      "/live/clip/loopend"     (println "track" (first (.arguments message)) "clip" (second (.arguments message)) "loopend" (last (.arguments message)))
                               ;; Aunque la pregunta es con /live/clip/loopend_id, la respuesta es con /live/clip/loopend
      "/live/volume"           (println "track" (first (.arguments message)) "volume" (second (.arguments message)))
      "/live/solo"             (println "track" (first (.arguments message)) "solo state" (second (.arguments message)))
      "/live/mute"             (println "track" (first (.arguments message)) "mute state" (second (.arguments message)))
      "/live/tempo"            (println "tempo" (first (.arguments message)))
      "/live/play"             (println "general play state" (first (.arguments message)))
      "/live/stop"             (println "general stop state" (first (.arguments message)))

      (do (println "not mapped. path: " (osc/get-address-pattern message))))))

;
(defn async-request-info-for-all-clips []
  (osc/send-osc-message (osc/make-osc-message "/live/name/clip"))

  #_(-> (osc/make-osc-message "/live/clip/loopend_id")
        (.add (int-array [0 0]))
        (osc/send-osc-message))
  #_(-> (osc/make-osc-message "/live/volume")
        (.add 0)
        (osc/send-osc-message))
  #_(-> (osc/make-osc-message "/live/mute")
        (.add 0)
        (osc/send-osc-message))
  )



(defn init-oscP5-communication [papplet]
  (osc/init-oscP5 papplet))


;; Lo que devuelve cada path
#_(
     "/live/name/clip"  ;; returns (int track, int clip, string name)
     "/live/clip/info"  ;; returns (int track, int clip, int state) [state: 0 = no clip, 1 = has clip, 2 = playing, 3 = triggered]
     "/live/play"  ;; returns (int state) [2 = playing, 1 = stopped]
     "/live/clip/loopend_id"  ;; returns (int track, int clip, float loopend)
     "/live/volume"  ;; returns (int track float volume)
     "/live/solo"  ;; returns (int track int state)
     "/live/mute"  ;; returns (int track int state)
     "/live/tempo"  ;; returns (float tempo)
     "/live/name/clip/done"   ;; mensaje que yo he añadido para saber cuándo ha terminado de enviar mensajes "/live/name/clip"
)
