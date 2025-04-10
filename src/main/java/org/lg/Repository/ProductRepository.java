package org.lg.Repository;

import jakarta.persistence.*;
import org.lg.Model.Service;
import org.lg.common.Response;
import org.lg.common.ResponseImp;
import org.lg.common.ResponseStatus;
import org.lg.common.StatusCode;
import org.lg.internal.Connection.EntityManagerFactoryUtil;
import org.lg.Model.InventoryTransactions;
import org.lg.Model.Product;
import org.lg.Model.Transactions;
import org.lg.internal.Security.JwtUtil;

import java.util.List;
import java.util.Optional;

public class ProductRepository implements ProductRepo{
    private EntityManagerFactory factory;
    private static volatile ProductRepository instance;
    private InventoryTransactionsRepo inventoryTransactionsRepo;
    private Service service;
    private ProductRepository(){
        factory= EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        inventoryTransactionsRepo=InventoryTransactionsRepository.getInstance();
        this.service=StoreRepository.getInstance().getService();
    }

    @Override
    public Response<Product> addProduct(Product product) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.persist(product);
            et.commit();
        } catch (Exception e){
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            InventoryTransactions inventoryTransactions=new InventoryTransactions(product,product.getQuantity(), Transactions.IN);
            Response response=inventoryTransactionsRepo.createTrasaction(inventoryTransactions);
            if(response.getStatusCode()!=200){
                et.rollback();
                return ResponseImp.error(response.getResponseStatus());
            }

            manager.close();
        }
        return ResponseImp.success(product);
    }

    @Override
    public Response<Product> updateProduct(Product product) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.merge(product);
            et.commit();
        } catch (Exception e){
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            manager.close();
        }
        return ResponseImp.success(product);
    }

    @Override
    public Response<Boolean> deleteProduct(String id) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            Product product=manager.find(Product.class,id);

            if(product!=null){
                for(InventoryTransactions inventoryTransactions:inventoryTransactionsRepo.findAllTransactionsByProductUUID(product.getUUID()).getData()){
                    inventoryTransactionsRepo.deleteTransaction(inventoryTransactions.getUUID());
                }
                manager.remove(product);

            }
            et.commit();
        } catch (Exception e){
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            manager.close();
        }
        return ResponseImp.success(true);
    }

    @Override
    public Response<List<Product>> getAllProducts() {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        TypedQuery<Product> findAll=manager.createNamedQuery("findAllProduct",Product.class);
        List<Product> products=findAll.getResultList();
        manager.close();
        return ResponseImp.success(products);
    }

    @Override
    public Response<Product> findProductById(String id) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        Product product;
        TypedQuery<Product> query=manager.createNamedQuery("findProductById",Product.class);
        query.setParameter("id",id);
        try {
            product=query.getSingleResult();

        }
        catch (Exception e){
            return ResponseImp.error(StatusCode.NOT_FOUND);
        }
        finally {
            manager.close();
        }

       return ResponseImp.success(product);
    }

    @Override
    public Response<Object> updateProducts(List<Product> list) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            for(Product product:list){
                manager.merge(product);
            }
            et.commit();
        } catch (Exception e){
            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));

        }
        finally {
            manager.close();
        }

        return ResponseImp.success(true);
    }

    @Override
    public Response<Boolean> updateProductInventory(String uuid,float updateQuantity) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            Response<Product> response=findProductById(uuid);
            if(response.getStatusCode()!=200) return ResponseImp.error(response.getResponseStatus());
            Product product=  response.getData();
            product.setQuantity(product.getQuantity()+updateQuantity);
            InventoryTransactions inventoryTransactions=new InventoryTransactions(product,updateQuantity, Transactions.IN);
            Response inventoryRespons=inventoryTransactionsRepo.createTrasaction(inventoryTransactions);
            Response updateResponse=updateProduct(product);
            if(inventoryRespons.getStatusCode()!=200 || updateResponse.getStatusCode()!=200){
                return ResponseImp.error(inventoryRespons.getStatusCode()!=200?inventoryRespons.getResponseStatus():updateResponse.getResponseStatus());
            }
            et.commit();
        } catch (Exception e){

            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            manager.close();
        }
        return ResponseImp.success(true);
    }
    public static ProductRepository getInstance( ){
        if(instance==null){
            synchronized (ProductRepository.class){
                if(instance==null){
                    instance=new ProductRepository();
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
