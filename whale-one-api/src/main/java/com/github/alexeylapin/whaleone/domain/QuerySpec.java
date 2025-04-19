package com.github.alexeylapin.whaleone.domain;

import java.util.List;

public interface QuerySpec {

    String spec();

    List<?> params();

}
