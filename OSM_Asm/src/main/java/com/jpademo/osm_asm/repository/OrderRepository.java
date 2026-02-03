package com.jpademo.osm_asm.repository;

import com.jpademo.osm_asm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Order findTopByOrderByOrderIdDesc();
}