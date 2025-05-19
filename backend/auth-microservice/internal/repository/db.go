package repository

import (
	"context"
	"fmt"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Repository struct {
	Pool *pgxpool.Pool
}

func NewPool(pool *pgxpool.Pool) *Repository {
	return &Repository{
		Pool: pool,
	}
}

func OpenConnection(ctx context.Context, connUrl string) (*Repository, error) {
	pool, err := pgxpool.New(ctx, connUrl)
	if err != nil {
		return nil, fmt.Errorf("unable to create to pool:%w", err)
	}

	if err := pool.Ping(ctx); err != nil {
		pool.Close()
		return nil, fmt.Errorf("unable to ping database :%w", err)
	}

	query := `CREATE TABLE IF NOT EXISTS auth_data (
    id         	SERIAL PRIMARY KEY,
	login 		TEXT UNIQUE NOT NULL,
    password_hash   TEXT NOT NULL,
	role 		TEXT NOT NULL
		)`

	_, err = pool.Exec(ctx, query)
	if err != nil {
		return nil, fmt.Errorf("failed to create auth data table:%w", err)
	}

	return &Repository{
		Pool: pool,
	}, nil
}

func (repo Repository) CloseConnection() {
	repo.Pool.Close()
}

func (repo Repository) CreateUser(ctx context.Context, login string, hashPassword string, role string) (int64, error) {
	var id int64
	if err := repo.Pool.QueryRow(ctx, "INSERT INTO auth_data (login, password_hash, role) VALUES ($1, $2, $3) RETURNING id", login, hashPassword, role).Scan(&id); err != nil {
		return -1, fmt.Errorf("failed to add user auth data:%w", err)
	}
	return id, nil
}

func (repo Repository) GetPasswordByLogin(ctx context.Context, login string) (string, error) {
	var hashPassword string
	if err := repo.Pool.QueryRow(ctx, "SELECT password_hash FROM auth_data WHERE login = $1", login).Scan(&hashPassword); err != nil {
		return "", fmt.Errorf("user not found:%w", err)
	}
	return hashPassword, nil
}

func (repo Repository) GetUserIdByLogin(ctx context.Context, login string) (int64, error) {
	var userId int64
	if err := repo.Pool.QueryRow(ctx, "SELECT id FROM auth_data WHERE login = $1", login).Scan(&userId); err != nil {
		return -1, fmt.Errorf("user not found:%w", err)
	}
	return userId, nil
}

func (repo Repository) GetRoleById(ctx context.Context, id int64) (string, error) {
	var role string
	if err := repo.Pool.QueryRow(ctx, "SELECT role FROM auth_data WHERE id = $1", id).Scan(&role); err != nil {
		return "", fmt.Errorf("user not found:%w", err)
	}
	return role, nil
}

func (repo Repository) UpdateUserRole(ctx context.Context, login string, newRole string) error {
	_, err := repo.Pool.Exec(ctx, "UPDATE auth_data SET role = $1 WHERE login = $2", newRole, login)
	if err != nil {
		return fmt.Errorf("user not found:%w", err)
	}
	return nil
}

func (repo Repository) GetLoginById(ctx context.Context, id int64) (string, error) {
	var login string
	if err := repo.Pool.QueryRow(ctx, "SELECT login FROM auth_data WHERE id = $1", id).Scan(&login); err != nil {
		return "", fmt.Errorf("user not found:%w", err)
	}
	return login, nil
}

func (repo Repository) GetPasswordAndRoleAndIdByLogin(ctx context.Context, login string) (int64, string, string, error) {
	var password, role string
	var id int64
	if err := repo.Pool.QueryRow(ctx, "SELECT id, password_hash, role FROM auth_data WHERE login = $1", login).Scan(&id, &password, &role); err != nil {
		return -1, "", "", fmt.Errorf("user not found:%w", err)
	}
	return id, password, role, nil
}
func (repo Repository) GetAll(ctx context.Context) ([]string, error) {
	rows, err := repo.Pool.Query(ctx, "SELECT login FROM auth_data")
	if err != nil {
		return nil, fmt.Errorf("query failed: %w", err)
	}
	defer rows.Close()

	var logins []string
	for rows.Next() {
		var login string
		if err := rows.Scan(&login); err != nil {
			return nil, fmt.Errorf("scan failed: %w", err)
		}
		logins = append(logins, login)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("rows error: %w", err)
	}

	return logins, nil
}
