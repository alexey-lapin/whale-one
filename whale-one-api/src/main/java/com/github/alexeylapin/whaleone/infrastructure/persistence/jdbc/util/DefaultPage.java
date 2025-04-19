package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import com.github.alexeylapin.whaleone.domain.Page;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.function.Function;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class DefaultPage<T> implements Page<T> {

    @Delegate
    private final org.springframework.data.domain.Page<T> delegate;

    @Override
    public <U> DefaultPage<U> map(Function<T, U> mapper) {
        return new DefaultPage<>(delegate.map(mapper));
    }

}
