package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.User;
import com.github.alexeylapin.whaleone.domain.model.UserListElement;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(long id);

    Page<UserListElement> findAll(int page, int size);

}
