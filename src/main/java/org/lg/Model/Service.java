package org.lg.Model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Service {
    private String passKey;
    private String UUID;
    private String name;
    private String email;

    public Service() {
    }

    public Service(String passKey, String UUID, String name, String email) {
        this.passKey = passKey;
        this.UUID = UUID;
        this.name = name;
        this.email = email;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return passKey.equals(service.passKey) && UUID.equals(service.UUID) && name.equals(service.name) && email.equals(service.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passKey, UUID, name, email);
    }

    @Override
    public String toString() {
        return "Service{" +
                "passKey='" + passKey + '\'' +
                ", UUID='" + UUID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
