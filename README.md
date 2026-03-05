## TomatoBar Remote

A local server and Android client for viewing TomatoBar pomodoro session data remotely. The server reads the TomatoBar log file from the host machine and exposes it over a REST API. The Android app connects to the server and presents session history and focus analytics.

## Architecture

The project is a monorepo containing two independent components.

`server/` is a FastAPI application that parses the TomatoBar log file (newline-delimited JSON) and serves structured session data over HTTP.

`android/` is a native Kotlin application built with Jetpack Compose that fetches session data from the server and renders it as a dashboard.

Communication between the two is a single JSON payload over HTTP. The server is intended to run on the same machine as TomatoBar, with the Android client reaching it over the local network or through a tunnel such as Tailscale.

```
tomatobar-remote/
├── server/
│   ├── main.py
│   ├── pyproject.toml
│   └── .env
├── android/
│   ├── app/
│   └── build.gradle.kts
└── README.md
```

## Server

### Requirements

Python 3.11 or later. uv is used for dependency management.

### Configuration

The server reads one environment variable.

`LOG_FILE_PATH` should be set to the full path of the TomatoBar log file. On macOS this is typically:

```
~/Library/Containers/com.github.ivoronin.TomatoBar/Data/Library/Caches/TomatoBar.log
```

Create a `.env` file in the `server/` directory:

```
LOG_FILE_PATH=~/Library/Containers/com.github.ivoronin.TomatoBar/Data/Library/Caches/TomatoBar.log
```

### Running

```
cd server
uv run fastapi dev main.py
```

The server starts on `http://127.0.0.1:8000` by default.

### Endpoints

`GET /` returns a health check confirming the server is running.

`GET /data` reads the TomatoBar log file, parses each line as JSON, and returns the full event list. The response is wrapped in a standard envelope:

```json
{
  "status": "success",
  "version": "1.0.0",
  "data": [
    { "timestamp": 1769866102.83, "type": "appstart" },
    { "event": "startStop", "fromState": "idle", "timestamp": 1769866132.09, "toState": "work", "type": "transition" }
  ]
}
```

### Log format

TomatoBar writes one JSON object per line. There are two event types.

`appstart` events contain a `timestamp` field and indicate the application was launched.

`transition` events contain `timestamp`, `fromState`, `toState`, and `event` fields. States are `idle`, `work`, and `rest`. The `event` field is either `startStop` (manual toggle) or `timerFired` (automatic transition at the end of a work or rest interval).

## Android

### Requirements

Android Studio with Kotlin and Jetpack Compose support. Minimum SDK target is 26 (Android 8.0).

### Configuration

The server base URL is configured in the app. When running over a local network, use the host machine's LAN IP. When using Tailscale, use the Tailscale IP or MagicDNS hostname.

### Building

Open the `android/` directory in Android Studio and build normally. Alternatively:

```
cd android
./gradlew assembleDebug
```

## Networking

The server binds to localhost by default. To make it reachable from the Android device, either bind to `0.0.0.0` and connect over the local network, or use a tunnel.

Tailscale is the recommended approach. Install it on both the Mac and the Android device, and the server becomes reachable at its Tailscale IP without exposing anything to the public internet. No configuration beyond installing Tailscale and logging in is required.
