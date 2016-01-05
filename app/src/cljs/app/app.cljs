(ns app.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.bootstrap]))

(def app-db (atom {:showcolorpicker false}))

(defn input-component [value title]
  [:div.row
   [:span.inputtitle title]
   [:input {:type "text" :default-value value}]
   [:button {:type "button" :on-click #(swap! app-db assoc :showcolorpicker (not (:showcolorpicker @app-db)))} "Toggle Colorpicker"]])

(defn calling-component []
  [:div "Parent component"
   [input-component "#2a2a2a" "mainfg"]])



(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))
