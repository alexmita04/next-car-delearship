package service;

import model.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientService {
    private final Map<Integer, Client> clients = new HashMap<>();

    /** Action 5: Add a new client */
    public Client addClient(Client client) {
        clients.put(client.getId(), client);
        return client;
    }

    /** Action 6: Remove a client */
    public boolean removeClient(int clientId) {
        return clients.remove(clientId) != null;
    }

    public Optional<Client> findClientById(int clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    public Map<Integer, Client> getClients() {
        return new HashMap<>(clients);
    }
}
