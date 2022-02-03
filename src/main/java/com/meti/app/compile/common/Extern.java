package com.meti.app.compile.common;

import com.meti.api.collect.java.List;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.attribute.Attribute;
import com.meti.app.compile.node.attribute.AttributeException;
import com.meti.app.compile.node.attribute.InputAttribute;
import com.meti.app.compile.text.RootText;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Extern(RootText root) implements Node {
    @Override
    public Attribute apply(Attribute.Category category) throws AttributeException {
        if (category == Attribute.Category.Value) return new InputAttribute(root);
        throw new AttributeException(category);
    }

    @Deprecated
    private Stream<Attribute.Category> apply2(Attribute.Group group) throws AttributeException {
        return Stream.empty();
    }

    @Override
    public com.meti.api.collect.stream.Stream<Attribute.Category> apply(Attribute.Group group) throws AttributeException {
        return List.createList(apply2(group).collect(Collectors.toList())).stream();
    }

    @Override
    public boolean is(Category category) {
        return category == Node.Category.Extern;
    }
}
