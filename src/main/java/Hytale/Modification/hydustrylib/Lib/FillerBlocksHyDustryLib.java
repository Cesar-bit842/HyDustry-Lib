package Hytale.Modification.hydustrylib.Lib;

import org.joml.Vector3i;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.BlockMaterial; 
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;

public class FillerBlocksHyDustryLib {
    
    public static Vector3i CalculateRealPositionUsingFillerBlocks(World world, Vector3i Position){
        Ref<ChunkStore> sectionRef = world.getChunkStore().getChunkSectionReferenceAtBlock(Position.x, Position.y, Position.z);
        if (sectionRef == null || !sectionRef.isValid()) {
            return Position;
        }
 
        var blockSection = world.getChunkStore().getStore().getComponent(sectionRef, BlockSection.getComponentType());
        if (blockSection == null) {
            return Position;
        }
 
        int filler = blockSection.getFiller(Position.x, Position.y, Position.z);
        int ox = Position.x - FillerBlockUtil.unpackX(filler);
        int oy = Position.y - FillerBlockUtil.unpackY(filler);
        int oz = Position.z - FillerBlockUtil.unpackZ(filler);
 
        BlockType blockType = EssentialsUtils.getBlockType(world, new Vector3i(ox, oy, oz));
        if (blockType == null || blockType == BlockType.EMPTY || (blockType.getMaterial() != BlockMaterial.Solid)) {
            return Position;
        }

        return new Vector3i(ox, oy, oz);
    }
}
