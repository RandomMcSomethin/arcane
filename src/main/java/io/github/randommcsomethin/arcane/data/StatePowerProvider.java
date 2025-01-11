package io.github.randommcsomethin.arcane.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public class StatePowerProvider {
    // codec
    public static final MapCodec<StatePowerProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.fieldOf("amount").forGetter(StatePowerProvider::getAmount),
        StatePredicate.CODEC.listOf().fieldOf("states").forGetter(StatePowerProvider::getStates)
    ).apply(instance, StatePowerProvider::new));

    private final float amount;
    private final List<StatePredicate> states;

    public StatePowerProvider(float amount, List<StatePredicate> states) {
        this.amount = amount;
        this.states = states;
    }

    public float getAmount() { return this.amount; }
    public List<StatePredicate> getStates() { return this.states; }
}
