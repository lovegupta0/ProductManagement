package org.lg.Repository;

import jakarta.persistence.*;
import org.lg.Model.Service;
import org.lg.common.Response;
import org.lg.common.ResponseImp;
import org.lg.common.ResponseStatus;
import org.lg.common.StatusCode;
import org.lg.internal.Connection.EntityManagerFactoryUtil;
import org.lg.Model.InventoryTransactions;
import org.lg.internal.Security.JwtUtil;

import java.util.List;

public class InventoryTransactionsRepository implements InventoryTransactionsRepo{
    private EntityManagerFactory factory;
    private static volatile InventoryTransactionsRepository instance;
    private Service service;
    private InventoryTransactionsRepository(){
        factory= EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.service=StoreRepository.getInstance().getService();
    }
    @Override
    public Response<Object> createTrasaction(InventoryTransactions transactions) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.persist(transactions);
            et.commit();

        }catch (Exception e){
            e.getMessage();
            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        return ResponseImp.success(transactions);
    }

    @Override
    public Response<Object>  updateTransaction(InventoryTransactions transactions) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.merge(transactions);
            et.commit();

        }catch (Exception e){
            e.getMessage();
            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        return ResponseImp.success(transactions);
    }

    @Override
    public Response<Integer>  deleteTransaction(String uuid) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        int count=0;
        try {
            et.begin();
            Query query = manager.createNamedQuery("deleteInventoryTransactionsByUUID");
            query.setParameter("uuid",uuid);
            count= query.executeUpdate();
            et.commit();

        }catch (Exception e){
            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        return ResponseImp.success(count);

    }

    @Override
    public Response<List<InventoryTransactions>> findAllTransactionsByProductUUID(String uuid) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        Query query=manager.createNamedQuery("findByProductUUID",InventoryTransactions.class);
        query.setParameter("uuid",uuid);
        List<InventoryTransactions> list=query.getResultList();
        manager.close();
        return ResponseImp.success(list);
    }

    @Override
    public Response<Object>  createTrasactions(List<InventoryTransactions> list) {
        for(InventoryTransactions transactions:list){
            Response<Object> response=createTrasaction(transactions);
            if(response.getStatusCode()!=200) return response;
        }
        return ResponseImp.success(true);
    }

    @Override
    public Response<Object> updateTransactionWithInvoiceNo(List<InventoryTransactions> list, String invoiceNo) {
        for(InventoryTransactions transaction:list){
            transaction.setInvoiceNo(invoiceNo);
            Response<Object> response=updateTransaction(transaction);
            if(response.getStatusCode()!=200) return response;
        }
        return ResponseImp.success(true);
    }

    @Override
    public Response<List<InventoryTransactions>> findfindAllTransactionsByInvoiceNo(String invoiceNo) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        Query query=manager.createNamedQuery("findInventoryTransactions");
        query.setParameter("invoiceNo",invoiceNo);
        List<InventoryTransactions> list=query.getResultList();
        manager.close();
        return ResponseImp.success(list);
    }

    @Override
    public Response<Integer>  deleteTransactions(List<InventoryTransactions> list) {
        int count=0;
        for(InventoryTransactions inventoryTransaction:list){
            Response<Integer> response=deleteTransaction(inventoryTransaction.getUUID());
            if(response.getStatusCode()!=200) return ResponseImp.error(response.getResponseStatus(),count);
            else count++;
        }
        return ResponseImp.success(count);
    }

    public static InventoryTransactionsRepository getInstance(){
        if(instance==null){
            synchronized (InventoryTransactionsRepository.class){
                if(instance==null){
                    instance=new InventoryTransactionsRepository();
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
