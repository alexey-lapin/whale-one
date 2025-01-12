package com.github.alexeylapin.whaleone.domain.repo;


import java.util.List;

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

}
