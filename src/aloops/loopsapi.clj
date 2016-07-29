(ns aloops.loopsapi
  (:require [aloops.bd :as bd]
            [aloops.oscapi :as oscapi])
  )

;; Las funciones aquí definidas se encargan de hacer cambios en el estado
;; de :antropoloops


;; antropoloops API

(defn request-clips-info []
  (println "asking ableton about all clip's info")
  (oscapi/async-request-info-for-all-clips))


#_(defn request-clips-loopend []
  (println "request-clips-loopend")
 )
