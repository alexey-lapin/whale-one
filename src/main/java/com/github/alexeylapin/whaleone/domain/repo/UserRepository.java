package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.model.Page;
import com.github.alexeylapin.whaleone.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(long id);

    Page<User> findAll(int page, int size);

    User save(User user);

}
