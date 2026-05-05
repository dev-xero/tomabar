package server

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/dev-xero/tomabar/internal/conf"
	"github.com/dev-xero/tomabar/internal/metrics"
	"github.com/dev-xero/tomabar/internal/utils"
)

// startServer opens an unencrypted TCP/IP port at :2118 ('bar' encoded
// according to each letter's position in the alphabet), then listens for
// incoming requests.
func StartServer(conf *conf.Conf) {
	srv := &http.Server{
		Addr:    ":2118",
		Handler: nil,
	}

	http.HandleFunc("/", handleIndex)
	http.HandleFunc("/metrics", handleMetrics(conf))

	go func() {
		log.Printf("Server is listening at %v:2118", conf.Host)
		if err := http.ListenAndServe(":2118", nil); err != nil {
			utils.Kill(err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, syscall.SIGTERM)
	<-quit

	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Printf("Forced shutdown: %v", err)
	}

	log.Println("Server stopped")
}

// handleIndex is a http handler that responds to requests hitting the index '/'.
// This is used purely for live-ness checks.
func handleIndex(w http.ResponseWriter, r *http.Request) {
	utils.LogRequest(r, func() {
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
	})
}

// HandleMetrics is a http handler that transmits Tomato bar's log file over a
// REST API.
func handleMetrics(conf *conf.Conf) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		utils.LogRequest(r, func() {
			metrics, err := metrics.ReadMetrics(conf)

			if err != nil {
				log.Printf(
					"something went wrong while fetching and parsing metrics: %v",
					err,
				)
				utils.Respond(
					w,
					http.StatusInternalServerError,
					utils.M{
						"message":   "Could not complete this request",
						"timestamp": time.Now(),
					},
				)
			}

			utils.Respond(
				w,
				http.StatusOK,
				utils.M{
					"message":   "Tomato Bar metrics",
					"timestamp": time.Now(),
					"data":      metrics,
				},
			)
		})
	}
}
