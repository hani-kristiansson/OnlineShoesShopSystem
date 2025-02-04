package model;

import java.time.Instant;
import java.time.LocalDateTime;

public class Orders {

    private int id;
    private int customerId;
    private LocalDateTime orderCreateDate;
    private LocalDateTime orderUpdateDate;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(LocalDateTime orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public LocalDateTime getOrderUpdateDate() {
        return orderUpdateDate;
    }

    public void setOrderUpdateDate(LocalDateTime orderUpdateDate) {
        this.orderUpdateDate = orderUpdateDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}