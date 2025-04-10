package org.lg.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "LStore")
@NamedQuery(name = "findByUsername", query = "select s from Store s where s.username=:username")
@NamedQuery(name = "findUser",query = "select u from Store u")
public class Store {
    @Id
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator",strategy = "org.lg.UUID.UUIDGenerator")
    private String UUID;
    private String username;
    private String email;
    @Column(name = "mobile_number")
    private String mobileNumber;
    private String name;
    private String password;
    private String address;

    public Store() {
    }

    public Store(String username, String email, String mobileNumber, String name, String password, String address) {
        this.username = username;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.password = password;
        this.address = address;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
