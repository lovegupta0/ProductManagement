package org.lg.Repository;

import org.lg.Model.InventoryTransactions;
import org.lg.common.Response;

import java.util.List;

public interface InventoryTransactionsRepo {
    public Response<Object> createTrasaction(InventoryTransactions transactions);
    public Response<Object>  updateTransaction(InventoryTransactions transactions);
    public Response<Integer>  deleteTransaction(String uuid);
    public Response<List<InventoryTransactions>> findAllTransactionsByProductUUID(String uuid);
    public Response<Object>  createTrasactions(List<InventoryTransactions> list);
    public Response<Object>  updateTransactionWithInvoiceNo(List<InventoryTransactions> list,String invoiceNo);
    public Response<List<InventoryTransactions>> findfindAllTransactionsByInvoiceNo(String invoiceNo);
    public Response<Integer>  deleteTransactions(List<InventoryTransactions> list);
}
