(ns app.colors
  (:require [goog.color :as color]))

(defn hexToRgb
  "convert hex color to rgb in values 0..1"
  [hexcolor]
  (mapv #(/ % 255.0) (color/hexToRgb hexcolor)))

(defn valid-rgb?
  [rgbcolor]
  (not-any? #(or (< % 0)
                 (> % 255)) rgbcolor))


(defn clamp
  [val]
  (max (min val 255) 0))

(defn clamped-rgb-vec
  [rgbvector]
  (mapv #(clamp %) rgbvector))

(defn rgbToHex
  [rgbcolor]
  (let [[r g b] rgbcolor]
    (color/rgbToHex r g b)))

(defn rgbToXyz
  [rgbcolor]
  (let [[r g b]
        (mapv #(* % 100)
              (mapv 
               #(if (> % 0.04045)
                  (js/Math.pow (/ (+ % 0.055) 1.055) 2.4)
                  (/ % 12.92)) rgbcolor))]
    [(+ (* r 0.4124) (* g 0.3576) (* b 0.1805))
     (+ (* r 0.2126) (* g 0.7152) (* b 0.0722))
     (+ (* r 0.0193) (* g 0.1192) (* b 0.9505))]))

(def xyzreferencewhited65 [95.047 100 108.883])

(defn xyzToLab
  [xyzcolor]
  (let [[x y z] (mapv #(if (> % 0.008856)
                         (js/Math.pow % (/ 1 3))
                         (+ (* % 7.787) (/ 16 116)))
                      (mapv / xyzcolor xyzreferencewhited65))]
    [(- (* 116 y) 16)
     (* 500 (- x y)) 
     (* 200 (- y z))]))

(defn radToDegrees
  [h]
  (if (> h 0)
    (* 180 (/ h (.-PI js/Math)))
    (- 360 (* 180 (/ (js/Math.abs h) (.-PI js/Math))))))

(defn labToLch
  [labcolor]
  (let [[l a b] labcolor
        h (radToDegrees (js/Math.atan2 b a))]
    [l
     (js/Math.sqrt (+ (* a a) (* b b)))
     h]))

(defn lchToLab
  [lchcolor]
  (let [[l c h] lchcolor
        hrad (/ (* h (.-PI js/Math)) 180)]
    [l
     (* (js/Math.cos hrad) c)
     (* (js/Math.sin hrad) c)]))

(defn labToXyz
  [labcolor]
  (let [[l a b] labcolor
        y (/ (+ l 16) 116)
        x (+  (/ a 500) y)
        z (- y (/ b 200))
        xyz [x y z]]
    (mapv * xyzreferencewhited65
          (mapv
           (fn [i] (let [cube (js/Math.pow i 3)]
                     (if (> cube 0.008856)
                       cube
                       (/ (- i (/ 16 116)) 116))))
           xyz))))

(defn xyzToRgb
  [xyzcolor]
  (let [[x y z] (mapv #(/ % 100) xyzcolor)
        r (+ (* x 3.2406) (* y -1.5372) (* z -0.4986))
        g (+ (* x -0.9689) (* y 1.8758) (* z 0.0415))
        b (+ (* x 0.0557) (* y -0.2040) (* z 1.0570))
        rgb [r g b]]
    (mapv #(js/Math.round (* % 255)) (mapv #(if (> % 0.0031308)
                                              (- (* 1.055 (js/Math.pow % (/ 1 2.4))) 0.055)
                                              (* % 12.92)) rgb))))


(defn hexToLch
  [hexcolor]
  (labToLch (xyzToLab (rgbToXyz (hexToRgb hexcolor)))))

(defn lchToRgb
  [lchcolor]
  (xyzToRgb (labToXyz (lchToLab lchcolor))))

(defn lchToHex
  [lchcolor]
  (rgbToHex (xyzToRgb (labToXyz (lchToLab lchcolor)))))

(defn darken
  "darken darkens a rgb color by a given factor.
   if no factor is provided, the color will be darkened 
   with the factor of 0.2."
  ([colorstring] (darken colorstring 0.2))
  ([colorstring factor]
   (color/rgbArrayToHex
    (color/darken (color/hexToRgb colorstring) factor))))

(defn lighten
  "lighten lightens a rgb color by a given factor.
   If no factor is provided, the color will be lightened
   with the factor of 0.2."
  ([colorstring] (lighten colorstring 0.2))
  ([colorstring factor]
   (color/rgbArrayToHex
    (color/lighten (color/hexToRgb colorstring) factor))))


(defn dark-bg?
  "dark-bg? returns true if the given hex color is 'dark'"
  [color]
  (let [[r g b] (color/hexToRgb color)]
    (> (- 1 (/ (+ (* 0.299 r)
                  (* 0.587 g)
                  (* 0.114 b))
               255))
       0.5)))
