package conf

import (
	_ "embed"
	"errors"
	"fmt"

	"gopkg.in/yaml.v3"
)

//go:embed conf.yaml
var configData []byte

// Conf represents the conf.yaml file.
type Conf struct {
	IsPrefixed  bool   `yaml:"is_prefixed"`
	MetricsPath string `yaml:"metrics_path"`
	Host        string `yaml:"host"`
	Port        int    `yaml:"port"`
	TTL         int    `yaml:"ttl"`
	Purge       int    `yaml:"purge"`
}

// ReadConfig reads the configuration yaml file from the embed and returns
// a pointer to the Conf struct, otherwise it returns an error.
func ReadConfig() (*Conf, error) {
	var conf Conf

	if err := yaml.Unmarshal(configData, &conf); err != nil {
		message := fmt.Sprintf("failed to unmarshal configuration file: %v", err)
		return nil, errors.New(message)
	}

	return &conf, nil
}
