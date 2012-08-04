(ns soipf.test.cookies)

(def
  re-token
  #"[!#$%&'*\-+.0-9A-Z\^_`a-z\|~]+")

(def
  re-quoted
  #"\"(\\\"|[^\"])*\"")

(def
  re-value
  (str re-token "|" re-quoted))

(def
  re-cookie
  (re-pattern (str "\\s*(" re-token ")=(" re-value ")\\s*[;,]?")))

(def
  cookie-attrs
  {"$Path" :path, "$Domain" :domain, "$Port" :port})

(def
  set-cookie-attrs
  {:comment "Comment", :comment-url "CommentURL", :discard "Discard",
   :domain "Domain", :max-age "Max-Age", :path "Path", :port "Port",
   :secure "Secure", :version "Version", :expires "Expires", :http-only "HttpOnly"})

(defn parse-cookie-header
  "Turn a HTTP Cookie header into a list of name/value pairs."
  [header]
  (for [[_ name value] (re-seq re-cookie header)]
    [name value]))
