package com.github.alexeylapin.whaleone.domain.repo;


import java.util.List;
import java.util.function.Function;

public interface Page<T> {

    int getNumber();

    int getSize();

    List<T> getContent();

    boolean isFirst();

    boolean isLast();

    boolean hasNext();

    boolean hasPrevious();

    int getTotalPages();

    long getTotalElements();

    <U> Page<U> map(Function<T, U> mapper);

}
