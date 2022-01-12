package com.meti.compile.clang;

import com.meti.compile.CompileException;
import com.meti.compile.attribute.Attribute;
import com.meti.compile.attribute.NodeAttribute;
import com.meti.compile.attribute.NodesAttribute;
import com.meti.compile.common.EmptyField;
import com.meti.compile.common.LineRenderer;
import com.meti.compile.common.block.BlockRenderer;
import com.meti.compile.common.condition.ConditionRenderer;
import com.meti.compile.common.integer.IntegerRenderer;
import com.meti.compile.common.invoke.InvocationRenderer;
import com.meti.compile.common.returns.ReturnRenderer;
import com.meti.compile.common.string.StringRenderer;
import com.meti.compile.common.variable.VariableRenderer;
import com.meti.compile.node.Content;
import com.meti.compile.node.Node;
import com.meti.compile.node.Text;
import com.meti.compile.render.EmptyRenderer;
import com.meti.compile.render.RenderException;
import com.meti.compile.render.Renderer;
import com.meti.option.None;
import com.meti.option.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record CRenderer(Node root) {
    private static Text renderField(Node node) throws CompileException {
        var name = node.apply(Attribute.Type.Name).asText();
        var type = node.apply(Attribute.Type.Type).asNode();

        if (type.is(Node.Type.Reference)) {
            var child = type.apply(Attribute.Type.Value).asNode();
            return renderFieldWithType(new EmptyField(Collections.emptySet(), name.prepend("*"), child));
        } else if (type.is(Node.Type.Primitive)) {
            var rendered = type.apply(Attribute.Type.Name)
                    .asText().computeTrimmed()
                    .toLowerCase();
            return name.prepend(rendered + " ");
        } else if (type.is(Node.Type.Integer)) {
            var isSigned = type.apply(Attribute.Type.Sign).asBoolean();
            var bits = type.apply(Attribute.Type.Bits).asInteger();
            var suffix = switch (bits) {
                case 8 -> "char";
                case 16 -> "int";
                default -> throw new RenderException("Unknown bit quantity: " + bits);
            };
            var value = (isSigned ? "" : "unsigned ") + suffix + " " + name.computeTrimmed();
            return new Text(value);
        } else {
            throw new RenderException("Cannot render type: " + type);
        }
    }

    private static Text renderFieldWithType(Node node) throws CompileException {
        var common = renderField(node);
        if (node.is(Node.Type.EmptyField)) {
            return common;
        } else {
            var value = node.apply(Attribute.Type.Value).asNode();
            var valueText = renderNode(value);
            return common.append("=").append(valueText);
        }
    }

    static Text renderNode(Node node) throws CompileException {
        if (node.is(Node.Type.Content)) {
            return node.apply(Attribute.Type.Value).asText();
        }

        var renderers = List.of(
                new BlockRenderer(node),
                new ConditionRenderer(node),
                new DeclarationRenderer(node),
                new EmptyRenderer(node),
                new ExternRenderer(node),
                new FunctionRenderer(node),
                new ImportRenderer(node),
                new IntegerRenderer(node),
                new InvocationRenderer(node),
                new LineRenderer(node),
                new ReturnRenderer(node),
                new StringRenderer(node),
                new StructureRenderer(node),
                new UnaryRenderer(node),
                new VariableRenderer(node));

        Option<Text> current = new None<>();
        for (Renderer renderer : renderers) {
            var result = renderer.render();
            if (result.isPresent()) {
                current = result;
            }
        }

        return current.orElseThrow(() -> new CompileException("Unable to render node: " + node));
    }

    public static Node renderSubFields(Node root) throws CompileException {
        var types = root.apply(Attribute.Group.Declaration).collect(Collectors.toList());
        var current = root;
        for (Attribute.Type type : types) {
            var node = root.apply(type).asNode();
            var renderedNode = renderFieldWithType(node);
            current = current.with(type, new NodeAttribute(new Content(renderedNode)));
        }
        return current;
    }

    public static Node renderSubNodes(Node root) throws CompileException {
        var types = root.apply(Attribute.Group.Node).collect(Collectors.toList());
        var current = root;
        for (Attribute.Type type : types) {
            var node = root.apply(type).asNode();
            var renderedNode = new CRenderer(node).render();
            current = current.with(type, new NodeAttribute(new Content(renderedNode)));
        }
        return current;
    }

    public Text render() throws CompileException {
        return renderAST(root);
    }

    private Text renderAST(Node root) throws CompileException {
        var withFields = renderSubFields(root);
        var withNodes = renderSubNodes(withFields);
        var withNodeCollections = renderSubNodeCollections(withNodes);
        var current = withDeclarationCollections(withNodeCollections);
        return renderNode(current);
    }

    private Node renderSubNodeCollections(Node withNodes) throws CompileException {
        var types = withNodes.apply(Attribute.Group.Nodes).collect(Collectors.toList());
        var current = withNodes;
        for (Attribute.Type type : types) {
            var oldNodes = withNodes.apply(type).asStreamOfNodes().collect(Collectors.toList());
            var newNodes = new ArrayList<Node>();
            for (Node oldNode : oldNodes) {
                newNodes.add(new Content(renderAST(oldNode)));
            }
            current = current.with(type, new NodesAttribute(newNodes));
        }
        return current;
    }

    private Node withDeclarationCollections(Node withNodeCollections) throws CompileException {
        var types = withNodeCollections.apply(Attribute.Group.Declarations).collect(Collectors.toList());
        var current = withNodeCollections;
        for (Attribute.Type type : types) {
            var oldDeclarations = current.apply(type).asStreamOfNodes().collect(Collectors.toList());
            var newDeclarations = new ArrayList<Node>();
            for (Node oldDeclaration : oldDeclarations) {
                newDeclarations.add(new Content(renderFieldWithType(oldDeclaration)));
            }
            current = current.with(type, new NodesAttribute(newDeclarations));
        }
        return current;
    }
}
