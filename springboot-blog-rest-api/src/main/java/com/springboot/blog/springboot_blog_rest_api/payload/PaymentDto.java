package com.springboot.blog.springboot_blog_rest_api.payload;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private long id;
    private String vnp_BankCode;
    private String vnp_TransactionNo;
    private String vnp_TmnCode;
    private String vnp_OrderInfo;
    private String vnp_TxnRef;
    private BigDecimal vnp_Amount;
    private String vnp_BankTranNo;
    private String vnp_ResponseCode;
    private LocalDateTime vnp_PayDate;
    private String vnp_SecureHash;
    private String vnp_CardType;
    private String vnp_TransactionStatus;
    private String paymentStatus;
    private LocalDateTime created_at;
    private String raw_callback_url;
    private long orderId;
}
