; En este namespace definimos los mensajes que queremos lanzarle
; a Ableton

;¿Cuándo usamos :import y cuándo usamos :use?***************
; import map symbols to Java classes and interfaces.
; You can use import to add such mappings to the current namespace.
; import expects as arguments the full names of the classes to import, or a sequential
; collection describing the package and classes to import

; use nos sirve para map symbols to vars definidos en otros namespaces


(ns aloops.oscloops
  (:import
   [oscP5 OscP5 OscMessage ]
   [netP5 NetAddress Logger])
  (:use
   quil.core     ;¿por qué hay que incluir quil.core aquí?
   ; [ dat00.util :as util]
   [ aloops.osc :as osc]
   ))


(defn make-call-bis [namee & more ]
  (if (nil? (first more))
    (list (symbol (str "." (name namee)) ))
    (concat (list (symbol (str "." (name namee)) )) more)) )


(defmacro map-get [class  things]
  `(-> ~class (~@(make-call-bis (first things) (second things) ))))


(defmacro map-direct-get [class things-col]
  (let [res (reduce (fn [c things]
                      (apply assoc c [(first things)
                                      `(let [o# (map-get ~class [:get (second ~things)])]
                                         (map-get o# [~@(vector (last things))]))]))
                    {}
                    things-col)]
    (if  (> (count res) 1)
      res
      (do
        (println res)
        (val (first res)))
      )

    ))
