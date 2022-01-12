package com.meti.compile;

import com.meti.compile.attribute.*;
import com.meti.compile.clang.CFormat;
import com.meti.compile.clang.CRenderer;
import com.meti.compile.common.Field;
import com.meti.compile.common.Import;
import com.meti.compile.common.Line;
import com.meti.compile.common.block.Splitter;
import com.meti.compile.common.integer.IntegerNode;
import com.meti.compile.common.integer.IntegerType;
import com.meti.compile.magma.MagmaLexer;
import com.meti.compile.node.*;
import com.meti.option.None;
import com.meti.option.Option;
import com.meti.option.Some;
import com.meti.source.Packaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MagmaCCompiler(Map<Packaging, String> inputMap) {
    public Map<Packaging, Output<String>> compile() throws CompileException {
        var outputMap = new HashMap<Packaging, Output<String>>();
        for (Packaging aPackaging : inputMap.keySet()) {
            var input = compileInput(aPackaging, inputMap.get(aPackaging));
            outputMap.put(aPackaging, input);
        }
        return outputMap;
    }

    private Output<String> compileInput(Packaging thisPackage, String input) throws CompileException {
        if (input.isBlank()) return new EmptyOutput<>();

        var root = new Text(input);
        var lines = split(root);
        var lexed = lex(lines);
        var resolved = resolveStage(lexed);
        var formatted = format(thisPackage, resolved);
        var divided = divide(thisPackage, formatted);
        return renderMap(divided);
    }

    private Map<CFormat, List<Node>> divide(Packaging thisPackage, ArrayList<Node> newNodes) {
        var map = new HashMap<CFormat, List<Node>>();
        for (Node node : newNodes) {
            if (node.is(Node.Type.Import) || node.is(Node.Type.Extern) || node.is(Node.Type.Structure)) {
                List<Node> list;
                if (!map.containsKey(CFormat.Header)) {
                    list = new ArrayList<>();
                    var header = thisPackage.formatDeclared();
                    list.add(new Content(new Text("\n#ifndef " + header + "\n")));
                    list.add(new Content(new Text("\n#define " + header + "\n")));
                    map.put(CFormat.Header, list);
                } else {
                    list = map.get(CFormat.Header);
                }
                list.add(node);
            } else {
                List<Node> list;
                if (!map.containsKey(CFormat.Source)) {
                    list = new ArrayList<>();
                    list.add(new Import(new Packaging(thisPackage.computeName())));
                    map.put(CFormat.Source, list);
                } else {
                    list = map.get(CFormat.Source);
                }
                list.add(node);
            }
        }

        if (map.containsKey(CFormat.Header)) {
            map.get(CFormat.Header).add(new Content(new Text("\n#endif\n")));
        }
        return map;
    }

    private Option<Node> fixAbstraction(Node node) throws AttributeException {
        if (node.is(Node.Type.Abstraction)) {
            if (node.apply(Attribute.Type.Identity)
                    .asNode()
                    .apply(Attribute.Type.Flags)
                    .asStreamOfFlags()
                    .noneMatch(value -> value == Field.Flag.Extern)) {
                return new Some<>(fixFunction(node));
            } else {
                return new Some<>(new EmptyNode());
            }
        } else {
            return new None<>();
        }
    }

    private Option<Node> fixBlock(Node node) throws AttributeException {
        if (node.is(Node.Type.Block)) {
            var oldChildren = node.apply(Attribute.Type.Children)
                    .asStreamOfNodes()
                    .collect(Collectors.toList());
            var newChildren = new ArrayList<Node>();
            for (Node oldChild : oldChildren) {
                Node newChild;
                if (oldChild.is(Node.Type.Invocation)) {
                    newChild = new Line(oldChild);
                } else {
                    newChild = oldChild;
                }
                newChildren.add(newChild);
            }
            return new Some<>(node.with(Attribute.Type.Children, new NodesAttribute(newChildren)));
        } else {
            return new None<>();
        }
    }

    private Node fixFunction(Node node) throws AttributeException {
        var identity = node.apply(Attribute.Type.Identity).asNode();
        var oldReturnType = identity.apply(Attribute.Type.Type).asNode();
        Node newReturnType;
        if (oldReturnType.is(Node.Type.Implicit)) {
            var value = node.apply(Attribute.Type.Value).asNode();
            newReturnType = resolveNode(value);
        } else {
            newReturnType = oldReturnType;
        }
        var newIdentity = identity.with(Attribute.Type.Type, new NodeAttribute(newReturnType));
        var withIdentity = node.with(Attribute.Type.Identity, new NodeAttribute(newIdentity));

        var oldParameters = withIdentity.apply(Attribute.Type.Parameters)
                .asStreamOfNodes()
                .collect(Collectors.toList());
        var newParameters = new ArrayList<Node>();
        for (Node parameter : oldParameters) {
            var oldType = parameter.apply(Attribute.Type.Type).asNode();
            if (oldType.is(Node.Type.Primitive) && oldType.apply(Attribute.Type.Name).asText().compute().equals("Bool")) {
                var newType = new IntegerType(true, 16);
                newParameters.add(parameter.with(Attribute.Type.Type, new NodeAttribute(newType)));
            } else {
                newParameters.add(parameter);
            }
        }
        return withIdentity.with(Attribute.Type.Parameters, new NodesAttribute(newParameters));
    }

    private Option<Node> fixImplementation(Node node) throws AttributeException {
        return node.is(Node.Type.Implementation)
                ? new Some<>(fixFunction(node))
                : new None<>();
    }

    private ArrayList<Node> format(Packaging thisPackage, ArrayList<Node> oldNodes) throws AttributeException {
        var newNodes = new ArrayList<Node>();
        for (Node node : oldNodes) {
            if (node.is(Node.Type.Import)) {
                var thatPackage = node.apply(Attribute.Type.Value).asPackaging();
                var newPackage = thisPackage.relativize(thatPackage);
                var withPackage = node.with(Attribute.Type.Value, new PackageAttribute(newPackage));
                newNodes.add(withPackage);
            } else {
                newNodes.add(node);
            }
        }
        return newNodes;
    }

    private ArrayList<Node> lex(List<Text> lines) throws CompileException {
        var oldNodes = new ArrayList<Node>();
        for (Text oldLine : lines) {
            oldNodes.add(new MagmaLexer(oldLine).lex());
        }
        return oldNodes;
    }

    private Output<String> renderMap(Map<CFormat, List<Node>> map) throws CompileException {
        var output = new MappedOutput<>(map);
        return output.map((format, list) -> {
            var builder = new StringBuilder();
            for (Node line : list) {
                builder.append(new CRenderer(line).render().compute());
            }
            return builder.toString().trim();
        });
    }

    private Node resolveNode(Node value) throws AttributeException {
        if (value.is(Node.Type.Block)) {
            var children = value.apply(Attribute.Type.Children)
                    .asStreamOfNodes()
                    .collect(Collectors.toList());
            return Primitive.Void;
        } else {
            return value;
        }
    }

    private ArrayList<Node> resolveStage(ArrayList<Node> lexed) throws AttributeException {
        var resolved = new ArrayList<Node>();
        for (Node node : lexed) {
            resolved.add(resolveStage(node));
        }
        return resolved;
    }

    private Node resolveStage(Node node) throws AttributeException {
        var resolved = toBoolean(node)
                .or(() -> fixImplementation(node))
                .or(() -> fixAbstraction(node))
                .or(() -> fixBlock(node))
                .orElse(node);
        var list = resolved.apply(Attribute.Group.Node).collect(Collectors.toList());
        var current = resolved;
        for (Attribute.Type type : list) {
            var child = current.apply(type).asNode();
            var resolvedChild = resolveStage(child);
            current = current.with(type, new NodeAttribute(resolvedChild));
        }
        return current;
    }

    private List<Text> split(Text root) {
        return new Splitter(root)
                .split()
                .collect(Collectors.toList());
    }

    private Option<Node> toBoolean(Node node) throws AttributeException {
        if (node.is(Node.Type.Boolean)) {
            return new Some<>(new IntegerNode(node.apply(Attribute.Type.Value).asBoolean() ? 1 : 0));
        }
        return new None<>();
    }
}
