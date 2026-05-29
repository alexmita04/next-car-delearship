package repository;

import model.Client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientRepository extends GenericRepository<Client> {
    @Override
    public Optional<Client> findById(int id) {
        return queryOne("SELECT id, name, phone, email FROM clients WHERE id = ?", id);
    }

    @Override
    public List<Client> findAll() {
        return query("SELECT id, name, phone, email FROM clients ORDER BY id");
    }

    @Override
    public Client insert(Client client) {
        int id = executeUpdateReturningId(
                "INSERT INTO clients (name, phone, email) VALUES (?, ?, ?)",
                client.getName(), client.getPhone(), client.getEmail()
        );
        return new Client(id, client.getName(), client.getPhone(), client.getEmail());
    }

    @Override
    public Client update(Client client) {
        executeUpdate(
                "UPDATE clients SET name = ?, phone = ?, email = ? WHERE id = ?",
                client.getName(), client.getPhone(), client.getEmail(), client.getId()
        );
        return client;
    }

    @Override
    public void deleteById(int id) {
        executeUpdate("DELETE FROM clients WHERE id = ?", id);
    }

    @Override
    protected Client mapRow(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email")
        );
    }
}
