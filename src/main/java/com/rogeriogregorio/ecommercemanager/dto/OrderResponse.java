package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.UserEntity;

import java.time.Instant;

public class OrderResponse {

    private Long id;
    private Instant moment;
    private UserEntity client;

    public OrderResponse() { }

    public OrderResponse(Long id, Instant moment, UserEntity client) {
        this.id = id;
        this.moment = moment;
        this.client = client;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Instant getMoment() { return moment; }

    public void setMoment(Instant moment) { this.moment = moment; }

    public UserEntity getClient() { return client; }

    public void setClient(UserEntity client) { this.client = client; }
}
