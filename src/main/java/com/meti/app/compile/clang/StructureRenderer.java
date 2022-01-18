package com.meti.app.compile.clang;

import com.meti.api.option.None;
import com.meti.api.option.Option;
import com.meti.api.option.Some;
import com.meti.app.compile.attribute.Attribute;
import com.meti.app.compile.attribute.AttributeException;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.Text;
import com.meti.app.compile.render.Renderer;

import java.util.stream.Collectors;

record StructureRenderer(Node node) implements Renderer {
    @Override
    public Option<Text> render() throws AttributeException {
        if (node.is(Node.Type.Structure)) {
            var name = node.apply(Attribute.Type.Name).asText();
            var fields = node.apply(Attribute.Type.Fields).asStreamOfNodes().collect(Collectors.toList());
            var builder = new StringBuilder();
            for (Node field : fields) {
                builder.append(field.apply(Attribute.Type.Value).asText().computeTrimmed()).append(";");
            }
            return new Some<>(name.prepend("struct ")
                    .append("{")
                    .append(builder.toString())
                    .append("}")
                    .append(";"));
        }
        return new None<>();
    }
}