(ns aloops.util)

(defn get-track-str [x]
  (str (first (name x))))

(defn get-track-int [x]
  (read-string (get-track-str x)))

(defn get-track-key [x]
  (keyword (get-track-str x)))
