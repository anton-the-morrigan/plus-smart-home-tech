package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id")
    UUID shoppingCartId;

    String username;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cart_products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Integer> products;

    @Enumerated(EnumType.STRING)
    ShoppingCartState state;
}
