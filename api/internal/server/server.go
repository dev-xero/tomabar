package server

import (
	"net/http"
	"time"

	"github.com/dev-xero/tomabar/internal/conf"
	"github.com/dev-xero/tomabar/internal/utils"
)

// startServer opens an unencrypted TCP/IP port at :2118 ('bar' encoded
// according to each letter's position in the alphabet), then listens for 
// incoming requests.
func StartServer(conf *conf.Conf) {
	http.HandleFunc("/", handleIndex)
	http.HandleFunc("/metrics", conf.HandleMetrics())

	if err := http.ListenAndServe(":2118", nil); err != nil {
		utils.Kill(err)
	}
}

// handleIndex is a http handler that responds to requests hitting the index '/'.
// This is used purely for live-ness checks.
func handleIndex(w http.ResponseWriter, req *http.Request) {
	err := utils.Respond(w, utils.M{
		"message":   "API is reachable",
		"timestamp": time.Now(),
		"data":      nil,
	})
	if err != nil {
		utils.Kill(err)
	}
}