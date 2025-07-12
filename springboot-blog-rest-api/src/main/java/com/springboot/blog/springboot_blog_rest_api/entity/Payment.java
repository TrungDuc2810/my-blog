package com.springboot.blog.springboot_blog_rest_api.entity;

import com.springboot.blog.springboot_blog_rest_api.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntity {
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
    @Lob
    @Column(columnDefinition = "TEXT")
    private String raw_callback_url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
