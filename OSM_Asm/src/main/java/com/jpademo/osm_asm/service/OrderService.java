package com.jpademo.osm_asm.service;

import com.jpademo.osm_asm.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    Order getOrderById(String id);

    void saveOrder(Order order);
}
