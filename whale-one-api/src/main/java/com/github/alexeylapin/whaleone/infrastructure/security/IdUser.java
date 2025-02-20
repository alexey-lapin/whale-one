package com.github.alexeylapin.whaleone.infrastructure.security;

public interface IdUser {

    long getId();

    String getName();

    Object getDelegate();

}
