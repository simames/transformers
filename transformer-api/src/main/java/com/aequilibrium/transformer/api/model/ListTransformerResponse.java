package com.aequilibrium.transformer.api.model;

import java.io.Serializable;
import java.util.List;

public class ListTransformerResponse  implements Serializable {
    private List<Transformer> transformers;

    protected ListTransformerResponse() {
    }

    public ListTransformerResponse(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    public List<Transformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }
}
