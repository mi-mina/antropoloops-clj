(ns aloops.graphics
  (:require [quil.core :as q]
            [aloops.loopsapi :as loopsapi]))

(declare mundi)
(declare splash)



;; Parece que definir una variable def dentro de defn está muy mal.
;; he buscado una alternativa: declare + intern
;; supongo que también podría haber usado alter-var-root, pero no sé qué es mejor
(defn load-resources []
  (intern 'aloops.graphics 'mundi (q/load-image "resources/1_BDatos/mundi_1728x1080.jpg"))
  (intern 'aloops.graphics 'splash (q/load-image "resources/1_BDatos/splash_1280x800.png")))

;; Importante!!! No olvidar que antes de pasar a uberjar tengo que quitar resources/ del path.

(defn adapt-to-frame [state]
  (let [ratio (/ (q/width) (q/height))
        img-sz (:img-sz state)]
    (if (>= ratio 1.6)
      (assoc state :img-sz (assoc img-sz
                             :ix (/ (- (q/width) (* (q/height) 1.6)) 2)
                             :iy 0
                             :img-width (* (q/height) 1.6)
                             :img-height (q/height)))
      (assoc state :img-sz (assoc img-sz
                             :ix 0
                             :iy (/ (- (q/height) (/ (q/width) 1.6)) 2)
                             :img-width (q/width)
                             :img-height (/ (q/width) 1.6))))))

(defn draw-background [state]
  (let [img-sz (:img-sz state)]
  (q/background 44 44 44) ;;color de fondo del background (= gris que el fondo de la imagen)
  (q/image mundi (:ix img-sz) (:iy img-sz) (:img-width img-sz) (:img-height img-sz))
  (q/no-stroke)
  (q/fill 35 35 35) ;; gris oscuro, fondo de las carátulas.
  (q/rect 0 0 (q/width) (+ (/ (:img-height img-sz) 5) (/ (- (q/height) (:img-height img-sz)) 2))))) ;;rectángulo donde van las portadas
;; En el caso de las funciones que se ejecutan dentro de draw no tenemos que preocuparnos de
;; devolver el estado porque no es tenido en cuenta para nada.

(defn draw-splash-screen [state]
  (if @loopsapi/ready?
    nil
    (let [img-sz (:img-sz state)]
      (q/image splash (:ix img-sz) (:iy img-sz) (:img-width img-sz) (:img-height img-sz)))))

