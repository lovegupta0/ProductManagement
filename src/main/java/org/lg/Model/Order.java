package org.lg.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "LOrder")
@NamedQuery(name = "findAllOrder",query = "select o from Order o")
@NamedQuery(name = "findByOrderId",query = "select o from Order o where o.UUID=:id")
public class Order {
    @Id
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator",strategy = "org.lg.UUID.UUIDGenerator")
    private String UUID;
    @Column(name = "invoice_no", unique = true, nullable = false)
    private String invoiceNo;
    @Column(name = "invoice_date")
    @CreationTimestamp
    private LocalDate invoiceDate;
    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id",nullable = false)
    private Client client;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_List", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "order_list")
    private List<OrderRecords> orderList;

    public Order() {
    }

    public Order( Client client, List<OrderRecords> orderList) {

        this.client = client;
        this.orderList = orderList;
    }

    @PrePersist
    public void GenerateInvoiceNo(){
        if(this.invoiceNo!=null && this.invoiceNo.length()>0) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        setInvoiceNo("INV"+timestamp);
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<OrderRecords> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderRecords> orderList) {
        this.orderList = orderList;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "UUID='" + UUID + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", client=" + client +
                ", orderList=" + orderList +
                '}';
    }


}
