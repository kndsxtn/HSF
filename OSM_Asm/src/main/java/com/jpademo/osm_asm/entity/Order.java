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


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", nullable = false)
    @Valid
    private Customer customer;

    @NotBlank(message = "Address is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    private String region;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    @Column(name = "order_date")
    private LocalDateTime orderDate;
}