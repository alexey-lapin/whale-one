package com.github.alexeylapin.whaleone.domain;

import java.util.List;

public record DefaultQuerySpec(String spec, List<?> params) implements QuerySpec {
}
