package org.lg.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "LProduct")
@NamedQuery(name = "findAllProduct",query = "select p from Product p")
@NamedQuery(name = "findProductById",query ="select p from Product p where p.UUID=:id" )
public class Product {
    @Id
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator",strategy = "org.lg.UUID.UUIDGenerator")
    private String UUID;
    private String name;
    private float quantity;
    private float cost;
    @Column(name = "selling_price")
    private float sellingPrice;
    private float discount;
    private float vat;

    public Product() {
    }

    public Product(String name, float quantity, float cost, float sellingPrice, float discount, float vat) {
        this.name = name;
        this.quantity = quantity;
        this.cost = cost;
        this.sellingPrice = sellingPrice;
        this.discount = discount;
        this.vat = vat;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    @Override
    public String toString() {
        return "Product{" +
                "UUID='" + UUID + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", cost=" + cost +
                ", sellingPrice=" + sellingPrice +
                ", discount=" + discount +
                ", vat=" + vat +
                '}';
    }
}
