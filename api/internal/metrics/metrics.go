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

// MetricsScanner struct is responsible for encapsulating the file scanner.
type MetricsScanner struct {
	File    *os.File
	Scanner *bufio.Scanner
}

// MetricScanner.ReadMetrics attempts to parse the metrics log file data into a sensible
// JSON representation.
func (ms *MetricsScanner) ReadMetrics() ([]Metric, error) {
	// We're going to cache the result of scanning this file so that later
	// requests don't take as long. The issue presently is efficiently scanning
	// the lines we're interested in and building our struct from there.
	var metrics []Metric

	for ms.Scanner.Scan() {
		// A source of concern is the inconsistencies in the log file,
		// while this approach isn't robust enough it should suffice.
		line := ms.Scanner.Bytes()

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
			// This should not happen.
			log.Println("Encountered an improperly formatted transition event")
			continue
		}
		metrics = append(metrics, metric)

		// !TODO: This has to be cached, since the file is then closed
		// or better still, read the file every time the cache has expired.
	}

	if err := ms.Scanner.Err(); err != nil {
		return metrics, err
	}

	return metrics, nil
}

// NewMetricsScanner produces a reference to a metrics scanner
// with the buffer using the passed file.
func NewMetricsScanner(conf *conf.Conf) (*MetricsScanner, error) {
	file, err := readRawMetricsFile(conf)
	if err != nil {
		return nil, err
	}

	return &MetricsScanner{
		File:    file,
		Scanner: bufio.NewScanner(file),
	}, nil
}

// MetricsScanner.Close closes the opened metric file, this should always be called
// with `defer`.
func (ms *MetricsScanner) Close() error {
	return ms.File.Close()
}

// readRawMetricsFile performs the actual file open and read operations, then
// returns a pointer to the file descriptor if no errors occur.
func readRawMetricsFile(conf *conf.Conf) (*os.File, error) {
	home, err := os.UserHomeDir()
	if err != nil {
		return nil, err
	}

	path := filepath.Join(home, conf.MetricsPath)

	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}

	return file, nil
}
