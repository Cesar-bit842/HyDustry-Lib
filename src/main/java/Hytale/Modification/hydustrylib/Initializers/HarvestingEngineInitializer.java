package Hytale.Modification.hydustrylib.Initializers;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer; 
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.util.ChunkUtil; 
import com.hypixel.hytale.server.core.modules.block.BlockModule;  
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import Hytale.Modification.hydustrylib.HyDustryLib;  
import Hytale.Modification.hydustrylib.Components.HarvestingEngineComponent; 

public class HarvestingEngineInitializer extends RefSystem<ChunkStore> {

    @Override
    public void onEntityAdded(@Nonnull Ref ref, @Nonnull AddReason reason, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo) commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
        if (info != null){
            HarvestingEngineComponent HarvestingEngineComponent_ = (HarvestingEngineComponent) commandBuffer.getComponent(ref, HyDustryLib.get().getHarvestingEngineComponentType());
            if (HarvestingEngineComponent_ != null) {
                Ref<ChunkStore> chunkRef = info.getSectionRef();
                if (chunkRef.isValid()) {
                    int index = info.getIndex();
                    int x = ChunkUtil.xFromColumn(index);
                    int y = ChunkUtil.xFromColumn(index);
                    int z = ChunkUtil.xFromColumn(index);
                    BlockSection blockSection = (BlockSection)commandBuffer.getComponent(chunkRef, BlockSection.getComponentType());
                    if (blockSection != null) {
                        blockSection.setTicking(x, y, z, true);
                    }
                } 
            }
        }
        return; 
    }  

    @Override
    public void onEntityRemove(@Nonnull Ref ref, @Nonnull RemoveReason reason, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
    }

    @Override
    public Query getQuery() {
        return Query.and(BlockModule.BlockStateInfo.getComponentType(), HyDustryLib.get().getHarvestingEngineComponentType());
    }
}