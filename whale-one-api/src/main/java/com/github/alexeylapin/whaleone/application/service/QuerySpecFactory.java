package com.github.alexeylapin.whaleone.application.service;

import com.github.alexeylapin.whaleone.domain.QuerySpec;

public interface QuerySpecFactory {

    QuerySpec create(String string);

}
