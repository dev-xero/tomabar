package metrics

import (
	"bufio"
	"os"
	"path/filepath"

	"github.com/dev-xero/tomabar/internal/conf"
)

// Metric represents session data, most importantly the state transitions
// and timestamps.
type Metric struct {
	Event     string `json:"event"`
	FromState string `json:"fromState"`
	Timestamp string `json:"timestamp"`
	ToState   string `json:"toState"`
}

// MetricsScanner struct is responsible for encapsulating the file scanner.
type MetricsScanner struct {
	File    *os.File
	Scanner *bufio.Scanner
}

// ReadMetrics attempts to parse the metrics log file data into a sensible
// JSON representation.
func (ms *MetricsScanner) ReadMetrics() ([]Metric, error) {
	// !TODO: Intelligently parse the metrics file
	return nil, nil
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

// Close closes the opened metric file, this should always be called
// with `defer`.
func (ms *MetricsScanner) Close() error {
	return ms.File.Close()
}

// readRawMetricsFile performs the actual file open and read operations, then
// returns a byte slice if no error occur.
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
