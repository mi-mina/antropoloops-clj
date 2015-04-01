; Aquí definimos todo lo que necesitamos para poner en
; marcha la comunicación OSC
; Estas funciones las usaremos en el namespace ocsloops
; en el que definimos los mensajes que queremos lanzarle a ableton
; ns is a macro that allows you to declaratively specify a namespace's name
; and what it needs to have   require d
                            ; refere d
                            ; use d
                            ; import ed
; import expects as arguments the full names of the classes to import,
; or a sequential collection describing the package and classes to import
; oscP5 es una librería para processing, pero como es java la podemos usar.
(ns aloops.osc
  (:import [oscP5 OscP5 OscMessage]
           [netP5 NetAddress Logger]))

; declare defs the supplied var names with no bindings,
; useful for making forward declarations.
; en este caso estamos declarando dos cosas a la vez
(declare my-remote-location my-oscP5)

; def el puerto de entrada
(def in-port 9001)

; def el puerto de salida
(def out-port 9000)

; Función para crear un mensaje osc.
; El puntito después de OscMessage es la forma idiomática de crear
; en clojure una nueva instancia de la clase OscMessage
(defn make-osc-message [path]
  (OscMessage. path))

; Función para mandar un mensaje
(defn send-osc-message [message]
  (.send my-oscP5 message my-remote-location))

; Esto es como
; oscP5 = new OscP5(this, inPort);
; myRemoteLocation = new NetAddress("localhost", outPort);
; que en processing está en el setup
; en ella se definen my-oscP5 y my-remote-location
; que estaban declaradas pero sin contenido
(defn init-oscP5 [papplet]
  (def my-oscP5 (OscP5. papplet in-port))
  (def my-remote-location (NetAddress. "localhost" out-port))
  )
