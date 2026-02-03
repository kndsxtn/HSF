package com.jpademo.osm_asm.service.impl;

import com.jpademo.osm_asm.entity.Customer;
import com.jpademo.osm_asm.entity.Order;
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
        // --- BƯỚC 1: XỬ LÝ KHÁCH HÀNG (CUSTOMER) ---
        Customer formCustomer = order.getCustomer();

        // Kiểm tra xem email này đã có trong DB chưa
        Optional<Customer> existingCustomer = customerRepository.findByEmail(formCustomer.getEmail());

        if (existingCustomer.isPresent()) {
            // TÌNH HUỐNG 1: Khách hàng CŨ
            Customer dbCustomer = existingCustomer.get();
            // Cập nhật thông tin mới (nếu có thay đổi)
            dbCustomer.setFirstName(formCustomer.getFirstName());
            dbCustomer.setLastName(formCustomer.getLastName());
            dbCustomer.setPhoneNumber(formCustomer.getPhoneNumber());

            // Gán khách hàng cũ (đã có ID) vào đơn hàng
            order.setCustomer(dbCustomer);
        } else {
            // TÌNH HUỐNG 2: Khách hàng MỚI TINH
            // ---> SỬA Ở ĐÂY: Lưu thủ công Khách hàng mới trước <---
            Customer savedCustomer = customerRepository.save(formCustomer);

            // Gán khách hàng vừa lưu (đã có ID sinh ra) vào đơn hàng
            order.setCustomer(savedCustomer);
        }

        // --- BƯỚC 2: SINH MÃ VÀ LƯU ORDER ---
        String nextId = generateOrderId();
        order.setOrderId(nextId);

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        // Lúc này Customer bên trong Order chắc chắn đã có ID, không bị lỗi Transient nữa
        orderRepository.save(order);
    }

    // Hàm logic sinh mã (Private use only)
    private String generateOrderId() {
        // Tìm đơn hàng có ID lớn nhất hiện tại (VD: ORD005)
        Order lastOrder = orderRepository.findTopByOrderByOrderIdDesc();

        if (lastOrder == null) {
            return "ORD001"; // Nếu chưa có đơn nào -> Bắt đầu từ 001
        }

        String lastId = lastOrder.getOrderId(); // VD: "ORD005"
        try {
            // Cắt chuỗi lấy phần số: "005" -> parse ra số 5
            int number = Integer.parseInt(lastId.substring(3));

            // Tăng lên 1 -> 6
            int nextNumber = number + 1;

            // Format lại thành 3 chữ số: 6 -> "006" -> Ghép thành "ORD006"
            return String.format("ORD%03d", nextNumber);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Phòng trường hợp dữ liệu cũ bị sai format, fallback về timestamp
            return "ORD" + (System.currentTimeMillis() % 1000);
        }
    }
}