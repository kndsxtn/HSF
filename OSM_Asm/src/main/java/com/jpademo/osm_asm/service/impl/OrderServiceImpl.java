package com.jpademo.osm_asm.service.impl;

import com.jpademo.osm_asm.entity.Country;
import com.jpademo.osm_asm.entity.Customer;
import com.jpademo.osm_asm.entity.Order;
import com.jpademo.osm_asm.repository.CountryRepository;
import com.jpademo.osm_asm.repository.CustomerRepository;
import com.jpademo.osm_asm.repository.OrderRepository;
import com.jpademo.osm_asm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Override
    @Transactional
    public void saveOrder(Order order) {
        Customer formCustomer = order.getCustomer();

        String countryNameInput = order.getCountry();

        Country countryEntity = countryRepository.findByCountryName(countryNameInput)
                .orElseGet(() -> {
                    return countryRepository.save(
                            Country.builder().countryName(countryNameInput).countryCode(countryNameInput.substring(0, 2).toUpperCase()).build()
                    );
                });


        formCustomer.setCountry(countryEntity);

        Optional<Customer> existingCustomer = customerRepository.findByEmail(formCustomer.getEmail());

        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException("Email already exists in the system!");
        } else {
            Customer savedCustomer = customerRepository.save(formCustomer);
            order.setCustomer(savedCustomer);
        }

        String nextId = generateOrderId();
        order.setOrderId(nextId);

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        orderRepository.save(order);
    }

    private String generateOrderId() {
        Order lastOrder = orderRepository.findTopByOrderByOrderIdDesc();
        if (lastOrder == null) {
            return "ORD001";
        }

        String lastId = lastOrder.getOrderId(); // VD: "ORD005"
        try {
            int number = Integer.parseInt(lastId.substring(3));
            int nextNumber = number + 1;
            return String.format("ORD%03d", nextNumber);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return "ORD" + (System.currentTimeMillis() % 1000);
        }
    }
}