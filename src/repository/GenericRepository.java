package repository;

import db.DatabaseConnection;
import exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericRepository<T> {
    protected final DatabaseConnection database;

    protected GenericRepository() {
        this.database = DatabaseConnection.getInstance();
    }

    protected Connection getConnection() throws SQLException {
        return database.getConnection();
    }

    public abstract Optional<T> findById(int id);

    public abstract List<T> findAll();

    public abstract T insert(T entity);

    public abstract T update(T entity);

    public abstract void deleteById(int id);

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> query(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + sql, e);
        }
    }

    protected Optional<T> queryOne(String sql, Object... params) {
        List<T> results = query(sql, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    protected int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Update failed: " + sql, e);
        }
    }

    protected int executeUpdateReturningId(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setParams(stmt, params);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new DatabaseException("No generated key returned");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Insert failed: " + sql, e);
        }
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
