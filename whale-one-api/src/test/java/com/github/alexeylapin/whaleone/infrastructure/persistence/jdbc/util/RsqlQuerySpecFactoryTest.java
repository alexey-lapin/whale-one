package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import com.github.alexeylapin.whaleone.domain.QuerySpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RsqlQuerySpecFactoryTest {

    private static RsqlQuerySpecFactory factory;

    @BeforeAll
    static void setup() {
        var conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), eq(String.class)))
                .then(invocation -> invocation.getArgument(0));
        when(conversionService.convert(any(), eq(Integer.class)))
                .then(invocation -> Integer.parseInt(invocation.getArgument(0).toString()));
        factory = new RsqlQuerySpecFactory(conversionService, Set.of(
                new FilterFieldDescriptor("id", Integer.class, "e", "id"),
                new FilterFieldDescriptor("name", String.class, "e", "name")
        ));
    }

    @Test
    public void test1() {
        QuerySpec spec = factory.create("id==1");

        assertThat(spec.spec()).isEqualTo("WHERE e.id = ?");
        assertThat(spec.params()).extracting(Object::toString).containsExactly("1");
    }

    @Test
    public void test11() {
        QuerySpec spec = factory.create("id==1");

        assertThat(spec.spec()).isEqualTo("WHERE e.id = ?");
        assertThat(spec.params()).extracting(Object::toString).containsExactly("1");
    }

    @Test
    public void test2() {
        QuerySpec spec = factory.create("id==1;name=in=(a,b,c)");

        assertThat(spec.spec()).isEqualTo("WHERE e.id = ? AND e.name IN (?,?,?)");
        assertThat(spec.params()).extracting(Object::toString).containsExactly("1", "a", "b", "c");
    }

    @Test
    public void test3() {
        QuerySpec spec = factory.create("id==1;name=ilike=aSdF");

        assertThat(spec.spec()).isEqualTo("WHERE e.id = ? AND LOWER(e.name) LIKE ?");
        assertThat(spec.params()).extracting(Object::toString).containsExactly("1", "%asdf%");
    }

}