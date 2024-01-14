package io.github.robertomike.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Sort {
    ASC("asc"),
    DESC("desc");

    final String sort;
}
