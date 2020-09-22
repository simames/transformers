package com.aequilibrium.transformer.api.model;

import com.aequilibrium.transformer.common.TransformerEnumType;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Validated
public class Descepticon extends Transformer {

    protected Descepticon() {
    }

    public Descepticon(@Valid @NotNull String name, @NotNull @Size(min = 1, max = 10)  Integer strength, @NotNull @Size(min = 1, max = 10)  Integer intelligence, @NotNull @Size(min = 1, max = 10)  Integer speed, @NotNull @Size(min = 1, max = 10)  Integer endurance, @NotNull @Size(min = 1, max = 10)  Integer rank, @NotNull @Size(min = 1, max = 10)  Integer courage, @NotNull @Size(min = 1, max = 10)  Integer firepower,  @NotNull @Size(min = 1, max = 10) Integer skill) {
        super(name, strength, intelligence, speed, endurance, rank, courage, firepower, skill);
        super.setType(TransformerEnumType.DESEPTICAN.getCode());
    }

}
