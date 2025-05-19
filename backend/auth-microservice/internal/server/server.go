package server

import (
	pb "auth-microservice/api"
	"auth-microservice/internal/repository"
	"auth-microservice/internal/service"
	"context"
	"fmt"
	"log"
	"net"
	"os"
	"strconv"

	"google.golang.org/grpc"
)

type Server struct {
	grpcServer *grpc.Server
	listener   net.Listener
	repo       repository.Repository
}

func NewServer(ctx context.Context) (*Server, error) {
	portStr := os.Getenv("AUTH_SERVICE_PORT")
	if portStr == "" {
		portStr = "50051"
	}
	port, err := strconv.Atoi(portStr)
	if err != nil {
		return nil, fmt.Errorf("invalid port: %w", err)
	}
	connURL := os.Getenv("DB_URL")
	if connURL == "" {
		return nil, fmt.Errorf("empty db_url")
	}
	repo, err := repository.OpenConnection(context.Background(), connURL)
	if err != nil {
		return nil, fmt.Errorf("failed to open db:%w", err)
	}
	fmt.Println("Opened the connection")
	tokenManager, err := service.NewTokenManager()
	if err != nil {
		return nil, fmt.Errorf("failed to create token manager:%w", err)
	}
	authService, err := service.NewAuthService(*repo, *tokenManager)
	if err != nil {
		return nil, fmt.Errorf("failed to create service:%w", err)
	}
	listener, err := net.Listen("tcp", fmt.Sprintf(":%d", port))
	if err != nil {
		return nil, fmt.Errorf("failed to listen:%w", err)
	}
	authService.InitAdmin(ctx)
	grpcServer := grpc.NewServer()
	pb.RegisterAuthServiceServer(grpcServer, authService)
	return &Server{
		grpcServer: grpcServer,
		listener:   listener,
		repo:       *repo,
	}, nil
}

func (s Server) Start(ctx context.Context) error {
	errCh := make(chan error, 1)

	go func() {
		log.Printf("gRPC server started on:%s", s.listener.Addr().String())
		if err := s.grpcServer.Serve(s.listener); err != nil {
			errCh <- err
		}
	}()
	select {
	case <-ctx.Done():
		log.Printf("Shutting down gRPC server gracufully...")
		s.repo.Pool.Close()
		s.grpcServer.GracefulStop()
		return nil

	case err := <-errCh:
		return fmt.Errorf("gRPC server failed:%w", err)
	}
}
