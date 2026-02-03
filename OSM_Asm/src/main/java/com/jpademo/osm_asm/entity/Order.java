package com.jpademo.osm_asm.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @Column(name = "order_id", length = 10)
    private String  orderId;

    // --- Thay đổi lớn nhất ở đây ---
    // Thay vì lưu tên/email trực tiếp, ta lưu object Customer
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // <--- Bắt buộc phải có cascade = CascadeType.ALL
    @JoinColumn(name = "customer_id", nullable = false)
    @Valid
    private Customer customer;

    // --- Các thông tin giao hàng (Shipping) giữ nguyên ---
    @NotBlank(message = "Address is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city; // Giữ nguyên String cho đơn giản

    private String region;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country; // Giữ nguyên String

    @Column(name = "order_date")
    private LocalDateTime orderDate;
}