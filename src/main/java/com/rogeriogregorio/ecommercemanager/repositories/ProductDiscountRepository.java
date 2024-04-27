package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
}
