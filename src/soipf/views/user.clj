(ns soipf.views.user
  (:refer-clojure :exclude [get swap!])
  (:require [soipf.models.user :as user]
            [soipf.models.invitation :as invitation]
            [clojure.string :as string]
            [noir.validation :as vali]
            [noir.cookies :as cookies]
            [noir.session :as session]
            [clj-time.core :as time])
  (:use noir.core
        noir.session
        [noir.response :only [redirect]]
        hiccup.form
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
  [:div.registration
   [:div.background "welcome"]
   [:div.container
    [:div.form
     [:div.title "Registration"]
     (form-to [:post (url-for register registration)]
              [:div {:class (error-class :login)}
               (text-field {:class "input" :placeholder "Username"}
                           :login (:login registration))]

              [:div {:class (error-class :password)}
               (password-field {:class "input" :placeholder "Password"}
                               :password)]

              [:div
               (password-field {:class "input" :placeholder "Confirm Password"}
                               :password-confirm)]
              [:button {:type "submit"
                        :class "submit click-once"} "Register"])]]])

(defpartial invite-not-found []
  [:h1 "Invitation not found"])

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

(defpage registration "/register" {:as registration}
  (layout
   (registration-form registration)))

(defpage register [:post "/register"] {:keys [login password password-confirm]
                                       :as registration}
  (if-let [user (user/create-user! login password password-confirm)]
    (do (session/put! :user user)
        (redirect "/"))))

(defpage invitation "/register/:invite-id" {:as registration}
  (layout
   (if (invitation/invitation-consumed? (:invite-id registration))
     (invite-not-found)
     (registration-form registration))))

(defpage use-invitation [:post "/register/:invite-id"]
  {:keys [login password password-confirm invite-id] :as registration}
  (if (invitation/invitation-consumed? invite-id)
    (invite-not-found)
    (if-let [user (user/create-user! login password password-confirm)]
      (do
        (invitation/consume-invitation! invite-id user)
        (session/put! :user user)
        (redirect "/"))
      (render register registration))))
