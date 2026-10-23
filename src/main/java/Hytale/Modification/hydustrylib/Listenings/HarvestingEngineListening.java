package Hytale.Modification.hydustrylib.Listenings; 

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem; 
import com.hypixel.hytale.math.util.ChunkUtil; 
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockComponentSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore; 

import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent;
import Hytale.Modification.hydustrylib.Components.HarvestingEngineComponent;
import Hytale.Modification.hydustrylib.Lib.EssentialsUtils;
import Hytale.Modification.hydustrylib.Lib.FarmingHyDustryLib; 

public class HarvestingEngineListening extends EntityTickingSystem<ChunkStore> {  
    
    public void tick(float dt, int index, @Nonnull ArchetypeChunk archetypeChunk, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) { 
        BlockSection blockSection = (BlockSection)archetypeChunk.getComponent(index, BlockSection.getComponentType());
        if (blockSection != null) {
            if (blockSection.getTickingBlocksCountCopy() != 0) {
                ChunkSection chunkSection = (ChunkSection)archetypeChunk.getComponent(index, ChunkSection.getComponentType());
                if (chunkSection != null) {
                    Ref<ChunkStore> chunkColumnRef = chunkSection.getChunkColumnReference();
                    if (chunkColumnRef != null && chunkColumnRef.isValid()) {
                        BlockComponentSection Section_ = (BlockComponentSection)archetypeChunk.getComponent(index, BlockComponentSection.getComponentType());
                        if (Section_ != null) { 
                            blockSection.forEachTicking(Section_, commandBuffer, chunkSection.getY(), this::BlockTicking);
                        } 
                    }
                } 
            }
        } 
    }
    private BlockTickStrategy BlockTicking(BlockComponentSection bcSection, CommandBuffer<ChunkStore> commandBuffer, int localX, int localY, int localZ, int blockIndex) {
      Ref<ChunkStore> blockRef = bcSection.getBlockReference(ChunkUtil.indexBlock(localX, localY, localZ));
      if (blockRef != null) { 
        HarvestingEngineComponent HarvestingEngineComponent_ = (HarvestingEngineComponent)commandBuffer.getComponent(blockRef, HarvestingEngineComponent.getComponentType());
        if (HarvestingEngineComponent_ != null) { 
            World world = commandBuffer.getExternalData().getWorld();  
            BlockModule.BlockStateInfo blockStateInfo = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            Vector3i LocalPosition = new Vector3i(localX, localY, localZ);
            Vector3ic globalCoord = EssentialsUtils.getGlobalPosition(LocalPosition, blockStateInfo, commandBuffer);
            Vector3i Position = new Vector3i(globalCoord.x(), globalCoord.y(), globalCoord.z());
            if (blockStateInfo != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();   
                
                if (HarvestingEngineComponent_ != null && !HarvestingEngineComponent_.LastActionTime.equals("1970-01-01 00:00:00")) { 
                    LocalDateTime last = LocalDateTime.parse(HarvestingEngineComponent_.LastActionTime, formatter);

                    if (last.isAfter(now)){
                        return BlockTickStrategy.CONTINUE;
                    }
                        
                }      
                LocalDateTime NextDate = now.plus(HarvestingEngineComponent_.Interval, ChronoUnit.MILLIS);
                HarvestingEngineComponent_.LastActionTime = NextDate.format(formatter); 
                
                world.execute(() -> {
                    FarmingHyDustryLib.ProccesingCoopsInRange(commandBuffer, HarvestingEngineComponent_, Position, (int)HarvestingEngineComponent_.Range, (int)HarvestingEngineComponent_.Fase);
                });  
                HarvestingEngineComponent_.Fase = (HarvestingEngineComponent_.Fase > 2) ? 0 : HarvestingEngineComponent_.Fase + 1; 
                return BlockTickStrategy.CONTINUE;  
            }
            else{
                return BlockTickStrategy.IGNORED;
            } 
        } 
        else {
            return BlockTickStrategy.IGNORED;
        } 
      } 
      else {
        return BlockTickStrategy.IGNORED;
      }
    }
    @Nullable
    public Query getQuery() {
        return Query.and(BlockSection.getComponentType(), BlockComponentSection.getComponentType());
    }
}
