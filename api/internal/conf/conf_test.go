package conf

import (
	"net/http"
	"net/http/httptest"
	"reflect"
	"testing"
)

func TestReadConfig(t *testing.T) {
	conf, err := ReadConfig()
	if err != nil {
		t.Errorf("failed to read config file: %v", err)
	}

	confVal := reflect.ValueOf(*conf)

	numFields := confVal.NumField()
	for i := range numFields {
		field := confVal.Field(i)
		if !field.IsValid() || field.IsZero() {
			t.Errorf(
				"invalid configuration, expected field '%v' to not be invalid"+
					"or zero",
				field,
			)
		}
	}
}

func TestHandleMetrics(t *testing.T) {
	req, err := http.NewRequest("GET", "/metrics", nil)
	if err != nil {
		t.Fatalf("failed to create request: %v", err)
	}

	conf, err := ReadConfig()
	if err != nil {
		t.Errorf("failed to read config file: %v", err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(conf.HandleMetrics())

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
