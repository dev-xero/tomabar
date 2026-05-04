package conf

import (
	"errors"
	"fmt"
	"io"
	"net/http"
	"os"

	"gopkg.in/yaml.v3"
)

// Conf represents the conf.yaml file.
type Conf struct {
	MetricsPath string `yaml:"metrics_path"`
	Host        string `yaml:"host"`
}

// readConfig reads the configuration yaml file from the file system and
// produces a pointer to the Conf struct, otherwise it returns an error.
func ReadConfig() (*Conf, error) {
	file, err := os.Open("conf.yaml")
	if err != nil {
		message := fmt.Sprintf("failed to open configuration file: %v", err)
		return nil, errors.New(message)
	}
	defer file.Close()

	data, err := io.ReadAll(file)
	if err != nil {
		message := fmt.Sprintf("failed to read configuration file: %v", err)
		return nil, errors.New(message)
	}

	var conf Conf
	err = yaml.Unmarshal(data, &conf)
	if err != nil {
		message := fmt.Sprintf("failed to unmarshal configuration file: %v", err)
		return nil, errors.New(message)
	}

	return &conf, nil
}

// handleMetrics is a http handler that transmits Tomato bar's log file over a
// REST API.
func (c *Conf) HandleMetrics() http.HandlerFunc {
	return func(w http.ResponseWriter, req *http.Request) {
		// utils.Respond(w, utils.M{}),
	}
}
