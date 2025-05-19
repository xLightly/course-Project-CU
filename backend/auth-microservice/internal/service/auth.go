package service

import (
	pb "auth-microservice/api"
	"auth-microservice/internal/repository"
	"context"
	"fmt"
	"os"

	"golang.org/x/crypto/bcrypt"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

type AuthService struct {
	pb.UnimplementedAuthServiceServer
	repo         repository.AuthRepository
	tokenManager TokenManager
}

func NewAuthService(repo repository.AuthRepository, tokenM TokenManager) (*AuthService, error) {
	return &AuthService{repo: repo, tokenManager: tokenM}, nil
}

func (s *AuthService) InitAdmin(ctx context.Context) (int64, error) {
	login := os.Getenv("ADMIN_LOGIN")
	password := os.Getenv("ADMIN_PASSWORD")
	hashPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		fmt.Errorf("failed to init the admin:%w", err)
	}
	id, err := s.repo.CreateUser(ctx, login, string(hashPassword), "admin")
	if err != nil {
		return -1, fmt.Errorf("failed to create admin:%w", err)
	}
	return id, nil
}

func (s *AuthService) Register(ctx context.Context, request *pb.RegisterRequest) (*pb.RegisterResponse, error) {
	if request.Login == "" || request.Password == "" {
		return nil, status.Errorf(codes.InvalidArgument, "login and password is required")
	}
	hashPassword, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "cannot hash the password:%w", err)
	}
	standartRole := "user"
	id, err := s.repo.CreateUser(ctx, request.Login, string(hashPassword), standartRole)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "Failed to wirte user into db:%w", err)
	}
	return &pb.RegisterResponse{UserId: id}, nil
}

func (s *AuthService) Login(ctx context.Context, request *pb.LoginRequest) (*pb.LoginResponse, error) {
	if request.Login == "" || request.Password == "" {
		return nil, status.Errorf(codes.InvalidArgument, "login and password is required")
	}
	var id int64
	id, trueHashPassword, role, err := s.repo.GetPasswordAndRoleAndIdByLogin(ctx, request.Login)
	if err != nil {
		return nil, status.Errorf(codes.InvalidArgument, "invalid login or password:%w", err)
	}
	if err := bcrypt.CompareHashAndPassword([]byte(trueHashPassword), []byte(request.Password)); err != nil {
		return nil, status.Errorf(codes.Unauthenticated, "invalid login or password: %w", err)
	}
	token, err := s.tokenManager.GenerateAccessToken(id, request.Login, role)
	return &pb.LoginResponse{Token: token}, nil
}

func (s *AuthService) ValidateToken(ctx context.Context, request *pb.ValidateTokenRequest) (*pb.ValidateTokenResponse, error) {
	if request.Token == "" {
		return &pb.ValidateTokenResponse{Valid: false}, status.Errorf(codes.InvalidArgument, "token is required")
	}
	claims, err := s.tokenManager.VerifyToken(request.Token)
	if err != nil {
		return &pb.ValidateTokenResponse{Valid: false}, status.Errorf(codes.Unauthenticated, "Failed to verify the token:%w", err)
	}
	trueLogin, err := s.repo.GetLoginById(ctx, claims.UserId)
	if err != nil {
		return &pb.ValidateTokenResponse{Valid: false}, status.Errorf(codes.Internal, "Internal server error")
	}
	trueRole, err := s.repo.GetRoleById(ctx, claims.UserId)
	if err != nil {
		return &pb.ValidateTokenResponse{Valid: false}, status.Errorf(codes.Internal, "Internal server error")
	}
	if trueLogin != claims.Login || trueRole != claims.Role {
		return &pb.ValidateTokenResponse{Valid: false}, status.Errorf(codes.Unauthenticated, "failed to verify the token")
	}
	return &pb.ValidateTokenResponse{
		Valid:  true,
		UserId: claims.UserId,
		Login:  claims.Login,
		Role:   claims.Role,
	}, nil
}

func (s *AuthService) PromoteToAdmin(ctx context.Context, request *pb.PromoteRequest) (*pb.PromoteResponse, error) {
	err := s.repo.UpdateUserRole(ctx, request.TargetLogin, "admin")
	if err != nil {
		return nil, status.Errorf(codes.Internal, "cannot update the role:%w", err)
	}
	return &pb.PromoteResponse{Success: true}, nil

}

func (s *AuthService) GetUsers(ctx context.Context, request *pb.Empty) (*pb.Users, error) {
	logins, err := s.repo.GetAll(ctx)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "cannot take the users:%w", err)
	}
	return &pb.Users{Logins: logins}, nil
}
