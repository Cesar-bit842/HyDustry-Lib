package Hytale.Modification.hydustrylib.Components;

import javax.annotation.Nullable;
  
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType; 
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import Hytale.Modification.hydustrylib.HyDustryLib;

public class HarvestingEngineComponent implements Component<ChunkStore> {
    public long Fase = 0;
    public long Range = 0;
    public long Interval = 0; 
    public int DecayWatering = 0; 
    public float DecayAccelerate = 1f;
    public boolean Water = false;
    public boolean Harvest = false;
    public boolean Fertilize = false;
    public boolean Accelerate = false;
    public String LastActionTime = "1970-01-01 00:00:00";
    
    public static final BuilderCodec<HarvestingEngineComponent> CODEC =
        BuilderCodec.builder(HarvestingEngineComponent.class, HarvestingEngineComponent::new) 
            .append(new KeyedCodec<>("Fase", Codec.LONG),
                    (state, nr) -> state.Fase = nr,
                    s -> s.Fase)
            .add() 
            .append(new KeyedCodec<>("Range", Codec.LONG),
                    (state, i) -> state.Range = i,
                    s -> s.Range)
            .add()
            .append(new KeyedCodec<>("Interval", Codec.LONG),
                    (state, i) -> state.Interval = i,
                    s -> s.Interval)
            .add() 
            .append(new KeyedCodec<>("DecayWatering", Codec.INTEGER),
                    (state, i) -> state.DecayWatering = i,
                    s -> s.DecayWatering)
            .add() 
            .append(new KeyedCodec<>("DecayAccelerate", Codec.FLOAT),
                    (state, f) -> state.DecayAccelerate = f,
                    s -> s.DecayAccelerate)
            .add()  
            .append(new KeyedCodec<>("Water", Codec.BOOLEAN),
                    (state, f) -> state.Water = f,
                    s -> s.Water)
            .add()  
            .append(new KeyedCodec<>("Harvest", Codec.BOOLEAN),
                    (state, f) -> state.Harvest = f,
                    s -> s.Harvest)
            .add()  
            .append(new KeyedCodec<>("Fertilize", Codec.BOOLEAN),
                    (state, f) -> state.Fertilize = f,
                    s -> s.Fertilize)
            .add()  
            .append(new KeyedCodec<>("Accelerate", Codec.BOOLEAN),
                    (state, f) -> state.Accelerate = f,
                    s -> s.Accelerate)
            .add()  
            .append(new KeyedCodec<>("LastActionTime", Codec.STRING),
                    (state, time) -> state.LastActionTime = time,
                    s -> s.LastActionTime)
            .add()
            .build();
            
    public HarvestingEngineComponent() { }

    public static ComponentType getComponentType() {
        return HyDustryLib.get().getHarvestingEngineComponentType();
    } 
    
    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        HarvestingEngineComponent copy = new HarvestingEngineComponent(); 
        copy.Fase = this.Fase;
        copy.Range = this.Range;
        copy.Interval = this.Interval;
        copy.DecayWatering = this.DecayWatering;
        copy.DecayAccelerate = this.DecayAccelerate;
        copy.Water = this.Water;
        copy.Harvest = this.Harvest;
        copy.Fertilize = this.Fertilize;
        copy.Accelerate = this.Accelerate;
        copy.LastActionTime = this.LastActionTime;
        
        return copy;
    }
}