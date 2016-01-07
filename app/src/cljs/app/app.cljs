(ns app.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.bootstrap]))

(def app-db (atom {:showcolorpicker false}))

(defn modal-component []
  [:div.modal.fade {:tabIndex "-1" :role "dialog" :id "colorpicker"
                    :aria-labelledby "colorpicker"}
   [:div.modal-dialog {:role "document"}
    [:div.modal-content
     [:div.modal-header
      [:button.close {:data-dismiss "modal" :aria-label "close"}
       [:span {:aria-hidden "true"} "close"]]]]]])

(defn input-component [value title]
  [:div.row
   [:span.inputtitle title]
   [:input {:type "text" :default-value value}]
   [:button {:type "button" :data-toggle "modal"
             :data-target "#colorpicker"} "Toggle Colorpicker"]
   ])



(defn calling-component []
  [:div "Parent component"
   [input-component "#2a2a2a" "mainfg"]
   [modal-component]])



(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))
