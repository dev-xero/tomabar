FROM golang:1.26.3-alpine3.22 AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN GOOS=linux go build -o /app/main ./cmd/main


FROM alpine:latest
RUN adduser -D -h /app user
WORKDIR /app
COPY --from=builder /app/main .
USER user
EXPOSE 2118
CMD ["/app/main"]
