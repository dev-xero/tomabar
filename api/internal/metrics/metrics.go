package metrics

import (
	"bufio"
	"encoding/json"
	"log"
	"os"
	"path/filepath"

	"github.com/dev-xero/tomabar/internal/conf"
)

// header denotes the kind of event which was logged by Tomato bar.
var header struct {
	Type string `json:"type"`
}

// Metric represents session data, most importantly the state transitions
// and timestamps.
type Metric struct {
	Event     string  `json:"event"`
	FromState string  `json:"fromState"`
	Timestamp float64 `json:"timestamp"`
	ToState   string  `json:"toState"`
	Type      string  `json:"type"`
}

// ReadMetrics attempts to parse the metrics log file data into a sensible
// JSON representation.
func ReadMetrics(conf *conf.Conf) ([]Metric, error) {
	// We're going to cache the result of scanning this file so that later
	// requests don't take as long. The issue presently is efficiently scanning
	// the lines we're interested in and building our struct from there.
	var metrics []Metric

	// basically cache-miss
	if true {

		rawFile, err := readRawMetricsFile(conf)
		if err != nil {
			return nil, err
		}
		defer rawFile.Close()

		bufScanner := bufio.NewScanner(rawFile)

		for bufScanner.Scan() {
			// There are many inconsistencies in the log file, but while this
			// particular approach isn't robust enough, it should suffice.
			line := bufScanner.Bytes()

			// An error here would mean the line was indeed malformed.
			if err := json.Unmarshal(line, &header); err != nil {
				continue
			}

			// We're not interested in non-transition events.
			if header.Type != "transition" {
				continue
			}

			var metric Metric
			if err := json.Unmarshal(line, &metric); err != nil {
				log.Println("Encountered an improperly formatted transition event")
				continue
			}

			metrics = append(metrics, metric)
		}

		if err := bufScanner.Err(); err != nil {
			return nil, err
		}
	}

	return metrics, nil
}

// readRawMetricsFile performs the actual file open and read operations, then
// returns a pointer to the file descriptor if no errors occur.
func readRawMetricsFile(conf *conf.Conf) (*os.File, error) {
	var prefix string
	if conf.IsPrefixed {
		home, err := os.UserHomeDir()
		if err != nil {
			return nil, err
		}
		prefix = home
	}
	path := filepath.Join(prefix, conf.MetricsPath)

	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}

	return file, nil
}
