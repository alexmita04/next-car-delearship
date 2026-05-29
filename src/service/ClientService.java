package service;

import model.Client;
import repository.ClientRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService() {
        this(new ClientRepository());
    }

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client addClient(Client client) {
        return clientRepository.insert(client);
    }

    public boolean removeClient(int clientId) {
        if (clientRepository.findById(clientId).isEmpty()) {
            return false;
        }
        clientRepository.deleteById(clientId);
        return true;
    }

    public Optional<Client> findClientById(int clientId) {
        return clientRepository.findById(clientId);
    }

    public Map<Integer, Client> getClients() {
        return clientRepository.findAll().stream()
                .collect(Collectors.toMap(Client::getId, client -> client));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
}
