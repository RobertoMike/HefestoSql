package io.github.robertomike.hefesto.actions;

import io.github.robertomike.hefesto.enums.Sort;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class Order {
    private final String field;
    private Sort sort = Sort.ASC;
}
