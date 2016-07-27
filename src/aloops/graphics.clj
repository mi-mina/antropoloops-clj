(ns aloops.graphics
  (:require [quil.core :as q]))

(defn load-resources []
  (def mundi (q/load-image "resources/1_BDatos/mapa_1728x1080.jpg")))

(defn adapt-to-frame [state]
  (let [ratio (/ (q/width) (q/height))]
    (if (>= ratio 1.6)
     (assoc state :ix (/ (- (q/width) (* (q/height) 1.6)) 2) :iy 0 :img-width (* (q/height) 1.6) :img-height (q/height))
     (assoc state :ix 0 :iy (/ (- (q/height) (/ (q/width) 1.6)) 2) :img-width (q/width) :img-height (/ (q/width) 1.6)))))

(defn draw-background [state]
  (q/background 44 44 44) ;;color de fondo del background (= gris que el fondo de la imagen)
  (q/image mundi (:ix state) (:iy state) (:img-width state) (:img-height state))
  (q/no-stroke)
  (q/fill 35 35 35) ;; gris oscuro, fondo de las carátulas.
  (q/rect 0 0 (q/width) (+ (/ (:img-height state) 5) (/ (- (q/height) (:img-height state)) 2)))) ;;rectángulo donde van las portadas

