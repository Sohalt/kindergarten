(ns sohalt.kindergarten
  (:require [org.httpkit.server :as http]
            [muuntaja.core :as m]
            [muuntaja.middleware :as mw]
            [ring.middleware.params :as ring-params]
            [ring.middleware.keyword-params :as ring-kw-params]
            [sohalt.kindergarten.coerce :as coerce]
            [clojure.string :as str]
            [ring.util.codec :as codec]))

(defn make-router [api-ns]
  (fn [{:keys [uri params]}]
    (if (= "/" uri)
      ;; healthcheck
      {:status 200}
      (let [parts (str/split (subs uri 1) #"/")
            [sym' & args] parts
            sym (symbol sym')
            _ (require api-ns)
            handler (ns-resolve api-ns sym)]
        (if (and handler (fn? @handler))
          {:status 200
           :body (apply handler (cond-> (map (comp coerce/auto-coerce codec/url-decode) args)
                                  (seq params) (conj params)))}
          {:status 404})))))

(defn wrap-middleware [app]
  (-> app
      coerce/wrap-coerce-params
      mw/wrap-params
      (mw/wrap-format (assoc-in m/default-options [:http :encode-response-body?] (constantly true)))
      ring-kw-params/wrap-keyword-params
      ring-params/wrap-params))

(defn main [{:as opts :keys [api-ns]}]
  (http/run-server (wrap-middleware (make-router api-ns))
                   (merge opts {:legacy-return-value? false
                                :bind "0.0.0.0"})))
