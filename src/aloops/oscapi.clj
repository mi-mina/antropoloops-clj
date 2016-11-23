(ns aloops.oscapi
  (:require [quil.core :as q]
            [quil.helpers.seqs :as h]
            [aloops.osc :as osc]
            [aloops.bd :as bd]))

;; No tengo muy clara cuál sería la diferencia entre este ns y loopsapi

;; A continuación defino varios atoms donde guardo partes del estado de la aplicación.
;; que no guardo en state por diversas razones.
;; En realidad son singletons (una única instancia y con acceso global), con lo cual
;; no estoy manejando el estado muy bien.
;; Además tengo el estado de la applicación dividido entre estos atoms globales
;; y el state que genera quil y que es pasado por las funciones setup, update, etc.
;; TODO: tratar de montar un sistema usando components de S. Sierra

(def loops-info (atom {}))
;; De momento he definido loops-info como un atom. No sé si hubiera gran cantidad de clips, por ejemplo, en una sesión entera
;; estaría garantizado que se añaden todos los clips o podría quedarse alguno fuera si el mensaje de respuesta de ableton
;; llega cuando todavía el atom no ha terminado de actualizarse por el mensaje anterior.
;; En loops-info guardo toda la información que pido una vez al comienzo de la sesión y no la vuelvo a pedir más a no
;; ser que se cambie de sesión. (habría que resetear la aplicación entera)

(def loopends (atom {}))
;; Defino un sitio distinto donde guargar la info sobre los loopends de los clips
;; TODO: Probar a meter la info de los loopends de nuevo junto con loops-info, haciendo que loops-info
;; sea un agent en vez de un atom

(def wave (atom {}))

(def ready? (atom false))

;; Iniciar comunicación con Ableton ********************************************************
(defn init-oscP5-communication [papplet]
  (osc/init-oscP5 papplet))


;; Funciones para preguntar a Ableton ********************************************************
(defn async-request-info-for-all-clips []
  (println "asking ableton for all clip's info")
  (-> (osc/make-osc-message "/live/name/clip")
      (osc/send-osc-message)))

(defn async-request-tempo []
  (println "asking ableton for the tempo")
  (-> (osc/make-osc-message "/live/tempo")
      (osc/send-osc-message)))

(defn async-request-volume-mute-solo []
  (println "asking ableton for track related info: volume, mute and solo")
  (doseq [paths ["/live/volume" "/live/mute" "/live/solo"]
          track (range 0 8)]
    (-> (osc/make-osc-message paths)
        (.add track)
        (osc/send-osc-message))))


(defn async-request-one-clip-state [track clip]
  (-> (osc/make-osc-message "/live/clip/info")
      (.add (int-array [track clip]))
      (osc/send-osc-message)))

(defn async-request-one-clip-loopend [track clip]
  (-> (osc/make-osc-message "/live/clip/loopend_id")
      (.add (int-array [track clip]))
      (osc/send-osc-message)))


;; Funciones para procesar los mensajes decibidos de Ableton ********************************************************

(defn load-loops-info [state message]
  ;; TODO: check that exist a place and a song if not throw an exception
  (let [[track clip nombre] (.arguments message)
        bd-song (first (filter #(= (:nombreArchivo %) nombre) bd/loops)) ;; pido el first porque el resultado es una secuencia de un elemento ({})
        bd-lugar (first (filter #(= (:lugar %) (:lugar bd-song)) bd/lugares))
        lugar (if (> (count (:lugar bd-lugar)) 19)
                   (apply str (concat (take 19 (:lugar bd-lugar)) [\. \. \.]))
                   (:lugar bd-lugar)) ;; Si el nombre del lugar es demasiado grande y no cabe en el rectángulo lo corto
        aloop {:track track
               :clip clip
               :nombre nombre
               :titulo (:titulo bd-song)
               :album (:album bd-song)
               :artista (:artista bd-song)
               :fecha (:fecha bd-song)
               :color-s (q/random 50 100 )
               :color-b (q/random 80 100)
               :color-h (condp = track ;; Hay que pasarlo a integer?
                          0 (q/random 105 120)
                          1 (q/random 145 160)
                          2 (q/random 300 315)
                          3 (q/random 330 345)
                          4 (q/random 195 210)
                          5 (q/random 230 245)
                          6 (q/random 25 40)
                          7 (q/random 50 65))
               :image (q/load-image (str "resources/0_portadas/" nombre ".jpg"))
               :lugar lugar
               :x (:coordX bd-lugar)
               :y (:coordY bd-lugar)}
        index (keyword (str (:track aloop) (:clip aloop)))]

    (println "loading loops info for" nombre "(track" track "clip" clip ")")

    (swap! loops-info assoc index aloop)
    (swap! wave assoc index (h/seq->stream (range 40 500 10))) ;; h/seq->stream son los diámetros de la onda

    (async-request-one-clip-state track clip) ;; pregunto por el estado de un clip cada vez que reciba un mensaje
                                              ;; sobre su ubicación. Como lo guardo en un sitio distinto a loops-info
                                              ;; no debo tener problemas de que se pierdan mensajes.
    (async-request-one-clip-loopend track clip)
    state)) ;; Esta función es procesada por :osc-event que toma el state de la aplicación
            ;; como argumento y lo devuelve supuestamente modificado.
            ;; En este caso, como no estamos modificando el state, sino un atom
            ;; externo a él, tenemos que devolver el estado sin modificarlo
            ;; Esta función no es una función pura porque tiene side-effects.

(defn load-clips-state [state message]
  (let [[track clip clip-state] (.arguments message)
        index (keyword (str track clip))]
    ;; (println track clip clip-state)

    ;; Este es un efecto secundario que quiero que ocurra y que igual no debería
    ;; estar aquí dentro sino ser una función separada
    ;; Lo que hago aquí es que cuando el clip pasa a estar en stop, vuelvo a asociar
    ;; cada index con un stream nuevo, ya que el anterior se ha consumido
    (when (= clip-state 1)
        (swap! wave assoc index (h/seq->stream (range 40 500 10))))

    (assoc-in state [:loops-state index] clip-state)))

(defn load-last-loop [state message]
  (let [[track clip clip-state] (.arguments message)
        index (keyword (str track clip))]
    (if (= 2 clip-state)
      (assoc state :last-loop index)
      state)))

(defn load-clips-loopend [state message]
  (let [[track clip loopend] (.arguments message)
        index (keyword (str track clip))]
    (swap! loopends assoc index loopend)
    state)) ;; Devuelve el estado sin modificarlo

(defn load-tracks-info [state message]
  (let [[track track-state] (.arguments message)
        track-index (keyword (str track))
        track-property (keyword (clojure.string/replace (.addrPattern message) #"/live/" ""))]
    ;; (println track track-state)
    (assoc-in state [:tracks-info track-index track-property] track-state)))

(defn load-tempo [state message]
  (assoc state :tempo (first (.arguments message))))

(defn load-general-play [state message]
  (assoc state :play (first (.arguments message))))


;; Función principal que procesa todos los mensajes recibidos de Ableton en :osc-event
(defn process-osc-event [state message]
  (let [path (osc/get-address-pattern message)]
    (condp = path
      "/live/name/clip"        (load-loops-info state message)
      "/live/clip/info"        (-> (load-clips-state state message)
                                   (load-last-loop message))
                               ;; El resultado del primer argumento de -> es el state
                               ;; que se lo paso al segundo argumento en la segunda posición
      "/live/clip/loopend"     (load-clips-loopend state message)
                               ;; Aunque la pregunta es con /live/clip/loopend_id, la respuesta es con /live/clip/loopend
      "/live/volume"           (load-tracks-info state message)
      "/live/solo"             (load-tracks-info state message)
      "/live/mute"             (load-tracks-info state message)
      "/live/tempo"            (load-tempo state message)
      "/live/play"             (load-general-play state message)

      #_(do (println "not mapped. path: " (osc/get-address-pattern message)))
      state ;;Muy importante, si no hay match con ningún path tenemos que devolver el estado
      )))



;; Ask Ableton for initial info.
;; Debería estar esta future en otro sitio???
(future ;; creo una future para correr código en otro thread, para poderlo dormir y que no bloquee mi thread principal
  (Thread/sleep 2000) ;; espero un poco para asegurarme que el sketch está creado cuando empiece a recibir mensajes de ableton,
  ;; ya que los voy a procesar através de :osc-event
  (async-request-info-for-all-clips) ;; pregunto por el track, clip y name de todos los clips que hay
  (async-request-tempo)
  (async-request-volume-mute-solo)
  (Thread/sleep 1000) ;; para darle tiempo a procesar la última request antes de quitar la splashscreen
  (reset! ready? true))

;; Qué pasa con el thread que abre la future? Se queda abierto?
;; Es eso un problema? Se puede cerrar él solo a sí mismo?







