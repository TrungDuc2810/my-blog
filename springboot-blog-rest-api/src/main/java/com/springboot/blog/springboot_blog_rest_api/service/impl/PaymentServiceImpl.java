package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Order;
import com.springboot.blog.springboot_blog_rest_api.entity.Payment;
import com.springboot.blog.springboot_blog_rest_api.exception.ResourceNotFoundException;
import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.PaymentDto;
import com.springboot.blog.springboot_blog_rest_api.repository.OrderRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.PaymentRepository;
import com.springboot.blog.springboot_blog_rest_api.service.PaymentService;
import com.springboot.blog.springboot_blog_rest_api.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private Mapper mapper;

    @Override
    public PaymentDto addPayment(PaymentDto paymentDto) {
        Payment payment = mapper.mapToEntity(paymentDto, Payment.class);

        Order order = orderRepository.findById(paymentDto.getOrderId()).orElseThrow(()
                -> new ResourceNotFoundException("Order", "id", String.valueOf(paymentDto.getOrderId())));

        Payment savedPayment = paymentRepository.save(payment);

        return mapper.mapToDto(savedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto updatePayment(long id, PaymentDto paymentDto) {
        return null;
    }

    @Override
    public PaymentDto getPayment(long id) {
        return null;
    }

    @Override
    public ListResponse<PaymentDto> getAllPayments(int pageNo, int pageSize, String sortBy, String sortDir) {
        return null;
    }

    @Override
    public void deletePayment(long id) {

    }
}
