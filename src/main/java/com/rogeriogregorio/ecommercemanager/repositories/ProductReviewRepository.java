package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.ProductReview;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.ProductReviewPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, ProductReviewPK> {
}
