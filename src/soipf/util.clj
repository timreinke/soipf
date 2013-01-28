(ns soipf.util)

(defn namespaced-keyword
  "Prefixes a keyword with a namespace and, in the future,
   generally composes two keywords in a reasonable fashion?

   (namespaced-keyword :db.part :user)
   == :db.part/user"
  [a b]
  (keyword (str
            (name a)
            "/"
            (name b))))

(defn make-def-form
  "Constructs the s-expression for defining a variable at the symbol
   corresopnding to keyword `key` with the value of the composition of
   keywords `prefix` and `key`

  (make-def-form :a :b)
  == '(def b :a/b)"
  [prefix key]
  (list 'def (symbol (name key)) (namespaced-keyword prefix key)))

(defmacro defkeys
  "For each keyword `key` in `keys`, defines a variable under the name
  `key` with the value of the composition of keywords `prefix` and `key`"
  [prefix & keys]
  (let [def-forms (map (partial make-def-form prefix) keys)]
    `(do
       ~@def-forms)))