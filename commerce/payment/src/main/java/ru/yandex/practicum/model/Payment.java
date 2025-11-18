package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.payment.dto.PaymentState;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    UUID paymentId;

    @Column(name = "total_payment")
    Double totalPayment;

    @Column(name = "delivery_total")
    Double deliveryTotal;

    @Column(name = "fee_total")
    Double feeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state")
    PaymentState paymentState;
}
