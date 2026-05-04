package utils

import (
	"encoding/json"
	"net/http/httptest"
	"reflect"
	"testing"
)

func TestRespond(t *testing.T) {
	rec := httptest.NewRecorder()
	data := M{
		"title":       "testing 'response'",
		"description": "must encode a valid struct as json.",
	}

	if err := Respond(rec, data); err != nil {
		t.Fatalf("response returned an error: %v", err)
	}

	var got M
	if err := json.NewDecoder(rec.Body).Decode(&got); err != nil {
		t.Fatalf("failed to decode response body: %v", err)
	}

	if !reflect.DeepEqual(got, data) {
		t.Fatalf("got %v, requires %v", got, data)
	}
}
