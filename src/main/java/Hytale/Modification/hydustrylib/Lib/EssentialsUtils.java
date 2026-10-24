package Hytale.Modification.hydustrylib.Lib;

import java.util.Map;

import javax.annotation.Nonnull;

import org.joml.Vector3i;
import org.joml.Vector3d;
import org.joml.Vector3ic;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil; 
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore; 
 
public class EssentialsUtils {
    public static <T extends Component<ChunkStore>> T GetComponentType(World world, Vector3i Position, @Nonnull ComponentType<ChunkStore, T> componentType){
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore(); 
        Ref<ChunkStore> BlockRef = EssentialsUtils.getBlockRef(world, Position);  

        if (BlockRef == null) { return null; }

        T ConsumerMachineComponentBlock = (T) chunkStore.getComponent(BlockRef, componentType);
        return ConsumerMachineComponentBlock;
    }  

    public static BlockType getBlockType(World world, Vector3i Position){
        Store<ChunkStore> ChunkStore = world.getChunkStore().getStore();
        if (ChunkStore != null) {
            Ref<ChunkStore> Section = world.getChunkStore().getChunkSectionReferenceAtBlock(Position.x, Position.y, Position.z);
            if (Section != null) {
                BlockSection blockSection = (BlockSection)ChunkStore.getComponent(Section, BlockSection.getComponentType()); 
                if(blockSection != null){
                    BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(Position.x, Position.y, Position.z));
                    if (blockType != null) {
                        return blockType;
                    }
                }  
            }
        }
        return null;
    }
    public static BlockSection getBlockSection(World world, Vector3i Position){
        Store<ChunkStore> ChunkStore = world.getChunkStore().getStore();
        if (ChunkStore != null) {
            Ref<ChunkStore> Section = world.getChunkStore().getChunkSectionReferenceAtBlock(Position.x, Position.y, Position.z);
            if (Section != null) {
                BlockSection blockSection = (BlockSection)ChunkStore.getComponent(Section, BlockSection.getComponentType()); 
                if(blockSection != null){
                    return blockSection; 
                }  
            }
        }
        return null;
    }
    
    public static Ref<ChunkStore> getBlockRef(World world, Vector3i Position){
        Store<ChunkStore> ChunkStore = world.getChunkStore().getStore();
        if (ChunkStore != null && Position != null) {
            Ref<ChunkStore> Section = world.getChunkStore().getChunkSectionReferenceAtBlock(Position.x, Position.y, Position.z);
            if (Section != null) {
                Ref<ChunkStore> BlockRef = BlockModule.getBlockEntity(ChunkStore, Section, Position.x, Position.y, Position.z);
                if(BlockRef != null){
                    return BlockRef;
                }
            }
        }
        return null;
    }
    public static int getBlockRotation(World world, Vector3i Position){
        Store<ChunkStore> ChunkStore = world.getChunkStore().getStore();
        if (ChunkStore != null) {
            Ref<ChunkStore> Section = world.getChunkStore().getChunkSectionReferenceAtBlock(Position.x, Position.y, Position.z);
            if (Section != null) {
                BlockSection blockSection = (BlockSection)ChunkStore.getComponent(Section, BlockSection.getComponentType()); 
                if(blockSection != null){
                    return blockSection.getRotationIndex(Position.x, Position.y, Position.z);
                } 
            }
        }
        return -1;
    } 
    
    public static Vector3ic getGlobalPosition(BlockModule.BlockStateInfo blockStateInfo, CommandBuffer<ChunkStore> commandBuffer) {
        int index = blockStateInfo.getIndex();
        int x = ChunkUtil.xFromIndex(index);
        int y = ChunkUtil.yFromIndex(index);
        int z = ChunkUtil.zFromIndex(index);
        return getGlobalPosition(new Vector3i(x, y, z), blockStateInfo, commandBuffer);
    }  
    public static Vector3ic getGlobalPosition(Vector3ic localPosition, BlockModule.BlockStateInfo blockStateInfo, CommandBuffer<ChunkStore> commandBuffer) {
        Ref<ChunkStore> sectionRef = blockStateInfo.getSectionRef();
        ChunkSection chunkSection = (ChunkSection) commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
        
        if (chunkSection == null) {
            return null;
        } else {
            
            int globalX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), localPosition.x());
            int globalZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), localPosition.z());
            int globalY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), localPosition.y()); 
            
            return new Vector3i(globalX, globalY, globalZ);
        }
   }
    
    public static Vector3i getRelativePosition(World world, Vector3i Position, Vector3d NextPosition){
        int RotationBlock = getBlockRotation(world, Position);
        BlockType TypeBlock = getBlockType(world, Position);
        Vector3d[] CPos = new Vector3d[]{ NextPosition };
        Vector3d[] FPos = RailsHyDustryLib.OrientGlobalPositions(CPos, RotationBlock); 
        String[] Paths = TypeBlock.getData().getRawTags().get("HyDustryUtility.Vertical");  
        Vector3i RPos = new Vector3i(
            Position.x + (Paths == null ? (int)FPos[0].x : 0), 
            Position.y + (Paths != null ? ((RotationBlock >= 8 && RotationBlock <= 63) ? -1 : + 1) : 0), 
            Position.z + (Paths == null ? (int)FPos[0].z : 0) 
        ); 
        Vector3i APos = FillerBlocksHyDustryLib.CalculateRealPositionUsingFillerBlocks(world, RPos);   
        return APos; 
   }
   public static Vector3i getValidRelativePosition(World world, Vector3i Position, Vector3d NextPosition){
        int RotationBlock = getBlockRotation(world, Position);
        BlockType TypeBlock = getBlockType(world, Position);
        Vector3d[] CPos = new Vector3d[]{ NextPosition };
        Vector3d[] FPos = RailsHyDustryLib.OrientGlobalPositions(CPos, RotationBlock); 
        String[] Paths = TypeBlock.getData().getRawTags().get("HyDustryUtility.Vertical");  
        Vector3i RPos = new Vector3i(
            Position.x + (Paths == null ? (int)FPos[0].x : 0), 
            Position.y + (Paths != null ? ((RotationBlock >= 8 && RotationBlock <= 63) ? -1 : + 1) : 0), 
            Position.z + (Paths == null ? (int)FPos[0].z : 0) 
        ); 
        Vector3i APos = FillerBlocksHyDustryLib.CalculateRealPositionUsingFillerBlocks(world, RPos); 
        BlockType blockType = EssentialsUtils.getBlockType(world, APos);
        if (blockType == null || blockType.getData() == null) return null;  
        return APos; 
   }
}
