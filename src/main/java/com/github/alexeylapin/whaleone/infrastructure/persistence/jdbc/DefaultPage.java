package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.repo.Page;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class DefaultPage<T> implements Page<T> {

    @Delegate
    private final org.springframework.data.domain.Page<T> delegate;

}
