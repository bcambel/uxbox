;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) 2015-2016 Andrey Antukh <niwi@niwi.nz>

(ns uxbox.util.spec
  (:require [cljs.spec :as s]))

;; --- Constants

(def email-rx
  #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$")

(def uuid-rx
  #"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")

;; --- Predicates

(defn email?
  [v]
  (and string?
       (re-matches email-rx v)))

(defn color?
  [v]
  (and (string? v)
       (re-matches #"^#[0-9A-Fa-f]{6}$" v)))

(defn file?
  [v]
  (instance? js/File v))

;; TODO: properly implement

(defn url-str?
  [v]
  (string? v))

;; --- Default Specs

(s/def ::uuid uuid?)
(s/def ::email email?)
(s/def ::color color?)

;; --- Public Api

(defn valid?
  [spec data]
  (let [valid (s/valid? spec data)]
    (when-not valid
      (js/console.error (str "Spec validation error:\n" (s/explain-str spec data))))
    valid))

(defn extract
  "Given a map spec, performs a `select-keys`
  like exctraction from the object.

  NOTE: this function does not executes
  the conform or validation of the data,
  is responsability of the user to do it."
  [data spec]
  (let [desc (s/describe spec)
        {:keys [req-un opt-un]} (apply hash-map (rest desc))
        keys (concat
              (map (comp keyword name) req-un)
              (map (comp keyword name) opt-un))]
    (select-keys data keys)))


