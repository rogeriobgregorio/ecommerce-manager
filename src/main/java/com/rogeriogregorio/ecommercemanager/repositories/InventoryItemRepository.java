package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, Long> {

    InventoryItemEntity findByProduct_Id(Long productId);

    Optional<InventoryItemEntity> findByProduct(ProductEntity product);
}
