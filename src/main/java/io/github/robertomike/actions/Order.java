package io.github.robertomike.actions;

import io.github.robertomike.enums.Sort;
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
