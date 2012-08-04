(ns soipf.views.user
  (:refer-clojure :exclude [get swap!])
  (:require [soipf.models.user :as user]
            [soipf.api.invitation :as invitation]
            [clojure.string :as string]
            [noir.validation :as vali]
            [noir.cookies :as cookies]
            [noir.session :as session]
            [clj-time.core :as time])
  (:use noir.core
        noir.session
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

(defpartial registration-form [registration]
  (form-to {:class "well"} [:post (url-for register registration)]
           [:legend "Register"]
           [:div {:class (error-class :login)}
            [:div.controls
             (text-field {:placeholder "Username"} :login (:login registration))]]
           [:div {:class (error-class :password)}
            [:div.controls
             (password-field {:placeholder "Password"} :password)]]
           [:div.control-group
            [:div.controls
             (password-field {:placeholder "Confirm Passowrd"} :password-confirm)]]
           (submit-button "Register")))

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
      (if-let [uri (get :redirected-from)]
        (do
          (remove! :redirected-from)
          (redirect uri))
        (redirect "/")))
    (render "/login" usr)))

(defpage "/logout" {}
  (user/logout)
  (redirect "/"))

(defpage register "/register/:invite-id" {:as registration}
  (layout
   (if (invitation/valid? (:invite-id registration))
     (registration-form registration)
     [:h1 "Invitation not found"])))

(defpage do-registration [:post "/register/:invite-id"]
      {:keys [login password password-confirm invite-id] :as registration}
  (if (and (user/valid? login password password-confirm)
           (invitation/valid? invite-id))
    (let [user (user/create-user! login password)]
      (invitation/use! invite-id user)
      (session/put! :user user)
      (redirect "/"))
    (render register registration)))