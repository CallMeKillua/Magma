package com.meti.app.clang;

import com.meti.api.ArrayStream;
import com.meti.api.Stream;
import com.meti.app.compile.AbstractRenderingStage;
import com.meti.app.compile.feature.BlockRenderer;
import com.meti.app.compile.feature.IntegerRenderer;
import com.meti.app.compile.feature.ReturnRenderer;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.output.Output;

public final class CRenderingStage extends AbstractRenderingStage {
    public CRenderingStage(Node node) {
        super(node);
    }

    @Override
    protected Stream<Processor<Output>> createProcessors() {
        return new ArrayStream<>(
                new BlockRenderer(node),
                new IntegerRenderer(node),
                new CImportRenderer(node),
                new ContentRenderer(node),
                new ReturnRenderer(node));
    }
}
