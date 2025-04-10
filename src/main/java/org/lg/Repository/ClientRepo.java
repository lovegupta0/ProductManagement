package org.lg.Repository;

import org.lg.Model.Client;
import org.lg.common.Response;

import java.util.List;
import java.util.Optional;

public interface ClientRepo {
    public Response<List<Client>> getAllClient();
    public Response<Client> addClient(Client client);
    public Response<Client> updateClient(Client client);
    public Response<Client> findById(String id);
    public Response<Client> getClientWithOrderList(String  id);
}
