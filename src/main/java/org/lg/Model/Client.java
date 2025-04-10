package org.lg.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "LClient")
@NamedQuery(name = "findAllClient",query = "select c from Client c")
@NamedQuery(name = "findClientWithOrderList",query = "select c from Client c join fetch c.orderList where c.UUID=:id")
public class Client {

    @Id
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator",strategy = "org.lg.UUID.UUIDGenerator")
    private String UUID;
    private String name;
    private String address;
    private String mobileNumber;
    private String email;
    @OneToMany(mappedBy = "client",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Order> orderList;


    public Client() {
    }

    public Client(String name, String address, String mobileNumber, String email, List<Order> orderList) {

        this.name = name;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.orderList = orderList;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public String toString() {
        return "Client{" +
                "UUID='" + UUID + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
