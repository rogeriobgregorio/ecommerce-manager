package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, OrderItemPK> {
}
