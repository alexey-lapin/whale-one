package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import com.github.alexeylapin.whaleone.application.service.QuerySpecFactory;
import com.github.alexeylapin.whaleone.domain.DefaultQuerySpec;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RsqlQuerySpecFactory implements QuerySpecFactory {

    private final RSQLParser parser;
    private final ConversionService conversionService;
    private final Map<String, FieldDescriptor> descriptors;

    public RsqlQuerySpecFactory(ConversionService conversionService, Set<FieldDescriptor> descriptors) {
        this.conversionService = conversionService;
        var comparisonOperators = RSQLOperators.defaultOperators();
        comparisonOperators.add(new ComparisonOperator("=ilike="));
        this.parser = new RSQLParser(comparisonOperators);
        this.descriptors = descriptors.stream()
                .collect(Collectors.toMap(FieldDescriptor::name, Function.identity()));
    }

    @Override
    public QuerySpec  create(String string) {
        if (string == null || string.isEmpty()) {
            return new DefaultQuerySpec("", List.of());
        }
        var node = parser.parse(string);
        var visitor = new RsqlJdbcVisitor(conversionService, descriptors);
        var params = new ArrayList<>();
        var sql = "WHERE " + node.accept(visitor, params);
        return new DefaultQuerySpec(sql, params);
    }

}
