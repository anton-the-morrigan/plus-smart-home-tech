package ru.yandex.practicum.warehouse.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    Double width;
    Double height;
    Double depth;
}
