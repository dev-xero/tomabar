package kv

import (
	"testing"
	"time"

	"github.com/patrickmn/go-cache"
)

func TestNewStore(t *testing.T) {
	ttl := 20
	purge := 25
	got := NewStore(ttl, purge)

	ttlDuration := time.Duration(ttl) * time.Minute
	purgeDuration := time.Duration(purge) * time.Minute

	expected := &KvStore{
		store: cache.New(
			ttlDuration,
			purgeDuration,
		),
		ttl: ttlDuration,
	}

	if got == nil {
		t.Fatal("Expected kv store to have been created, is nil")
	}

	if got.store == nil {
		t.Fatal("Expected store to point to a valid cache, is nil")
	}

	if expected.ttl != got.ttl {
		t.Fatalf(
			"Expected ttl to be %v, got %v",
			expected.ttl,
			got.ttl,
		)
	}
}
