package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RsqlJdbcVisitor implements RSQLVisitor<String, List<Object>> {

    private final ConversionService conversionService;
    private final Map<String, FieldDescriptor> descriptors;

    @Deprecated
    public RsqlJdbcVisitor(ConversionService conversionService, Map<String, FieldDescriptor> descriptors) {
        this.conversionService = conversionService;
        this.descriptors = descriptors;
    }

    public RsqlJdbcVisitor(ConversionService conversionService, Set<FieldDescriptor> descriptors) {
        this(conversionService, descriptors.stream()
                .collect(Collectors.toMap(FieldDescriptor::name, Function.identity())));
    }

    @Override
    public String visit(AndNode node, List<Object> param) {
        return node.getChildren().stream()
                .map(n -> n.accept(this, param))
                .collect(Collectors.joining(" AND "));
    }

    @Override
    public String visit(OrNode node, List<Object> param) {
        return node.getChildren().stream()
                .map(n -> n.accept(this, param))
                .collect(Collectors.joining(" OR "));
    }

    @Override
    public String visit(ComparisonNode node, List<Object> param) {
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
                param.add(convertArgument(arguments.getFirst(), descriptor));
                return field + " = ?";
            case "!=":
                param.add(convertArgument(arguments.getFirst(), descriptor));
                return field + " <> ?";
            case ">":
                param.add(convertArgument(arguments.getFirst(), descriptor));
                return field + " > ?";
            case "<":
                param.add(convertArgument(arguments.getFirst(), descriptor));
                return field + " < ?";
            case "=in=":
                arguments.stream()
                        .map(a -> convertArgument(a, descriptor))
                        .forEach(param::add);
                var placeholders = arguments.stream()
                        .map((i) -> "?")
                        .collect(Collectors.joining(","));
                return field + " IN (%s)".formatted(placeholders);
            case "=ilike=":
                param.add("%" + arguments.getFirst().toLowerCase() + "%");
                return "LOWER(" + field + ") LIKE ?";
            default:
                throw new UnsupportedOperationException("Operator not supported: " + operator);
        }
    }

    private Object convertArgument(String argument, FieldDescriptor descriptor) {
        return conversionService.convert(argument, descriptor.type());
    }

}
