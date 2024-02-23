package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    OrderItemEntity findById_OrderEntityAndId_ProductEntity(OrderEntity orderEntity, ProductEntity productEntity);
}
