(ns air-mon.core
  (:require-macros
 [cljs.core.async.macros :refer[go-loop]])
  (:require [om.core :as om :include-macros true]
            [om-bootstrap.panel :as p]
            [om-bootstrap.button :as b]
            [om-bootstrap.nav :as n]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))

(enable-console-print!)

(def c (chan))

(println "Air Monitor dash ready!")
;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn create-marker! [lat long map title]
  (let [marker-options (clj->js {"position" (google.maps.LatLng. lat,long)
                                 "title" title
                                 "map" map }) ]
    (js/google.maps.Marker. marker-options) ) )

(defn header-element
  [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/nav #js {:className "navbar navbar-default
      navbar-static-top" }
                (dom/div #js {:className "container"}
                         (dom/div #js {:className "navbar-header"}
                                  (dom/a #js
                                  {:className "navbar-brand" :href "#"} "AirMonitor")

                                  ) ) ) ) ))


(defn floating-add-marker-button
  [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "marker-button"
                    :onClick (fn[e] (put! c "button clicked")
                               (. e preventDefault)
                               )}
               "add marker" ) ) ) )

(defn map-element
  [data owner]
  (reify
    ;; om/IShouldUpdate
    ;; (should-update[_, next-props, next-state]
    ;;   (println "should update called")
    ;;   true)
    om/IDidMount
    (did-mount [_]
      (println "Map Mounted!")
      (let [map-canvas (om/get-node owner)
            map-options (clj->js {"center" (google.maps.LatLng. -34.397, 150.644)
                                  "zoom" 8})
            map (js/google.maps.Map. map-canvas map-options)

           ]

        (create-marker! -34.397, 150.644,  map "Some Location")


        )
      )
    om/IRender
    (render [_]
      (dom/div #js {:id "map" :className "map"} ) ) ) )


(defn info-element
  [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "info"} "You will find some meters here")
      )
    )
  )


(defn  root-element [data owner]
  (reify om/IRender
    (render [_]
      (dom/div {:id "root"}

               (om/build header-element nil)
               (om/build map-element nil)
               (om/build  floating-add-marker-button nil)
               (om/build info-element nil)

               ))))
(om/root
 root-element
 app-state
 {:target (. js/document (getElementById "app"))})


(go-loop []
  (let [x (<! c)]
    (println "Got a value in this loop:" x))
  (recur))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
