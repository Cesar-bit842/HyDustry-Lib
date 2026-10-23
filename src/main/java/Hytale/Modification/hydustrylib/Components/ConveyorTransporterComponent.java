package Hytale.Modification.hydustrylib.Components;

import javax.annotation.Nullable;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec; 
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import Hytale.Modification.hydustrylib.HyDustryLib; 

public class ConveyorTransporterComponent implements Component<ChunkStore> {
    public float Velocity = 1;
    public float Progress = 0; 
    public boolean ItemsDetected = false;
    public boolean LookingBehaviour = false;
    public static final BuilderCodec<ConveyorTransporterComponent> CODEC =
        BuilderCodec.builder(ConveyorTransporterComponent.class, ConveyorTransporterComponent::new) 
            .append(new KeyedCodec<>("Velocity", Codec.FLOAT),
                    (state, nr) -> state.Velocity = nr,
                    s -> s.Velocity)
            .add()  
            .append(new KeyedCodec<>("Progress (Only Tags)", Codec.FLOAT),
                    (state, nr) -> state.Progress = nr,
                    s -> s.Progress)
            .add()  
            .append(new KeyedCodec<>("ItemsDetected (Only Tags)", Codec.BOOLEAN),
                    (state, nr) -> state.ItemsDetected = nr,
                    s -> s.ItemsDetected)
            .add()  
            .append(new KeyedCodec<>("Looking Behaviour (Only Tags)", Codec.BOOLEAN),
                    (state, nr) -> state.LookingBehaviour = nr,
                    s -> s.LookingBehaviour)
            .add()  
            .build();
            
    public ConveyorTransporterComponent() { }
    public static ComponentType getComponentType() {
        return HyDustryLib.get().getConveyorTransporterComponentType();
    } 
    
    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        ConveyorTransporterComponent copy = new ConveyorTransporterComponent(); 
        copy.Velocity = this.Velocity; 
        copy.Progress = this.Progress; 
        copy.ItemsDetected = this.ItemsDetected; 
        copy.LookingBehaviour = this.LookingBehaviour;
        return copy;
    }
}
