package org.lg.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.lg.Model.Service;
import org.lg.common.Response;
import org.lg.common.ResponseImp;
import org.lg.common.ResponseStatus;
import org.lg.common.StatusCode;
import org.lg.internal.Connection.EntityManagerFactoryUtil;
import org.lg.Model.Store;
import org.lg.internal.Security.JwtUtil;

import java.util.Optional;

public class StoreRepository implements StoreRepo{
    private EntityManagerFactory factory;
    private static volatile StoreRepository instance;
    private Service service;
    private StoreRepository(){
        factory= EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
    }
    @Override
    public Response<Object> Authenticate(String username, String password) {
        EntityManager manager=factory.createEntityManager();
        TypedQuery<Store> user=manager.createNamedQuery("findByUsername",Store.class);
        user.setParameter("username",username);
        try {
            Optional<Store> optionalUser=Optional.ofNullable(user.getSingleResult());
            if(optionalUser.isPresent()){
                Store userData=optionalUser.get();
                service=new Service(JwtUtil.generateToken(userData.getUUID(),userData.getUsername(),userData.getName()),userData.getUUID(),userData.getName(),userData.getEmail());
                if(password.equals(optionalUser.get().getPassword())) return ResponseImp.success(true);
            }
        }
        catch (Exception e){
            return ResponseImp.error(ResponseStatus.of(401,e.getMessage()));
        }
        finally {
            manager.close();
        }

        return ResponseImp.error(StatusCode.UNAUTHORIZED);
    }

    @Override
    public  Response<Store> getUser() {
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        TypedQuery<Store> user=manager.createNamedQuery("findUser",Store.class);

        Optional<Store> optionalUser=Optional.ofNullable(user.getSingleResult());
        if(optionalUser.isPresent()){
            optionalUser.get().setPassword("");
            return ResponseImp.success(optionalUser.get());
        }
        return ResponseImp.error(StatusCode.NOT_FOUND);
    }

    @Override
    public Service getService() {
        return this.service;
    }

    @Override
    public void logout() {
        this.service=null;
    }

    public static StoreRepository getInstance(){
        if(instance==null){
            synchronized (StoreRepository.class){
                if(instance==null){
                    instance=new StoreRepository();
                }
            }
        }
        return instance;
    }
}
