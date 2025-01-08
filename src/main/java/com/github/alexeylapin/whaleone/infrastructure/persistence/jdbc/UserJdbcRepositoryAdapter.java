package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.model.Page;
import com.github.alexeylapin.whaleone.domain.model.User;
import com.github.alexeylapin.whaleone.domain.repo.UserRepository;
import org.springframework.data.domain.PageRequest;
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
    public Optional<User> findById(long id) {
        return repository.findById(id)
                .map(UserJdbcRepositoryAdapter::mapUser);
    }

    @Override
    public Page<User> findAll(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("username"));
        org.springframework.data.domain.Page<JdbcUserEntity> aPage =
                repository.findAllByUsernameNotIn(List.of("admin"), pageable);
        List<User> items = aPage.stream()
                .map(UserJdbcRepositoryAdapter::mapUser)
                .toList();
        return new Page<>(items, aPage.getTotalPages(), aPage.getNumber());
    }

    @Override
    public User save(User user) {
        JdbcUserEntity entity = new JdbcUserEntity(user.id(),
                user.version(),
                user.username(),
                user.password(),
                user.enabled(),
                user.authorities().stream()
                        .map(JdbcUserAuthorityEntity::new)
                        .collect(Collectors.toSet()));
        JdbcUserEntity saved = repository.save(entity);
        return mapUser(saved);
    }

    private static User mapUser(JdbcUserEntity it) {
        return new User(it.id(),
                it.version(),
                it.username(),
                it.password(),
                it.enabled(),
                new TreeSet<>(it.authorities().stream()
                        .map(JdbcUserAuthorityEntity::name)
                        .toList()));
    }

}
