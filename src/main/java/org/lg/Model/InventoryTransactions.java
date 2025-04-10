package org.lg.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Table(name = "linventory_transactions")
@NamedQuery(name = "findByProductUUID",query = "select i from InventoryTransactions i where i.product.UUID=:uuid")
@NamedQuery(name = "deleteInventoryTransactionsByUUID" ,query = "DELETE FROM InventoryTransactions it WHERE it.UUID = :uuid")
@NamedQuery(name = "findInventoryTransactions",query = "select i from InventoryTransactions i where i.invoiceNo=:invoiceNo")
public class InventoryTransactions {
    @Id
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator",strategy = "org.lg.UUID.UUIDGenerator")
    private String UUID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "transaction_quantity")
    private float transactionQuantity;
    @CreationTimestamp
    @Column(name = "transaction_date")
    private LocalDate transactionDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private Transactions transactionType;
    @Column(name = "invoice_no",nullable = true)
    private String invoiceNo;

    public InventoryTransactions() {
    }

    public InventoryTransactions(Product product, float transactionQuantity, Transactions transactionType) {
        this.product = product;
        this.transactionQuantity = transactionQuantity;
        this.transactionType = transactionType;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public float getTransactionQuantity() {
        return transactionQuantity;
    }

    public void setTransactionQuantity(float transactionQuantity) {
        this.transactionQuantity = transactionQuantity;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Transactions getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Transactions transactionType) {
        this.transactionType = transactionType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    @Override
    public String toString() {
        return "InventoryTransactions{" +
                "UUID='" + UUID + '\'' +
                ", product=" + product +
                ", transactionQuantity=" + transactionQuantity +
                ", transactionDate=" + transactionDate +
                ", transactionType=" + transactionType +
                ", invoiceNo='" + invoiceNo + '\'' +
                '}';
    }
}
