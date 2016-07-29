(ns aloops.util

  )

;; En este ns colocamos funciones auxiliares que usamos en un momento determinado
;; pero que son genéricas y nos pueden servir en otro sitio.

(defn substring? [sub st]
  (if (not= (.indexOf st sub) -1)
    true
    false))
