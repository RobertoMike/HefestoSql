package io.github.robertomike.hefesto.actions;

import io.github.robertomike.hefesto.enums.Sort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Order {
    private final String field;
    private Sort sort = Sort.ASC;
}
