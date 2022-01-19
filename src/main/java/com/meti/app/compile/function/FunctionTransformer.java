package com.meti.app.compile.function;

import com.meti.api.collect.java.List;
import com.meti.api.collect.stream.StreamException;
import com.meti.api.option.None;
import com.meti.api.option.Option;
import com.meti.api.option.Some;
import com.meti.app.compile.cache.Cache;
import com.meti.app.compile.common.Field;
import com.meti.app.compile.common.variable.Variable;
import com.meti.app.compile.node.EmptyNode;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.attribute.Attribute;
import com.meti.app.compile.node.attribute.AttributeException;
import com.meti.app.compile.stage.CompileException;
import com.meti.app.compile.stage.Transformer;

public record FunctionTransformer(Node oldNode) implements Transformer {
    @Override
    public Option<Node> transform() throws CompileException {
        if (oldNode.is(Node.Type.Abstraction)) {
            return new Some<>(isExternal() ? EmptyNode.EmptyNode_ : oldNode);
        }
        if (oldNode.is(Node.Type.Implementation)) {
            var function = transformFunction();
            return new Some<>(function);
        }
        return new None<>();
    }

    private boolean isExternal() {
        try {
            return oldNode.apply(Attribute.Type.Identity)
                    .asNode()
                    .apply(Attribute.Type.Flags)
                    .asStreamOfFlags1()
                    .foldRight(List.createList(), List::add)
                    .contains(Field.Flag.Extern);
        } catch (StreamException | AttributeException e) {
            return false;
        }
    }

    private Node transformFunction() throws CompileException {
        try {
            var identity = oldNode.apply(Attribute.Type.Identity).asNode();
            Node function;
            if (identity.apply(Attribute.Type.Flags)
                        .asStreamOfFlags1()
                        .count() == 0) {
                var name = identity.apply(Attribute.Type.Name).asInput();
                function = new Cache(new Variable(name), oldNode);
            } else {
                function = oldNode;
            }
            return function;
        } catch (StreamException | AttributeException e) {
            throw new CompileException(e);
        }
    }
}
