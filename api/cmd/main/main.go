package main

import (
	"log"

	"github.com/dev-xero/tomabar/internal/conf"
	"github.com/dev-xero/tomabar/internal/server"
)

// main is the application entry point.
func main() {
	conf, err := conf.ReadConfig()
	if err != nil {
		log.Fatal(err)
	}

	log.Printf("Configuration file loaded")

	server.StartServer(conf)
}
