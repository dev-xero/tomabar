## TomaBar API

This is a tiny server and REST API written in Go. Its purpose is to read Tomato Bar's metrics log from your local filesystem then transmit this over a network connection.

Note that the connection is unencrypted and if you plan to use this in a remote setting you should setup proper transport layer security.

## Configuration

The configuration file can be found as `conf.yaml` in the internal/conf package.

```yaml
is_prefixed: true
metrics_path: Library/Containers/com.github.ivoronin.TomatoBar/Data/Library/Caches/TomatoBar.log
host: http://localhost
port: 2118
```

This is very bare-bones and the default settings should work on your system.

- `is_prefixed`: This determines wether the server prefixes your metrics path with $HOME on unix systems. This is important for the tests, otherwise you can use your full path and set this to false.

- `metrics_path`: This is where the server will search for your Tomato Bar logs. It must be the exact file path otherwise the application will not start.

- `host`: Unless you plan to run the server outside your local computer, you can keep this as it is.

- `port`: This is the socket port to listen to for incoming requests.

> ### Why "2118"?
> 
> No reason in particular, I replaced the word 'bar' with each letter's position in the alphabet!

## Task files

This project uses [taskfile](https://taskfile.dev/) for convenience. It's similar to Make or hacking together your own helper shell scripts. I use the tasks defined here as a shorthand for building, testing, or installing dependencies. 

You may install it on MacOS systems using this command, if you have brew installed:

```bash
brew install go-task
```

## Running

Running the server is a matter of using:

```bash
go run cmd/main/main.go
```

Or if you're using Task:

```bash
task
```

## Testing

Similarly, to test modules, you may run:

```bash
go test ./internal/...
```

Or with Task:

```bash
task run-tests
```

## Extending the API

If you want to customize or extend any part of the server or project, feel free to fork the repository!
