(ns aloops.bd
  (:require [clojure.data.json :as json :refer [read-str]]))

(def loops (json/read-str
            (slurp "resources/1_BDatos/BDLoops.txt") :key-fn keyword ))
(def lugares (json/read-str
              (slurp "resources/1_BDatos/BDLugares.txt") :key-fn keyword))

; :key-fn keyword
; según el reference:
; Single-argument function called on JSON property names; return
; value will replace the property names in the output. Default
; is clojure.core/identity, use clojure.core/keyword to get
; keyword properties.
; En este caso, como usamos la función keyword, nos sirve para convertir
; en keyword los property names del json

