package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.domain.model.User;
import com.github.alexeylapin.whaleone.domain.model.UserListElement;
import com.github.alexeylapin.whaleone.domain.model.UserRef;
import com.github.alexeylapin.whaleone.domain.repo.Page;
import com.github.alexeylapin.whaleone.domain.repo.UserRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserApi {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoMapper mapper;

    @PostMapping("/users")
    public UserInfo create(@RequestBody User userModel,
                           @AuthenticationPrincipal IdUser user) {
        ZonedDateTime now = ZonedDateTime.now();
        UserRef userRef = new UserRef(user.getId(), user.getName());
        userModel = userModel.toBuilder()
                .id(0)
                .version(0)
                .createdAt(now)
                .createdBy(userRef)
                .lastUpdatedAt(now)
                .lastUpdatedBy(userRef)
                .password(passwordEncoder.encode(userModel.password()))
                .build();
        return mapper.map(userRepository.save(userModel));
    }

    @PutMapping("/users/{id}")
    public UserInfo update(@PathVariable long id,
                           @RequestBody UserInfo userModel,
                           @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing user expected");
        Assert.isTrue(userModel.version() > 0,
                "user.version must be greater than 0 - existing user expected");
        Assert.isTrue(id == userModel.id(),
                "id must match");
        User alteredUser = userRepository.findById(id).orElseThrow().toBuilder()
                .id(id)
                .version(userModel.version())
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .username(userModel.username())
                .enabled(userModel.enabled())
                .build();
        return mapper.map(userRepository.save(alteredUser));
    }

    @PutMapping("/users/{id}/password")
    public UserInfo updatePassword(@PathVariable long id,
                                   @RequestBody UserPassword userPassword,
                                   @AuthenticationPrincipal IdUser user) {
        Assert.isTrue(id > 0,
                "id must be greater than 0 - existing user expected");
        User alteredUser = userRepository.findById(id).orElseThrow().toBuilder()
                .id(id)
                .version(userPassword.version())
                .lastUpdatedAt(ZonedDateTime.now())
                .lastUpdatedBy(new UserRef(user.getId(), user.getName()))
                .password(passwordEncoder.encode(userPassword.password()))
                .build();
        return mapper.map(userRepository.save(alteredUser));
    }

    @GetMapping("/users/{id}")
    public UserInfo get(@PathVariable long id) {
        return mapper.map(userRepository.findById(id).orElseThrow());
    }

    @GetMapping("/users")
    public PageDto<UserListElement> getAll(@RequestParam int page,
                                           @RequestParam int size,
                                           @RequestParam Optional<String> name,
                                           @RequestParam Optional<String> client,
                                           @RequestParam Optional<String> ownership,
                                           @RequestParam Optional<String> region,
                                           @RequestParam Optional<String> type) {
        Page<UserListElement> aPage = userRepository.findAll(page, size);
        return new PageDto<>(aPage.getContent(), aPage.getNumber(), aPage.getSize(), aPage.getTotalElements());
    }

    public record UserInfo(
            long id,
            int version,
            ZonedDateTime createdAt,
            UserRef createdBy,
            ZonedDateTime lastUpdatedAt,
            UserRef lastUpdatedBy,
            String username,
            boolean enabled,
            Set<String> authorities
    ) {
    }

    public record UserPassword(
            int version,
            String password
    ) {
    }

    @Mapper(config = MappingConfig.class)
    interface UserInfoMapper {

        UserInfo map(User user);

        User map(UserInfo userInfo);

    }

}
