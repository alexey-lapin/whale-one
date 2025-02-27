package com.github.alexeylapin.whaleone.infrastructure.web;

import com.github.alexeylapin.whaleone.domain.model.User;
import com.github.alexeylapin.whaleone.domain.repo.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@RegisterReflectionForBinding({UserController.NewUser.class, UserController.UpdateUser.class})
@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String users(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size,
                        Model model) {
        model.addAttribute("users", userRepository.findAll(page, size));
        return "pages/users";
    }

    @GetMapping("/users/new")
    public String userNew() {
        return "pages/user-new";
    }

    @PostMapping("/users/new")
    public String userNewSubmit(@ModelAttribute NewUser formUser) {
        if (!formUser.password().equals(formUser.confirmPassword())) {
            throw new IllegalArgumentException("passwords do not match");
        }
        userRepository.save(User.builder()
                .version(0)
                .username(formUser.username())
                .password(passwordEncoder.encode(formUser.password()))
                .enabled(formUser.enabled())
                .authorities(Set.of("USER"))
                .build());
        return "redirect:/users";
    }

    @GetMapping("/users/{id}")
    public String userView(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "pages/user-update";
    }

    @PostMapping("/users/{id}")
    public String userUpdateSubmit(@PathVariable("id") long id, @ModelAttribute UpdateUser formUser) {
        if (!Objects.equals(formUser.password(), formUser.confirmPassword())) {
            throw new IllegalArgumentException("passwords do not match");
        }
        User user = userRepository.findById(id).orElseThrow();
        String password;
        if (formUser.password() == null) {
            password = user.password();
        } else {
            password = passwordEncoder.encode(formUser.password());
        }
        userRepository.save(user.toBuilder()
                .version(user.version())
                .username(formUser.username())
                .password(password)
                .enabled(formUser.enabled())
                .build());
        return "redirect:/users";
    }

    public record NewUser(
            int version,
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String confirmPassword,
            boolean enabled
    ) {
    }

    public record UpdateUser(
            int version,
            @NotBlank String username,
            String password,
            String confirmPassword,
            boolean enabled
    ) {
    }

}
