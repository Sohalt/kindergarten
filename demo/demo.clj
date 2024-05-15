(ns demo)

;; functions get turned into api endpoints
;; `curl http://localhost:7777/current-time`
;; => "1970-01-01T00:00:00Z"
(defn current-time []
  (str (java.time.Instant/now)))

;; positional parameters are path segments
;; `curl http://localhost:7777/hello/world`
;; => "Hello world!"
(defn hello [name]
  (str "Hello " name "!"))

;; numbers, keywords, and booleans get coerced automatically
;; `curl http://localhost:7777/sub/42/23`
;; => 19
(defn sub [a b]
  (- a b))

;; pass named parameters with query parameters
;; `curl 'http://localhost:7777/add?a=1&b=2'`
;; => 3
(defn add [{:keys [a b]}]
  (+ a b))

;; or as application/json, application/edn, or application/transit+json, by doing a `POST` request with the appropriate `Content-Type` header and encoded body
;; `curl -H 'Content-Type: application/json' -d '{"multiplier":5, "values": [1,2,3]}' http://localhost:7777/data-transform`
;; => [5,10,15]
(defn data-transform [{:keys [multiplier values]}]
  (map (partial * multiplier) values))

;; default return type is JSON, specify an alternate type using the `Accept` header:
;; `curl -H 'Accept: application/edn' -H 'Content-Type: application/json' -d '{"multiplier":5, "values": [1,2,3]}' http://localhost:7777/data-transform`
;; => (5 10 15)
