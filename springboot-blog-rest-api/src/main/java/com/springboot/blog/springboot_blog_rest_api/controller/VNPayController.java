package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.entity.Order;
import com.springboot.blog.springboot_blog_rest_api.entity.OrderStatus;
import com.springboot.blog.springboot_blog_rest_api.payload.PaymentDto;
import com.springboot.blog.springboot_blog_rest_api.repository.OrderRepository;
import com.springboot.blog.springboot_blog_rest_api.service.OrderService;
import com.springboot.blog.springboot_blog_rest_api.service.PaymentService;
import com.springboot.blog.springboot_blog_rest_api.service.impl.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {
    private final VNPayService vnPayService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public VNPayController(VNPayService vnPayService, PaymentService paymentService, OrderRepository orderRepository, OrderService orderService) {
        this.paymentService = paymentService;
        this.vnPayService = vnPayService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/create-payment")
    public ResponseEntity<String> submitOrder(@RequestParam("amount") BigDecimal orderTotal,
                                              @RequestParam("orderInfo") String orderInfo,
                                              HttpServletRequest request) {
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo);
        return new ResponseEntity<>(vnpayUrl, HttpStatus.OK);
    }

    @GetMapping("/callback-payment")
    public ResponseEntity<?> paymentCompleted(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);
        String queryString = request.getQueryString();

        Map<String, String> params = new HashMap<>();
        if (queryString != null) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = keyValue.length > 1
                        ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                        : "";
                params.put(key, value);
            }
        }

        if (paymentStatus == 1) {
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setVnp_Amount(new BigDecimal(params.get("vnp_Amount")).divide(new BigDecimal(100)));
            paymentDto.setVnp_BankCode(params.get("vnp_BankCode"));
            paymentDto.setVnp_TransactionNo(params.get("vnp_TransactionNo"));
            paymentDto.setVnp_TmnCode(params.get("vnp_TmnCode"));
            paymentDto.setVnp_OrderInfo(params.get("vnp_OrderInfo"));
            paymentDto.setVnp_TxnRef(params.get("vnp_TxnRef"));
            paymentDto.setVnp_BankTranNo(params.get("vnp_BankTranNo"));
            paymentDto.setVnp_ResponseCode(params.get("vnp_ResponseCode"));
            paymentDto.setVnp_SecureHash(params.get("vnp_SecureHash"));
            paymentDto.setVnp_CardType(params.get("vnp_CardType"));
            paymentDto.setVnp_TransactionStatus(params.get("vnp_TransactionStatus"));
            paymentDto.setRaw_callback_url(request.getRequestURL() + "?" + request.getQueryString());
            paymentDto.setCreated_at(LocalDateTime.now());
            paymentDto.setPaymentStatus("Success");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            paymentDto.setVnp_PayDate(LocalDateTime.parse(params.get("vnp_PayDate"), formatter));

            try {
                paymentDto.setOrderId(Long.parseLong(params.get("vnp_TxnRef")));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid orderId format from vnp_TxnRef");
            }

            PaymentDto saved = paymentService.addPayment(paymentDto);
            Order order = orderRepository.findById(saved.getOrderId()).orElse(null);
            order.setStatus(OrderStatus.valueOf("CONFIRMED"));
            orderRepository.save(order);

            response.sendRedirect("http://localhost:5173/orders");

            return ResponseEntity.ok(saved);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
    }
}