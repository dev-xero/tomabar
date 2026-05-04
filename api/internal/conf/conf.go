package conf

import (
	_ "embed"
	"errors"
	"fmt"
	"net/http"
	"time"

	"github.com/dev-xero/tomabar/internal/utils"
	"gopkg.in/yaml.v3"
)

//go:embed conf.yaml
var configData []byte

// Conf represents the conf.yaml file.
type Conf struct {
	MetricsPath string `yaml:"metrics_path"`
	Host        string `yaml:"host"`
}

// readConfig reads the configuration yaml file from the file system and
// produces a pointer to the Conf struct, otherwise it returns an error.
func ReadConfig() (*Conf, error) {
	var conf Conf

	if err := yaml.Unmarshal(configData, &conf); err != nil {
		message := fmt.Sprintf("failed to unmarshal configuration file: %v", err)
		return nil, errors.New(message)
	}

	return &conf, nil
}

// handleMetrics is a http handler that transmits Tomato bar's log file over a
// REST API.
func (c *Conf) HandleMetrics() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		utils.Respond(
			w,
			http.StatusOK,
			utils.M{
				"message":   "Tomato Bar metrics",
				"timestamp": time.Now(),
				"data":      nil,
			},
		)
	}
}
