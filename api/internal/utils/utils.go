package utils

import (
	"encoding/json"
	"log"
	"net/http"
	"time"
)

// M is shorthand for string to any data type mappings.
type M map[string]any

// Kill logs an error message fatally.
func Kill(err error) {
	log.Fatalf("an unexpected error occurred: %v", err)
}

// Respond returns json encoded data to the caller.
func Respond(w http.ResponseWriter, status int, data any) error {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	return json.NewEncoder(w).Encode(data)
}

// LogRequest logs an http request.
func LogRequest(r *http.Request, callback func()) {
	then := time.Now().UnixMilli()
	callback()
	now := time.Now().UnixMilli()
	delta := now - then

	log.Printf("%s %s - ip: %s, took: %v ms", r.Method, r.Pattern, getIp(r), delta)
}

// getIp attempts to get the client's IP address from headers first.
func getIp(r *http.Request) string {
	ip := r.Header.Get("X-Real-Ip")
	if ip == "" {
		ip = r.Header.Get("X-Forwarded-For")
	}
	if ip == "" {
		ip = r.RemoteAddr
	}
	return ip
}
