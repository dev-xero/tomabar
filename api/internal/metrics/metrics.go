package metrics

import (
	"bufio"
	"encoding/json"
	"log"
	"os"
	"path/filepath"

	"github.com/dev-xero/tomabar/internal/conf"
	"github.com/dev-xero/tomabar/internal/kv"
)

// header represents the kind of event which was logged by Tomato bar.
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

// GetMetrics attempts to fetch the metrics log file from a cache first, or
// fallback to the local filesystem.
func GetMetrics(conf *conf.Conf, cache *kv.KvStore) ([]Metric, error) {
	// We're going to cache the result of scanning this file so that later
	// requests don't take as long.
	if cached, found := cache.GetMetrics(); found {
		// Cache Hit.
		// We use this instead.
		log.Println("Cache Hit! Using stored value")
		metrics := cached.([]Metric)
		return metrics, nil
	}

	log.Println("Cache miss, defaulting to local filesystem")

	// Cache Miss.
	// The issue presently is efficiently scanning the lines so that
	// our Metric struct slice is built from there.
	rawFile, err := readRawMetricsFile(conf)
	if err != nil {
		return nil, err
	}
	defer rawFile.Close()

	metrics, err := parseRawMetrics(rawFile)
	if err != nil {
		return nil, err
	}

	cache.SetMetrics(metrics)

	log.Printf("Metrics saved to cache, valid for %v minutes\n", conf.TTL)

	return metrics, nil
}

// readRawMetricsFile performs the actual file open and read operations, then
// returns a pointer to the file descriptor if no errors occur.
func readRawMetricsFile(conf *conf.Conf) (*os.File, error) {
	// This prefix is necessary for CI tests since they do not have the actual
	// log file in the $HOME root but instead rely on mock data.
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

// parseRawMetrics attempts to parse the metrics file into structured,
// consistent slice of the `Metric` type.
func parseRawMetrics(file *os.File) ([]Metric, error) {
	var metrics []Metric

	bufScanner := bufio.NewScanner(file)

	for bufScanner.Scan() {
		// There are many inconsistencies in the log file, and this particular
		// approach isn't robust enough, but it should suffice.
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

	return metrics, nil
}
