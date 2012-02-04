(ns soipf.format
  (:import (org.pegdown PegDownProcessor Extensions))
  (:require [clj-time [format :as format]
                      [coerce :as coerce]]))

(defn markdownify [s]
  (.markdownToHtml
   (PegDownProcessor. (bit-or Extensions/AUTOLINKS
                              Extensions/SUPPRESS_ALL_HTML
                              Extensions/SMARTYPANTS))
   s))

(def date-format (format/formatter "yyyy-MM-dd hh:mm"))

(defn date-str [date]
  (if (= (class date) org.joda.time.DateTime)
    (format/unparse date-format date)
    (date-str (coerce/from-date date))))
