package main

import (
	"auth-microservice/internal/server"
	"context"
	"log"
	"os/signal"
	"syscall"
)

//go:generate protoc -I=../api --go_out=../api --go_opt=paths=source_relative --go-grpc_out=../api --go-grpc_opt=paths=source_relative ../api/user.proto

func main() {
	ctx, stop := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer stop()

	server, err := server.NewServer(ctx)
	if err != nil {
		log.Fatalf("Failed to create server")
	}
	if err = server.Start(ctx); err != nil {
		log.Fatalf("Server error:%w", err)
	}

}
