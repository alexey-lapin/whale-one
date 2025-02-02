package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.User;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.domain.repo.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
public class UserJdbcRepositoryAdapter implements UserRepository {

    private final UserJdbcRepository repository;

    public UserJdbcRepositoryAdapter(UserJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(user.id(),
                user.version(),
                user.username(),
                user.password(),
                user.enabled(),
                user.authorities().stream()
                        .map(UserAuthorityEntity::new)
                        .collect(Collectors.toSet()));
        UserEntity saved = repository.save(entity);
        return mapUser(saved);
    }

    @Override
    public Optional<User> findById(long id) {
        return repository.findById(id)
                .map(UserJdbcRepositoryAdapter::mapUser);
    }

    @Override
    public Page<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username"));
        var aPage = repository.findAllByUsernameNotIn(List.of("admin"), pageable);
        return new DefaultPage<>(aPage.map(UserJdbcRepositoryAdapter::mapUser));
    }

    private static User mapUser(UserEntity it) {
        return new User(it.id(),
                it.version(),
                it.username(),
                it.password(),
                it.enabled(),
                new TreeSet<>(it.authorities().stream()
                        .map(UserAuthorityEntity::name)
                        .toList()));
    }

}
