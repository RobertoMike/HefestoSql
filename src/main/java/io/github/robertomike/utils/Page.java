package io.github.robertomike.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.AbstractList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Page<T> {
    private List<T> data;
    private long page;
    private long total;
}
