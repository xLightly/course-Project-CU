package service

import (
	"os"
	"time"

	"fmt"

	"github.com/golang-jwt/jwt/v5"
)

type Claims struct {
	Role   string `json:"role"`
	UserId int64  `json:"uid"`
	Login  string `json:"login"`
	jwt.RegisteredClaims
}

type TokenManager struct {
	secretKey []byte
}

func NewTokenManager() (*TokenManager, error) {
	secret := os.Getenv("JWT_SECRET")
	if secret == "" {
		return nil, fmt.Errorf("JWT_SECRET enviroment is not set")
	}
	return &TokenManager{
		secretKey: []byte(secret),
	}, nil
}

func (m *TokenManager) GenerateAccessToken(userId int64, login string, role string) (string, error) {
	claims := &Claims{
		UserId: userId,
		Login:  login,
		Role:   role,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(24 * time.Hour)),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	signedToken, err := token.SignedString(m.secretKey)
	if err != nil {
		return "", fmt.Errorf("failed to sign the token:%w", err)
	}
	return signedToken, nil
}

func (m *TokenManager) VerifyToken(tokenString string) (*Claims, error) {
	token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
		return m.secretKey, nil
	})
	if err != nil {
		return nil, fmt.Errorf("failed to parse the token: %w", err)
	}
	claims, ok := token.Claims.(*Claims)
	if !ok || !token.Valid {
		return nil, fmt.Errorf("invalid token claims")
	}
	return claims, nil
}
