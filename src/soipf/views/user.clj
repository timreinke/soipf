(ns soipf.views.user
  (:require [soipf.models.user :as user]
            [clojure.string :as string]
            [noir.validation :as vali]
            [noir.cookies :as cookies]
            [clj-time.core :as time])
  (:use [noir.core :only [defpage defpartial render]]
        [noir.response :only [redirect]]
        hiccup.form-helpers
        soipf.views.common
        soipf.format))

(defpartial login-form [usr]
  (form-to {:class "well"} [:post "/login"]
           [:legend "Login"]
           (vali/on-error :login error-text)
           [:div.control-group
            [:div.controls
             (text-field {:placeholder "Username"} :login (:login usr))]]
           [:div.control-group
            [:div.controls
             (password-field {:placeholder "Password"} :password)]]
           (hidden-field "persistent" "true")
           (submit-button "Login")))

(defpage "/login" {:as usr}
  (if (user/logged-in?)
    (redirect "/")
    (layout
     (login-form usr))))

(defpage [:post "/login"] {:keys [login password persistent] :as usr}
  (if (user/login login password)
    (do
      (when (= persistent "true")
        (cookies/put! :ring-session
                      {:value (cookies/get :ring-session)
                       :expires (date-str (time/plus (time/now) (time/days 20))
                                          cookie-format)
                       :path "/"}))
      (redirect "/"))
    (render "/login" usr)))

(defpage "/logout" {}
  (user/logout)
  (redirect "/"))

(defpage "/test" {}
  (cookies/put! :test-cookie
                {:value "test"})
  (redirect "/"))