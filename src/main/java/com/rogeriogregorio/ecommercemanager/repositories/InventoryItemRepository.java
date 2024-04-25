package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.InventoryItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    boolean existsByProduct(Product product);

    Optional<InventoryItem> findByProduct(Product product);
}
