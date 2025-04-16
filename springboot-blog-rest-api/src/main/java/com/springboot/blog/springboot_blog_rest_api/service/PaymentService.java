package com.springboot.blog.springboot_blog_rest_api.service;

import com.springboot.blog.springboot_blog_rest_api.payload.ListResponse;
import com.springboot.blog.springboot_blog_rest_api.payload.PaymentDto;

public interface PaymentService {
    PaymentDto addPayment(PaymentDto paymentDto);
    PaymentDto updatePayment(long id, PaymentDto paymentDto);
    PaymentDto getPayment(long id);
    ListResponse<PaymentDto> getAllPayments(int pageNo, int pageSize, String sortBy, String sortDir);
    void deletePayment(long id);
}
