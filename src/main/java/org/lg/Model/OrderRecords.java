package org.lg.Model;

import jakarta.persistence.*;

@Embeddable
public class OrderRecords {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    private float soldQuantity;
    private float cost;
    private float sellingPrice;
    private float discout;
    private float vat;

    public OrderRecords() {
    }

    public OrderRecords(Product product, float soldQuantity, float cost, float sellingPrice, float discout, float vat) {
        this.product = product;
        this.soldQuantity = soldQuantity;
        this.cost = cost;
        this.sellingPrice = sellingPrice;
        this.discout = discout;
        this.vat = vat;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public float getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(float soldQuantity) {
        this.soldQuantity = soldQuantity;
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

    public float getDiscout() {
        return discout;
    }

    public void setDiscout(float discout) {
        this.discout = discout;
    }

    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    @Override
    public String toString() {
        return "OrderRecords{" +
                "product=" + product +
                ", soldQuantity=" + soldQuantity +
                ", cost=" + cost +
                ", sellingPrice=" + sellingPrice +
                ", discout=" + discout +
                ", vat=" + vat +
                '}';
    }
}
