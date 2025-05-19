package repository

import (
	"context"
)

type AuthRepository interface {
	CreateUser(ctx context.Context, login string, hashPassword string, role string) (int64, error)
	GetPasswordByLogin(ctx context.Context, login string) (string, error)
	GetUserIdByLogin(ctx context.Context, login string) (int64, error)
	GetRoleById(ctx context.Context, id int64) (string, error)
	GetLoginById(ctx context.Context, id int64) (string, error)
	GetPasswordAndRoleAndIdByLogin(ctx context.Context, login string) (int64, string, string, error)
	UpdateUserRole(ctx context.Context, login string, newRole string) error
	GetAll(ctx context.Context) ([]string, error)
	CloseConnection()
}
