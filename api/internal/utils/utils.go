package utils

import (
	"encoding/json"
	"log"
	"net/http"
)

// M is shorthand for string to any data type mappings.
type M map[string]any

// Kill logs an error message fatally.
func Kill(err error) {
	log.Fatalf("an unexpected error occurred: %v", err)
}


// Respond returns json encoded data to the caller.
func Respond(w http.ResponseWriter, data any) error {
	return json.NewEncoder(w).Encode(data)
}