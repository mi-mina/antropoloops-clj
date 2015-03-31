(ns aloops.osc
  (:import [oscP5 OscP5 OscMessage]
           [netP5 NetAddress Logger]))
; ns is a macro that allows you to declaratively specify a namespace's name
; and what it needs to have   require d
                            ; refere d
                            ; use d
                            ; import ed
; import expects as arguments the full names of the classes to import,
; or a sequential collection describing the package and classes to import

(declare my-remote-location my-oscP5)
;declare defs the supplied var names with no bindings,
;useful for making forward declarations.

(def in-port 9001)

(def out-port 9000)

(defn make-osc-message [path]
  (OscMessage. path))
; Función para crear un mensaje osc.

(defn send-osc-message [message]
  (.send my-oscP5 message my-remote-location))
