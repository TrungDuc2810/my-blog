package com.springboot.blog.springboot_blog_rest_api.payload;

import com.springboot.blog.springboot_blog_rest_api.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private long id;
    private String vnp_txn_ref;
    private String vnp_transaction_no;
    private BigDecimal vnp_amount;
    private String vnp_bank_code;
    private String vnp_card_type;
    private String vnp_order_info;
    private String vnp_response_code;
    private String vnp_pay_date;
    private String vnp_transaction_status;
    private String raw_data;
    private long order_id;
    private PaymentStatus paymentStatus;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
