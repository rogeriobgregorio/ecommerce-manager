package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, Long> {
}
