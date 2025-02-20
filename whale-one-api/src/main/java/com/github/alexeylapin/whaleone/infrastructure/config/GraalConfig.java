package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.infrastructure.security.UserDetailsIdUser;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(GraalConfig.Hints.class)
@Configuration(proxyBeanMethods = false)
public class GraalConfig {

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.serialization().registerType(UserDetailsIdUser.class);
        }

    }

}
