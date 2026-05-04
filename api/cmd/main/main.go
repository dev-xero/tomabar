package main

import (
	"log"

	"github.com/dev-xero/tomabar/internal/conf"
	"github.com/dev-xero/tomabar/internal/metrics"
	"github.com/dev-xero/tomabar/internal/server"
)

// main is the application entry point.
func main() {
	conf, err := conf.ReadConfig()
	if err != nil {
		log.Fatal(err)
	}
	log.Printf("Configuration file loaded")

	ms, err := metrics.NewMetricsScanner(conf)
	if err != nil {
		log.Fatal(err)
	}
	defer ms.File.Close()
	log.Printf("Metrics scanner started successfully")

	server.StartServer(conf, ms)
}
