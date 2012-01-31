(ns soipf.views.user
  (:require [soipf.views.common :as common]
            [soipf.models.user :as user]
            [clojure.string :as string])
  (:use [noir.core :only [defpage defpartial render]]
        [noir.response :only [redirect]]
        [hiccup.form-helpers]))

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
           (submit-button "Login")))

(defpage "/login" {:as usr}
  (if (user/logged-in?)
    (redirect "/")
    (common/layout
     [:div.row
      [:div.span4.offset4 (login-form usr)]])))

(defpage [:post "/login"] {:keys [login password] :as usr}
  (if (user/login login password)
    (redirect "/")
    (render "/login" usr)))

(defpage "/logout" {}
  (user/logout)
  (redirect "/"))