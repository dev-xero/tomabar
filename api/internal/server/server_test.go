package server

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"github.com/dev-xero/tomabar/internal/conf"
)

func TestHandleIndex(t *testing.T) {
	req, err := http.NewRequest("GET", "/", nil)
	if err != nil {
		t.Fatalf("failed to setup request: %v", err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(handleIndex)

	handler.ServeHTTP(rr, req)

	if status := rr.Code; status != http.StatusOK {
		t.Errorf("handler returned the wrong status code, got %v, want %v",
			rr.Code,
			http.StatusOK,
		)
	}

	expected := "is reachable"

	if !strings.Contains(rr.Body.String(), expected) {
		t.Errorf(
			"handler returned unexpected body, expected message to contain "+
				"'%v', got %v,",
			expected,
			rr.Body.String(),
		)
	}
}

func TestHandleMetrics(t *testing.T) {
	req, err := http.NewRequest("GET", "/metrics", nil)
	if err != nil {
		t.Fatalf("failed to create request: %v", err)
	}

	conf, err := conf.ReadConfig()
	if err != nil {
		t.Fatalf("failed to read config file: %v", err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(handleMetrics(conf))

	handler.ServeHTTP(rr, req)

	if status := rr.Code; status != http.StatusOK {
		t.Errorf(
			"unexpected status code, got %v, want %v",
			status,
			http.StatusOK,
		)
	}

	// !TODO: body data property should contain keys unique to metrics log.
}
