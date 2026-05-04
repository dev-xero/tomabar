package server

import (
	"log"
	"net/http"
	"time"

	"github.com/dev-xero/tomabar/internal/conf"
	"github.com/dev-xero/tomabar/internal/metrics"
	"github.com/dev-xero/tomabar/internal/utils"
)

// startServer opens an unencrypted TCP/IP port at :2118 ('bar' encoded
// according to each letter's position in the alphabet), then listens for
// incoming requests.
func StartServer(conf *conf.Conf, ms *metrics.MetricsScanner) {
	http.HandleFunc("/", handleIndex)
	http.HandleFunc("/metrics", handleMetrics(ms))

	log.Printf("Server is listening at %v:2118", conf.Host)
	if err := http.ListenAndServe(":2118", nil); err != nil {
		utils.Kill(err)
	}
}

// handleIndex is a http handler that responds to requests hitting the index '/'.
// This is used purely for live-ness checks.
func handleIndex(w http.ResponseWriter, r *http.Request) {
	err := utils.Respond(
		w,
		http.StatusOK,
		utils.M{
			"message":   "API is reachable",
			"timestamp": time.Now(),
			"data":      nil,
		},
	)
	if err != nil {
		utils.Kill(err)
	}
}

// HandleMetrics is a http handler that transmits Tomato bar's log file over a
// REST API.
func handleMetrics(ms *metrics.MetricsScanner) http.HandlerFunc {
	ms.ReadMetrics()
	return func(w http.ResponseWriter, r *http.Request) {
		utils.Respond(
			w,
			http.StatusOK,
			utils.M{
				"message":   "Tomato Bar metrics",
				"timestamp": time.Now(),
				"data":      nil,
			},
		)
	}
}
