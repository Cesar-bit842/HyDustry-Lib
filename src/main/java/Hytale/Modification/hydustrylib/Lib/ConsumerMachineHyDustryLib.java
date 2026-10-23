package Hytale.Modification.hydustrylib.Lib;
 
import java.util.Map;
import java.util.function.Consumer;

import org.joml.Vector3d;
import org.joml.Vector3i;
 
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import Hytale.Modification.hydustrylib.HyDustryLib;
import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent; 

public class ConsumerMachineHyDustryLib { 
    public static ConsumerMachineComponent ConsumerMachineValidatorComponent(World world, int InitialBlockRotation, Vector3i InitialBlockPosition, Map<String, String[]> InitialBlockRawTags, Vector3d ExpectedPosition){
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
        Vector3d[] ConsumerMachineFixedForward = new Vector3d[]{ 
           ExpectedPosition
        };
        Vector3d[] ConsumerMachineValidator = RailsHyDustryLib.OrientGlobalPositions(ConsumerMachineFixedForward, InitialBlockRotation); 
        String[] Paths = InitialBlockRawTags.get("HyDustryUtility.Vertical");  
        Vector3i ConsumerMachineRelativePosition = new Vector3i(
            InitialBlockPosition.x + (Paths == null ? (int)ConsumerMachineValidator[0].x : 0), 
            InitialBlockPosition.y + (Paths != null ? ((InitialBlockRotation >= 8 && InitialBlockRotation <= 63) ? -1 : + 1) : 0), 
            InitialBlockPosition.z + (Paths == null ? (int)ConsumerMachineValidator[0].z : 0) 
        ); 
        
        Ref<ChunkStore> ConsumerMachineBlockRef = EssentialsUtils.getBlockRef(world, ConsumerMachineRelativePosition);  
        if (ConsumerMachineBlockRef == null) { return null; }
        ConsumerMachineComponent ConsumerMachineComponentBlock = (ConsumerMachineComponent) chunkStore.getComponent(ConsumerMachineBlockRef, ConsumerMachineComponent.getComponentType());
        return ConsumerMachineComponentBlock;
    } 
}
