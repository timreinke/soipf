(ns soipf.wol
  (:require [net.cgrand.enlive-html :as enlive]))

(defn configure-wol [wol opts]
  (merge wol opts))

(defn generic-invoke [widget]
  ((widget :run) widget))

(def view-wol
  {:template [:file "widgets/wol/view.html"]
   :render (bind [:title :host :wake])})

(defn classify
  "Turns e.g. :abc into :.abc for use in enlive selectors.

   The 'class' in 'classify' refers to HTML classes."
  [key]
  (keyword (str "." (name key))))

;; WARNING
;; this function depends on enlive internals
(defn bind
  "Takes a list of keywords and returns a function taking
   an enlive node(set) and a widget.

   The returned function sets the content each node matching
   a key (as a class) to the value of that key from the widget"
  ([keys]
     (bind keys {}))
  ([keys bindings]
     (let [selectors (map (comp enlive/cacheable vector classify)
                          keys)]
       (fn [node widget]
         (reduce (fn [doc [key selector]]
                   (transform doc
                              selector
                              (enlive/content (pr-str (widget key)))))
                 (map vector keys selectors))))))

(def view-widget
  {:template [:file "widgets/view.html" :.container]
   :snippets {:body [:file "widgets/view.html" :.body]}
   :render (bind [:title :description]
                 {:widget (fn [widget]
                            (with-snippet :body
                              [:.pair]
                              (clone-for [[key value] widget]
                                         [:.name] (content (pr-str key))
                                         [:.value] (content (pr-str value)))))})
   ;; :animate (cljsfn) ;; as in come to life, not necessarily move
   ;; one interpretation is 'to become interactive'
   })


(def wol-prototype
  {:title "A Host"
   :host "thisisyourcomputer.com"
   :wake '(fn [host]
            (exec "wol" host))
   :run '(fn [{:keys [host wake]}]
           ((eval (read-string wake)) host))
   :aspects {:clone true
             :view #'view-wol
             ;; like attr_accessible from rails... (mass assignment security)
             :accessible-attrs [:title :host :wake]
             :invoke #'generic-invoke}})


(defn generic-instantiate [prototype opts]
  (merge prototype opts))

(def wake-on-lan-constructor
  {:id ::wol

   ;; the meta directives shown here aren't supported yet
   :options ^{:default {:required true  ;; sugar for the next line
                        :validate [(complement empty?)]}}
     {:title "This is used to list the WoL widget"
      :host "The host to wake"
      :wake "The code to wake the host.  A function of the host.
             Compiled in the context of at least clojure.core"}

   :prototype #'wol-prototype

   :title "Wake-On-LAN"

   :description "A widget that allows waking up a computer from the comforts of soipf.
    This one even allows running arbitrary code!"

   :aspects {:view #'view-widget
             :instantiate #'generic-instantiate}})

(defn var->symbol [var]
  (second (read-string (pr-str var))))
