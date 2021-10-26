package com.meti.clang;

import com.meti.compile.AbstractRenderingStage;
import com.meti.compile.Node;
import com.meti.compile.feature.IntegerRenderer;
import com.meti.compile.feature.ReturnRenderer;
import com.meti.compile.output.Output;

import java.util.stream.Stream;

public final class CRenderingStage extends AbstractRenderingStage {
    public CRenderingStage(Node node) {
        super(node);
    }

    @Override
    protected Stream<Processor<Output>> createProcessors() {
        return Stream.of(
                new CImportRenderer(node),
                new ReturnRenderer(node),
                new IntegerRenderer(node));
    }
}
