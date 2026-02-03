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


    // 1. Hiển thị form đặt hàng (GET Request)
    @GetMapping("/order")
    public String showOrderForm(Model model) {
        // Tạo một object rỗng để hứng dữ liệu từ form
        model.addAttribute("order", new Order());
        return "order-form"; // Trả về file order-form.html
    }

    // 2. Xử lý khi nhấn nút "Add" (POST Request)
    @PostMapping("/order")
    public String submitOrder(@Valid @ModelAttribute("order") Order order,
                              BindingResult result,
                              Model model) {
        // A. Kiểm tra lỗi Validate (Annotation @NotBlank, @Size...)
        if (result.hasErrors()) {
            return "order-form"; // Nếu có lỗi, quay lại form để hiện thông báo đỏ
        }

        try {
            // B. Gọi Service để lưu (Kiểm tra trùng Email + Sinh mã ORDxxx)
            orderService.saveOrder(order);
        } catch (IllegalArgumentException e) {
            // C. Nếu trùng email -> Thêm lỗi thủ công vào field "email"
            result.rejectValue("email", "error.order", e.getMessage());
            return "order-form";
        }

        // D. Thành công -> Chuyển hướng (Redirect) để tránh resubmit form
        // Tạm thời redirect về trang form kèm thông báo success
        return "redirect:/order?success";
    }

    // 3. Hiển thị danh sách đơn hàng (GET Request)
    @GetMapping("/orders") // Đường dẫn là /orders
    public String listOrders(Model model) {
        // Gọi Service lấy toàn bộ danh sách
        model.addAttribute("orders", orderService.getAllOrders());
        return "order-list"; // Trả về file order-list.html
    }

    // 4. Xem chi tiết đơn hàng (GET Request với ID động)
    @GetMapping("/order/{id}")
    public String viewOrderDetails(@PathVariable("id") String orderId, Model model) {
        // Gọi Service tìm order theo ID
        // Nếu không thấy, Service sẽ ném lỗi (bạn có thể try-catch nếu muốn kỹ hơn)
        Order order = orderService.getOrderById(orderId);

        // Gửi object order sang view
        model.addAttribute("order", order);

        return "order-detail"; // Trả về file order-detail.html
    }
}