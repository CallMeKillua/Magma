package com.meti.compile;

import com.meti.collect.StreamException;
import com.meti.compile.attribute.Attribute;
import com.meti.compile.common.Line;
import com.meti.compile.common.block.Block;
import com.meti.compile.node.Node;
import com.meti.option.None;
import com.meti.option.Option;
import com.meti.option.Some;

public record BlockTransformer(Node root) implements Transformer {
    @Override
    public Option<Node> transform() throws CompileException {
        if (root.is(Node.Type.Block)) {
            return new Some<>(transformBlock(root));
        } else {
            return new None<>();
        }
    }

    private static Node transformBlock(Node root) throws CompileException {
        try {
            return root.apply(Attribute.Type.Children)
                    .asStreamOfNodes1()
                    .map(BlockTransformer::transformChild)
                    .foldRight(new Cache.Prototype<>(new Block.Builder()), Cache.Prototype::merge)
                    .toCache();
        } catch (StreamException e) {
            throw new CompileException(e);
        }
    }

    private static Cache.Prototype<Block.Builder> transformChild(Node oldChild) {
        if (!oldChild.is(Node.Type.Implementation)) {
            var newChild = shouldBeAsLine(oldChild) ? new Line(oldChild) : oldChild;
            return new Cache.Prototype<>(new Block.Builder().add(newChild));
        }
        return new Cache.Prototype<>(new Block.Builder(), oldChild);
    }

    private static boolean shouldBeAsLine(Node child) {
        return child.is(Node.Type.Binary) || child.is(Node.Type.Invocation);
    }
}
