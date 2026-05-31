package service;

import exceptions.ClientDeletionException;
import model.Client;
import repository.ClientRepository;
import repository.SaleRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientService {
    private final ClientRepository clientRepository;
    private final SaleRepository saleRepository;

    public ClientService() {
        this(new ClientRepository(), new SaleRepository());
    }

    public ClientService(ClientRepository clientRepository) {
        this(clientRepository, new SaleRepository());
    }

    public ClientService(ClientRepository clientRepository, SaleRepository saleRepository) {
        this.clientRepository = clientRepository;
        this.saleRepository = saleRepository;
    }

    public Client addClient(Client client) {
        Client saved = clientRepository.insert(client);
        AuditService.getInstance().logAction("ADD_CLIENT");
        return saved;
    }

    public void removeClient(int clientId) throws ClientDeletionException {
        if (clientRepository.findById(clientId).isEmpty()) {
            throw new ClientDeletionException("Client not found.");
        }
        if (saleRepository.hasSalesForClient(clientId)) {
            throw new ClientDeletionException(
                    "Client can't be deleted because it has sales.");
        }
        clientRepository.deleteById(clientId);
        AuditService.getInstance().logAction("REMOVE_CLIENT");
    }

    public Client updateClient(Client client) {
        clientRepository.update(client);
        AuditService.getInstance().logAction("UPDATE_CLIENT");
        return client;
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
