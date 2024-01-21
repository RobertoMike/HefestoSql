package io.github.robertomike.hefesto.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Page<T> {
    private List<T> data;
    private long page;
    private long total;
}
