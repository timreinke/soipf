(ns soipf.format
  (:import (org.pegdown PegDownProcessor Extensions)))

(defn markdownify [s]
  (.markdownToHtml
   (PegDownProcessor. (bit-or Extensions/AUTOLINKS
                              Extensions/SUPPRESS_ALL_HTML
                              Extensions/SMARTYPANTS))
   s))
