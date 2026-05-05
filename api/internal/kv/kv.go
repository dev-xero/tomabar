package kv

import (
	"errors"
	"log"
	"time"

	"github.com/dev-xero/tomabar/internal/utils"
	"github.com/patrickmn/go-cache"
)

// KvStore is a thin wrapper around the `go-cache` package.
type KvStore struct {
	store *cache.Cache
	ttl   time.Duration
}

// KvStore.GetMetrics attempts to fetch metrics from the cache.
func (k *KvStore) GetMetrics() (any, bool) {
	return k.store.Get("metrics")
}

// KvStore.SetMetrics attempts to save Metric slices for a limited
// duration.
func (k *KvStore) SetMetrics(metrics any) {
	k.store.Set("metrics", metrics, k.ttl)
}

// NewCache creates a new kv store, using `go-cache` under the hood.
func NewStore(ttlInMinutes int, purgeInMinutes int) *KvStore {
	if ttlInMinutes <= 0 || purgeInMinutes <= 0 {
		utils.Kill(errors.New("TTL and Purge times cannot be less than 1"))
	}

	ttl := time.Duration(ttlInMinutes) * time.Minute
	purge := time.Duration(purgeInMinutes) * time.Minute

	log.Printf("CACHE :::: TTL: %v, Purge: %v\n", ttl, purge)

	return &KvStore{
		store: cache.New(ttl, purge),
		ttl:   ttl,
	}
}
