package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import com.github.alexeylapin.whaleone.application.service.QuerySpecFactory;
import com.github.alexeylapin.whaleone.domain.DefaultQuerySpec;
import com.github.alexeylapin.whaleone.domain.QuerySpec;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
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
    private final Map<String, FilterFieldDescriptor> descriptors;

    public RsqlQuerySpecFactory(ConversionService conversionService, Set<FilterFieldDescriptor> descriptors) {
        this.conversionService = conversionService;
        var comparisonOperators = RSQLOperators.defaultOperators();
        comparisonOperators.add(new ComparisonOperator("=ilike="));
        this.parser = new RSQLParser(comparisonOperators);
        this.descriptors = descriptors.stream()
                .collect(Collectors.toMap(FilterFieldDescriptor::name, Function.identity()));
    }

    @Override
    public QuerySpec create(String string) {
        if (string == null || string.isEmpty()) {
            return new DefaultQuerySpec("", List.of());
        }
        var node = parser.parse(string);
        var visitor = new JdbcRsqlVisitor(conversionService, descriptors);
        var sql = "WHERE " + node.accept(visitor);
        return new DefaultQuerySpec(sql, visitor.getParameters());
    }

    public static class JdbcRsqlVisitor implements RSQLVisitor<String, Void> {

        private final ConversionService conversionService;
        private final Map<String, FilterFieldDescriptor> descriptors;
        private final List<Object> parameters = new ArrayList<>();

        public JdbcRsqlVisitor(ConversionService conversionService, Map<String, FilterFieldDescriptor> descriptors) {
            this.conversionService = conversionService;
            this.descriptors = descriptors;
        }

        @Override
        public String visit(AndNode node, Void param) {
            return node.getChildren().stream()
                    .map(n -> n.accept(this))
                    .collect(Collectors.joining(" AND "));
        }

        @Override
        public String visit(OrNode node, Void param) {
            return node.getChildren().stream()
                    .map(n -> n.accept(this))
                    .collect(Collectors.joining(" OR "));
        }

        @Override
        public String visit(ComparisonNode node, Void param) {
            var selector = node.getSelector();
            var descriptor = descriptors.get(selector);
            if (descriptor == null) {
                throw new IllegalArgumentException("Field not allowed: " + selector);
            }

            var operator = node.getOperator().getSymbol();
            var arguments = node.getArguments();
            var field = descriptor.getField();

            switch (operator) {
                case "==":
                    parameters.add(convertArgument(arguments.getFirst(), descriptor));
                    return field + " = ?";
                case "!=":
                    parameters.add(convertArgument(arguments.getFirst(), descriptor));
                    return field + " <> ?";
                case ">":
                    parameters.add(convertArgument(arguments.getFirst(), descriptor));
                    return field + " > ?";
                case "<":
                    parameters.add(convertArgument(arguments.getFirst(), descriptor));
                    return field + " < ?";
                case "=in=":
                    arguments.stream()
                            .map(a -> convertArgument(a, descriptor))
                            .forEach(parameters::add);
                    var placeholders = arguments.stream()
                            .map((i) -> "?")
                            .collect(Collectors.joining(","));
                    return field + " IN (%s)".formatted(placeholders);
                case "=ilike=":
                    parameters.add("%" + arguments.getFirst().toLowerCase() + "%");
                    return "LOWER(" + field + ") LIKE ?";
                default:
                    throw new UnsupportedOperationException("Operator not supported: " + operator);
            }
        }

        private Object convertArgument(String argument, FilterFieldDescriptor descriptor) {
            return conversionService.convert(argument, descriptor.type());
        }

        public List<Object> getParameters() {
            return parameters;
        }

    }

}
