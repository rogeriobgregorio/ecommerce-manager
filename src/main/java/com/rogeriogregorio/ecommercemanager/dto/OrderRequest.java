package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.UserEntity;

import java.time.Instant;

public class OrderRequest {

    private Long id;
    private Instant moment;
    private UserEntity client;

    public OrderRequest() { }

    public OrderRequest(Instant moment, UserEntity client) {
        this.moment = moment;
        this.client = client;
    }

    public OrderRequest(Long id, Instant moment, UserEntity client) {
        this.id = id;
        this.moment = moment;
        this.client = client;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Instant getMoment() { return moment; }

    public UserEntity getClient() { return client; }
}
