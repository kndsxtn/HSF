package com.jpademo.osm_asm.controller;

import com.jpademo.osm_asm.entity.Order;
import com.jpademo.osm_asm.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.jpademo.osm_asm.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/order")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order-form";
    }

    @PostMapping("/order")
    public String submitOrder(@Valid @ModelAttribute("order") Order order,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            return "order-form";
        }

        try {
            orderService.saveOrder(order);
        } catch (IllegalArgumentException e) {
            result.rejectValue("customer.email", "error.order", e.getMessage());
            return "order-form";
        }

        return "redirect:/order?success";
    }


    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order-list";
    }


    @GetMapping("/order/{id}")
    public String viewOrderDetails(@PathVariable("id") String orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order-detail";
    }
}