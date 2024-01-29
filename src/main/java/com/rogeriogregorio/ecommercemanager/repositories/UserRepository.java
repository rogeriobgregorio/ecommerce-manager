package com.rogeriogregorio.ecommercemanager.repositories;

import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE lower(u.name) LIKE lower(concat('%', :name, '%'))")
    List<UserEntity> findByName(@Param("name") String name);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    UserEntity findUserWithOrders(@Param("id") Integer id);
}
