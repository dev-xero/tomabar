package conf

import (
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
