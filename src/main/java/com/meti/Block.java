package com.meti;

import java.util.Collection;
import java.util.stream.Stream;

public record Block(Collection<Node> children) implements Node {
    @Override
    public String render() {
        var builder = new StringBuilder();
        for (Node child : children) {
            builder.append(child.render());
        }

        return "{" + builder + "}";
    }

    @Override
    public Stream<Node> streamNodes() {
        return children.stream();
    }

    @Override
    public Node withNodes(Collection<Node> nodes) {
        return new Block(nodes);
    }
}
