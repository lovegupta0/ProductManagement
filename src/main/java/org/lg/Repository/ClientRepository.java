package org.lg.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.lg.Model.Service;
import org.lg.common.Response;
import org.lg.common.ResponseImp;
import org.lg.common.ResponseStatus;
import org.lg.common.StatusCode;
import org.lg.internal.Connection.EntityManagerFactoryUtil;
import org.lg.Model.Client;
import org.lg.internal.Security.JwtUtil;


import java.util.List;
import java.util.Optional;

public class ClientRepository implements ClientRepo{
    private EntityManagerFactory factory;
    private static volatile ClientRepository instance;
    private Service service;
    private ClientRepository(){
        factory= EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.service=StoreRepository.getInstance().getService();
    }
    @Override
    public Response<List<Client>> getAllClient() {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        TypedQuery<Client> findAll=manager.createNamedQuery("findAllClient",Client.class);
        List<Client> list=findAll.getResultList();
        manager.close();
        return ResponseImp.success(list);
    }

    @Override
    public Response<Client> addClient(Client client) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {

            et.begin();
            manager.persist(client);
            et.commit();
        } catch (Exception e){
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            manager.close();
        }
        return ResponseImp.success(client);
    }

    @Override
    public Response<Client> updateClient(Client client) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.merge(client);
            et.commit();
        } catch (Exception e){
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            manager.close();
        }
        return ResponseImp.success(client);
    }

    @Override
    public Response<Client> findById(String id) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        Optional<Client> client=Optional.ofNullable(manager.find(Client.class,id));
        manager.close();
        if(client.isPresent()){
            return ResponseImp.success(client.get());
        }
        return ResponseImp.error(StatusCode.NOT_FOUND);
    }

    @Override
    public Response<Client> getClientWithOrderList(String id) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        TypedQuery<Client> clientTypedQuery=manager.createNamedQuery("findClientWithOrderList",Client.class);
        clientTypedQuery.setParameter("id",id);
        Optional<Client> client=Optional.ofNullable(clientTypedQuery.getSingleResult());
        if(client.isPresent()){
            return ResponseImp.success(client.get());
        }
        return ResponseImp.error(StatusCode.NOT_FOUND);
    }

    public static ClientRepository getInstance(){
        if(instance==null){
            synchronized (ClientRepository.class){
                if (instance==null){
                    instance=new ClientRepository();
                }
            }
        }
        return instance;
    }
    private void checkService(){
        if(this.service==null){
            this.service=StoreRepository.getInstance().getService();
        }
    }
}
