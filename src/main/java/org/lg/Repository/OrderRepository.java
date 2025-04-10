package org.lg.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.lg.common.Response;
import org.lg.common.ResponseImp;
import org.lg.common.ResponseStatus;
import org.lg.common.StatusCode;
import org.lg.internal.Connection.EntityManagerFactoryUtil;
import org.lg.Model.*;
import org.lg.internal.Security.JwtUtil;

import java.util.*;

public class OrderRepository implements OrderRepo{
    private EntityManagerFactory factory;
    private ProductRepo productRepo;
    private InventoryTransactionsRepo inventoryTransactionsRepo;
    private static volatile OrderRepository instance;
    private Service service;
    private OrderRepository(){
        factory= EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        productRepo=ProductRepository.getInstance();
        inventoryTransactionsRepo=InventoryTransactionsRepository.getInstance();
        service=StoreRepository.getInstance().getService();
    }

    private Response<Order> addOrder(Order order) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();

            manager.persist(order);
            et.commit();
        } catch (Exception e){
            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        }
        finally {
            manager.close();
        }
        return ResponseImp.success(order);
    }

    @Override
    public ResponseImp<List<Order>> getAllOrder() {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        TypedQuery<Order> findAll=manager.createNamedQuery("findAllOrder",Order.class);
        List<Order> list=findAll.getResultList();
        manager.close();
        return ResponseImp.success(list);
    }

    @Override
    public ResponseImp<Order> findByOrderId(String id) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        Optional<Order> order=Optional.ofNullable(manager.find(Order.class,id));
        manager.close();
        if(order.isPresent()){
            return ResponseImp.success(order.get());
        }
        return ResponseImp.error(StatusCode.NOT_FOUND);
    }

    @Override
    public ResponseImp<Boolean> deleteOrder(String id) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager=factory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        List<Product> products=new ArrayList<>();
        List<InventoryTransactions> inventoryTransactionsList;
        try {
            et.begin();
            Order order=manager.find(Order.class,id);
            if(order!=null){
                Response<List<InventoryTransactions>> response=inventoryTransactionsRepo.findfindAllTransactionsByInvoiceNo(order.getInvoiceNo());
                inventoryTransactionsList=response.getData();
                for (OrderRecords orderRecord:order.getOrderList()){
                    Product product=orderRecord.getProduct();
                    product.setQuantity(product.getQuantity()+orderRecord.getSoldQuantity());
                }
                productRepo.updateProducts(products);
                inventoryTransactionsRepo.deleteTransactions(inventoryTransactionsList);
                manager.remove(order);
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
    public Response<Order> updateOrder(Order order) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et = manager.getTransaction();
        List<OrderRecords> productsInc=new ArrayList<>();
        List<OrderRecords> productsDec=new ArrayList<>();
        List<OrderRecords> productsAdded=new ArrayList<>();
        List<OrderRecords> productsRemove=new ArrayList<>();
        Map<String,OrderRecords> orderRecordsMap=new HashMap<>();
        Map<String,OrderRecords> oldOrderRecordsMap=new HashMap<>();
        Map<String,InventoryTransactions> inventoryTransactionsMap=new HashMap<>();
        for(OrderRecords orderRecord:order.getOrderList()){
            orderRecordsMap.put(orderRecord.getProduct().getUUID(),orderRecord);
        }
        for(InventoryTransactions inventoryTransaction:inventoryTransactionsRepo.findfindAllTransactionsByInvoiceNo(order.getInvoiceNo()).getData()){
            inventoryTransactionsMap.put(inventoryTransaction.getProduct().getUUID(),inventoryTransaction);
        }

        try {
            et.begin();
            Response<Order> prevOrderRes=findByOrderId(order.getUUID());
            if(prevOrderRes.getStatusCode()==200){
                Order prevOrder=prevOrderRes.getData();
                for(OrderRecords oldOrderRecord:prevOrder.getOrderList()){
                    if(orderRecordsMap.containsKey(oldOrderRecord.getProduct().getUUID())){
                        OrderRecords newOrderRecord=orderRecordsMap.get(oldOrderRecord.getProduct().getUUID());
                        if(oldOrderRecord.getSoldQuantity()!=newOrderRecord.getSoldQuantity()){
                            if(oldOrderRecord.getSoldQuantity()>newOrderRecord.getSoldQuantity()){
                                productsDec.add(oldOrderRecord);
                            }
                            else{
                                productsInc.add(oldOrderRecord);
                            }
                        }
                    }
                    else{
                        productsRemove.add(oldOrderRecord);
                    }
                    oldOrderRecordsMap.put(oldOrderRecord.getProduct().getUUID(),oldOrderRecord);
                }
                for(OrderRecords newOrderRecord:order.getOrderList()){
                    if(!oldOrderRecordsMap.containsKey(newOrderRecord.getProduct().getUUID())){
                        productsAdded.add(newOrderRecord);
                    }
                }
                for(OrderRecords orderRecord:productsRemove){
                    InventoryTransactions transaction=inventoryTransactionsMap.get(orderRecord.getProduct().getUUID());
                    inventoryTransactionsRepo.deleteTransaction(transaction.getUUID());
                    Product product=orderRecord.getProduct();
                    product.setQuantity(product.getQuantity()+orderRecord.getSoldQuantity());
                    productRepo.updateProduct(product);
                }
                for(OrderRecords orderRecord:productsAdded){
                    InventoryTransactions transaction=new InventoryTransactions(orderRecord.getProduct(),orderRecord.getSoldQuantity(),Transactions.OUT);
                    transaction.setInvoiceNo(order.getInvoiceNo());
                    inventoryTransactionsRepo.createTrasaction(transaction);
                    Product product=orderRecord.getProduct();
                    product.setQuantity(product.getQuantity()-orderRecord.getSoldQuantity());
                    productRepo.updateProduct(product);
                }
                for(OrderRecords orderRecord:productsInc){
                    float incValue=orderRecordsMap.get(orderRecord.getProduct().getUUID()).getSoldQuantity()-orderRecord.getSoldQuantity();
                    InventoryTransactions transaction=inventoryTransactionsMap.get(orderRecord.getProduct().getUUID());
                    transaction.setTransactionQuantity(transaction.getTransactionQuantity()+incValue);
                    inventoryTransactionsRepo.updateTransaction(transaction);
                    Product product=orderRecord.getProduct();
                    product.setQuantity(product.getQuantity()-incValue);
                    productRepo.updateProduct(product);
                }
                for(OrderRecords orderRecord:productsDec){
                    float decValue=orderRecord.getSoldQuantity()-orderRecordsMap.get(orderRecord.getProduct().getUUID()).getSoldQuantity();
                    InventoryTransactions transaction=inventoryTransactionsMap.get(orderRecord.getProduct().getUUID());
                    transaction.setTransactionQuantity(transaction.getTransactionQuantity()-decValue);
                    inventoryTransactionsRepo.updateTransaction(transaction);
                    Product product=orderRecord.getProduct();
                    product.setQuantity(product.getQuantity()+decValue);
                    productRepo.updateProduct(product);
                }
            }
            else return prevOrderRes;
            manager.merge(order);
            et.commit();
        } catch (Exception e) {
           return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        } finally {
            manager.close();
            oldOrderRecordsMap.clear();
            orderRecordsMap.clear();
            inventoryTransactionsMap.clear();
            productsInc.clear();
            productsDec.clear();
            productsAdded.clear();
            productsRemove.clear();

        }
        return ResponseImp.success(order);
    }

    @Override
    public Response<Order> doCheckOut(Order order) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        EntityManager manager = factory.createEntityManager();
        EntityTransaction et = manager.getTransaction();
        List<Product> products=new ArrayList<>();
        List<InventoryTransactions> inventoryTransactionsList=new ArrayList<>();
        for(OrderRecords orderRecords: order.getOrderList()){
            Product product=orderRecords.getProduct();
            InventoryTransactions transactions =new InventoryTransactions(product,orderRecords.getSoldQuantity(), Transactions.OUT);
            inventoryTransactionsList.add(transactions);
            product.setQuantity(product.getQuantity()-orderRecords.getSoldQuantity());
            products.add(product);
        }
        try {
            et.begin();
            inventoryTransactionsRepo.createTrasactions(inventoryTransactionsList);
            productRepo.updateProducts(products);
            addOrder(order);
            inventoryTransactionsRepo.updateTransactionWithInvoiceNo(inventoryTransactionsList,order.getInvoiceNo());
            et.commit();
        } catch (Exception e) {
            et.rollback();
            return ResponseImp.error(ResponseStatus.of(400,e.getMessage()));
        } finally {
            manager.close();

        }
        return ResponseImp.success(order);
    }

    @Override
    public Response<Boolean> isAbleToCheckOut(Order order) {
        checkService();
        if(!JwtUtil.validateService(service)){
            return ResponseImp.error(StatusCode.UNAUTHORIZED);
        }
        for(OrderRecords orderRecords:order.getOrderList()){
            if(orderRecords.getProduct().getQuantity()<orderRecords.getSoldQuantity()) return ResponseImp.error(ResponseStatus.of(600,orderRecords.getProduct().getName()+" sold quantity exceed the inventory "),false);
        }

        return ResponseImp.success(true);
    }

    public static OrderRepository getInstance(){
        if(instance==null){
            synchronized (OrderRepository.class){
                if(instance==null){
                    instance=new OrderRepository();
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
