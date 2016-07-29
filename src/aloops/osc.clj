(ns aloops.osc
  (:import [oscP5 OscP5 OscMessage]
           [netP5 NetAddress]))

;; Aquí deberían estar todas las funciones que se comunican con oscP5


(declare my-remote-location my-oscP5)

(def in-port 9001)
(def out-port 9000)

(defn make-osc-message [path]
  (OscMessage. path))

(defn send-osc-message [message]
    (.send my-oscP5 message my-remote-location))

(defn get-address-pattern [message]
  (.addrPattern message))

(defn init-oscP5 [papplet]
  (intern 'aloops.osc 'my-oscP5 (OscP5. papplet in-port)) ;; This is like oscP5 = new OscP5(this, inPort);
  (intern 'aloops.osc 'my-remote-location (NetAddress. "localhost" out-port))) ;;myRemoteLocation = new NetAddress("localhost", outPort);



;; TODO
;; El código de abajo es el que estoy usando ahora en vez de lo definido en
;; init-oscP5. Traducirlo más adelante
  #_( create a new osc properties object
      OscProperties properties = new OscProperties();

      ;set a default NetAddress. sending osc messages with no NetAddress parameter
      ;in oscP5.send() will be sent to the default NetAddress.
      properties.setRemoteAddress("localhost", outPort);

      ;the port number you are listening for incoming osc packets.
      properties.setListeningPort(inPort);

      ;set the datagram byte buffer size. this can be useful when you send/receive
      ;huge amounts of data, but keep in mind, that UDP is limited to 64k
      properties.setDatagramSize(5000);

      ;initialize oscP5 with our osc properties
      oscP5 = new OscP5(this,properties);

      ;println("Estas son las propiedades "+properties.toString());

      )

  #_(defn create-and-send-test-message []
  (-> (make-osc-message "/test")
      (.add "123")
      (send-osc-message)))
