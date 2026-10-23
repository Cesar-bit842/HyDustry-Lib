package Hytale.Modification.hydustrylib.TypesOfConsumerMachineOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
 
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType; 
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock; 
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;

import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent;
import Hytale.Modification.hydustrylib.Lib.EssentialsUtils;
import Hytale.Modification.hydustrylib.Lib.FillerBlocksHyDustryLib;
import Hytale.Modification.hydustrylib.Lib.ItemContainerHyDustryLib;
import Hytale.Modification.hydustrylib.Lib.RailsHyDustryLib;  

public class Extractor {  
    public static void Execute(ConsumerMachineComponent ConsumerMachine) { 
        ItemContainerBlock OutputItemsContainer = ConsumerMachine.GetOutputItemsHyDustryComponent();
        ItemContainerBlock InputItemsContainer = ConsumerMachine.GetInputItemsHyDustryComponent(); 
         
        if (OutputItemsContainer == null || InputItemsContainer == null) { 
            return; 
        }  
 
        String activeFuelId = null; 
        if (ConsumerMachine.RequireFuel) {
            activeFuelId = ConsumerMachine.getAvailableFuelId(); 
            if (activeFuelId == null) { 
                return;  
            }
        } 

        if (!ConsumerMachine.ManufacturingAvailability()) { 
            return; 
        } 
 
        Map<String, String[]> systemRaw = ConsumerMachine.DeserializerMap(ConsumerMachine.SystemRaw);
        boolean requiresInputItems = systemRaw != null && systemRaw.containsKey("HyDustryUtility.Machine.Scanner.Require.Items");

        if (requiresInputItems) {
            if (!ConsumerMachine.ItemsVerification(ConsumerMachine.RequireFuel)) {
                return;
            }
        } 

        World world = Universe.get().getDefaultWorld();
        Vector3d UnsafePosition = ConsumerMachine.GetPosition();
        Vector3i Position = new Vector3i((int)UnsafePosition.x, (int)UnsafePosition.y, (int)UnsafePosition.z);
        
        ScanResult resultadoEscaneo = ProcessContentFromTheArea(ConsumerMachine, world, Position);
        if (resultadoEscaneo == null) {
            return; 
        } 

        String predominantItem = resultadoEscaneo.itemId();
        int baseBlockCount = resultadoEscaneo.count();
 
        Double multiplicator = getOutputMultiplicatorBlock(ConsumerMachine, predominantItem);
        if (multiplicator == null || multiplicator <= 0.0) {
            multiplicator = 1.0;  
        } 
        if (ConsumerMachine.RequireFuel && activeFuelId != null) {
            Map<String, Double> scannerUpgrades = ConsumerMachine.getAutoUpgradeScannerMultiplicatorRequirements();
            if (scannerUpgrades != null && scannerUpgrades.containsKey(predominantItem)) {
                Double bonusMultiplicator = scannerUpgrades.get(predominantItem);
                if (bonusMultiplicator != null && bonusMultiplicator > 0.0) {
                    multiplicator = bonusMultiplicator; 
                }
            }
        }
 
        Map<String, Integer> outputRecipes = getOutputBlocks(ConsumerMachine, predominantItem);
        if (outputRecipes == null || outputRecipes.isEmpty()) {
            return;
        } 
        List<ItemStack> outputItemStacks = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : outputRecipes.entrySet()) {
            String itemId = entry.getKey();
            int recipeAmount = entry.getValue(); 
            int finalQuantity = (int) Math.round((baseBlockCount * multiplicator) * recipeAmount);

            if (finalQuantity > 0 && itemId != null && !itemId.trim().isEmpty()) {
                ItemStack stack = new ItemStack(itemId, finalQuantity);
                if (stack.isValid()) {
                    outputItemStacks.add(stack);
                }
            }
        } 
        if (outputItemStacks.isEmpty()) {
            return;
        } 
 
        if (requiresInputItems) {
            ConsumerMachine.processItems(ConsumerMachine.RequireFuel);
        } 
        if (ConsumerMachine.RequireFuel && activeFuelId != null) {
            ConsumerMachine.processFuel(activeFuelId);
        } 
 
        long currentProcessTime = ConsumerMachine.ManufacturingTime; 
        int customTimer = getMultiplicatorTimer(ConsumerMachine, resultadoEscaneo);
        if (customTimer > 0) {
            currentProcessTime = (long) customTimer;
        }
 
        if (ConsumerMachine.RequireFuel && activeFuelId != null) { 
            Map<String, Integer> timeUpgrade = ConsumerMachine.getAutoUpgradeTimeRequirements();
            if (timeUpgrade != null && !timeUpgrade.isEmpty()) { 
                long fuelReducedTime = timeUpgrade.values().iterator().next().longValue();
                if (fuelReducedTime > 0) {
                    currentProcessTime = fuelReducedTime;
                }
            }
        }  

        ConsumerMachine.RenewManufacturingAvailability(currentProcessTime);  
        for (ItemStack stack : outputItemStacks) {
            if (ItemContainerHyDustryLib.ValidationItemForAdding(OutputItemsContainer.getItemContainer(), stack)) {
                OutputItemsContainer.getItemContainer().addItemStack(stack);
            }
        }  
    } 
    public static ScanResult ProcessContentFromTheArea(ConsumerMachineComponent consumerMachine, World world, Vector3i PositionBlock) {
        int RotationIndexBlock = EssentialsUtils.getBlockRotation(world, PositionBlock);
        Vector3d[] Positions = getScannerPositions(consumerMachine);
        Map<String, Integer> Blocks = getScannerBlocks(consumerMachine);   
        Positions = (Positions != null) ? Positions : new Vector3d[]{ new Vector3d(0, 0, -1) }; 
         
        Map<String, Integer> areaCounts = new HashMap<>();

        for (Vector3d Position : Positions) {

            Vector3d[] RelativePositionArray = new Vector3d[]{ new Vector3d(Position.x, Position.y, Position.z) }; 
            Vector3d[] CorrectedArrayPosition = RailsHyDustryLib.OrientGlobalPositions(RelativePositionArray, RotationIndexBlock); 
            Vector3d[] AbsolutePositionArray = new Vector3d[]{ 
                new Vector3d(
                    PositionBlock.x + CorrectedArrayPosition[0].x,
                    PositionBlock.y + CorrectedArrayPosition[0].y,
                    PositionBlock.z + CorrectedArrayPosition[0].z
                ) 
            }; 
            Vector3i CorrectedPosition = new Vector3i((int)AbsolutePositionArray[0].x, (int)AbsolutePositionArray[0].y, (int)AbsolutePositionArray[0].z);
            Vector3i InputItemsAbsolutePosition = FillerBlocksHyDustryLib.CalculateRealPositionUsingFillerBlocks(world, CorrectedPosition);  
            
            BlockType blockType = EssentialsUtils.getBlockType(world, InputItemsAbsolutePosition);
            if (blockType != null) {
                BlockGathering Gathering = blockType.getGathering();
                if (Gathering != null) { 
                    BlockBreakingDropType BreakDropList = Gathering.getBreaking();
                    if (BreakDropList != null) {  
                        String itemId = BreakDropList.getItemId(); 
                        String dropListId = BreakDropList.getDropListId();  
                        String detectedItem = null;
                        ItemModule itemModule = ItemModule.get(); 
                        if (itemId != null && !itemId.isEmpty()) {
                            if (Blocks != null && Blocks.containsKey(itemId)) {
                                detectedItem = itemId;
                            }
                        }    
                        else if (dropListId != null && !dropListId.isEmpty() && itemModule.isEnabled()) {     
                            List<ItemStack> drops = itemModule.getRandomItemDrops(dropListId); 
                            if (drops != null && !drops.isEmpty()) {
                                for (ItemStack drop : drops) {
                                    if (drop != null && drop.getItemId() != null) {
                                        String sampleId = drop.getItemId();  
                                        if (Blocks != null && Blocks.containsKey(sampleId)) {
                                            detectedItem = sampleId; 
                                            break; 
                                        }
                                    }
                                }
                            }
                        } 
                        if (detectedItem != null) {
                            areaCounts.put(detectedItem, areaCounts.getOrDefault(detectedItem, 0) + 1);
                        }
                    }
                }
            } 
        }
 
        String predominantOre = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : areaCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                predominantOre = entry.getKey();
            }
        }
  
        if (predominantOre != null) {
            return new ScanResult(predominantOre, maxCount);
        }
        return null;
    }

    private static Map<String, Integer> ConversionOfValuesMap(ConsumerMachineComponent consumerMachine, String ValuesName) {
        Map<String, String[]> systemRaw = consumerMachine.DeserializerMap(consumerMachine.SystemRaw);    
        if (!systemRaw.containsKey("HyDustryUtility.Machine." + ValuesName)) {
            return null; 
        }

        String[] itemKeys = systemRaw.get("HyDustryUtility.Machine." + ValuesName);

        if (itemKeys == null || itemKeys.length == 0) {
            return null; 
        }

        Map<String, Integer> result = new LinkedHashMap<>(); 

        for (int i = 0; i < itemKeys.length; i++) {
            String itemKey = itemKeys[i]; 
            if (itemKey == null || itemKey.trim().isEmpty()) {
                continue;
            }
 
            result.put(itemKey.trim(), i); 
        }
            
        return result;
    }
    private static Map<String, Integer> ConversionOfValuesMaping(ConsumerMachineComponent consumerMachine, String KeysName, String ValuesName) {
        Map<String, String[]> systemRaw = consumerMachine.DeserializerMap(consumerMachine.SystemRaw);   
        if (!systemRaw.containsKey("HyDustryUtility.Machine." + KeysName)) {
            return null; 
        }

        String[] itemIds = systemRaw.get("HyDustryUtility.Machine." + KeysName);
        String[] itemCounts = systemRaw.containsKey("HyDustryUtility.Machine." + ValuesName) 
                ? systemRaw.get("HyDustryUtility.Machine." + ValuesName) 
                : new String[0];

        if (itemIds == null || itemIds.length == 0) {
            return null; 
        }
 
        Map<String, Integer> Result = new LinkedHashMap<>(); 
        
        for (int i = 0; i < itemIds.length; i++) {
            String itemId = itemIds[i];
            if (itemId == null || itemId.trim().isEmpty()) {
                continue;
            }

            int count = 1;   
            if (itemCounts != null && i < itemCounts.length) {
                try {
                    count = Integer.parseInt(itemCounts[i].trim());
                    if (count <= 0) count = 1; 
                } 
                catch (NumberFormatException e) {
                    count = 1;  
                }
            }

            Result.put(itemId, Result.getOrDefault(itemId, 0) + count);
        }
         
        return Result;
    }
    private static Map<String, Double> ConversionOfValuesMapingDouble(ConsumerMachineComponent consumerMachine, String KeysName, String ValuesName) {
        Map<String, String[]> systemRaw = consumerMachine.DeserializerMap(consumerMachine.SystemRaw);   
        
        if (!systemRaw.containsKey("HyDustryUtility.Machine." + KeysName)) {
            return null; 
        }

        String[] itemIds = systemRaw.get("HyDustryUtility.Machine." + KeysName);
        String[] itemCounts = systemRaw.containsKey("HyDustryUtility.Machine." + ValuesName) 
                ? systemRaw.get("HyDustryUtility.Machine." + ValuesName) 
                : new String[0];

        if (itemIds == null || itemIds.length == 0) {
            return null; 
        }

        Map<String, Double> Result = new LinkedHashMap<>(); 
        
        for (int i = 0; i < itemIds.length; i++) {
            String itemId = itemIds[i];
            if (itemId == null || itemId.trim().isEmpty()) {
                continue;
            }

            double count = 1.0;   
            if (itemCounts != null && i < itemCounts.length) {
                try {
                    count = Double.parseDouble(itemCounts[i].trim());
                    if (count <= 0.0) count = 1.0; 
                } 
                catch (NumberFormatException e) {
                    count = 1.0;  
                }
            }

            Result.put(itemId, Result.getOrDefault(itemId, 0.0) + count);
        }
        
        return Result;
    }
 
    public static Vector2i getOffsetScannerObsolete(ConsumerMachineComponent consumerMachine) {
        int offsetX = getValueFromTag(consumerMachine, "Scanner.Offset.X");
        int offsetY = getValueFromTag(consumerMachine, "Scanner.Offset.Y");
        return new Vector2i(offsetX, offsetY);
    } 
    public static Vector2i getSizeScannerObsolete(ConsumerMachineComponent consumerMachine) {
        int sizeX = getValueFromTag(consumerMachine, "Scanner.Size.X");
        int sizeY = getValueFromTag(consumerMachine, "Scanner.Size.Y");
        return new Vector2i(sizeX, sizeY);
    }
    public static Vector3d[] getScannerPositions(ConsumerMachineComponent consumerMachine) {
        return RailsHyDustryLib.ConvertingTagsPositionstoArrays(consumerMachine.DeserializerMap(consumerMachine.SystemRaw), "Machine.Scanner.Positions");
    }

    public static Map<String, Integer> getScannerBlocks(ConsumerMachineComponent consumerMachine) {
        return ConversionOfValuesMap(consumerMachine, "Scanner.Blocks");
    }
    public static Map<String, Integer> getOutputBlocks(ConsumerMachineComponent consumerMachine, String Block) {
        Map<String, Integer> Block_ = getScannerBlocks(consumerMachine); 
        if (Block_ == null || !Block_.containsKey(Block)) {
            return null; 
        } 
        Integer OutputPosition = Block_.get(Block);
        if (OutputPosition == null) {
            return null;
        }
    
        String Blocks_ = "Output.Items." + OutputPosition;
        String Counts_ = "Output.Counts." + OutputPosition;  
        Map<String, Integer> Blocks = ConversionOfValuesMaping(consumerMachine, Blocks_, Counts_); 
        return Blocks;
    }
    public static Double getOutputMultiplicatorBlock(ConsumerMachineComponent consumerMachine, String Block) { 
        String Blocks_ = "Output.Items";
        String Counts_ = "Output.Multiplicator";
        Map<String, Double> Blocks = ConversionOfValuesMapingDouble(consumerMachine, Blocks_, Counts_); 
        if (Blocks == null || !Blocks.containsKey(Block)) {
            return null; 
        } 
        Double Multiplicator = Blocks.get(Block);
        if (Multiplicator == null) {
            return null;
        }
        return Multiplicator;
    }
    public static Integer getOutputTimerBlock(ConsumerMachineComponent consumerMachine, String Block) {
        Map<String, Integer> Block_ = getScannerBlocks(consumerMachine); 
        if (Block_ == null || !Block_.containsKey(Block)) {
            return null; 
        } 
        Integer OutputPosition = Block_.get(Block);
        if (OutputPosition == null) {
            return null;
        }
 
        Map<String, String[]> systemRaw = consumerMachine.DeserializerMap(consumerMachine.SystemRaw);
        String fullKey = "HyDustryUtility.Machine.Output.Timer." + OutputPosition;
        
        if (systemRaw != null && systemRaw.containsKey(fullKey)) {
            String[] values = systemRaw.get(fullKey);
            if (values != null && values.length > 0 && values[0] != null) {
                try {
                    return Integer.parseInt(values[0].trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    private static int getValueFromTag(ConsumerMachineComponent consumerMachine, String tag) { 
        Map<String, String[]> systemRaw = consumerMachine.DeserializerMap(consumerMachine.SystemRaw); 
        String fullKey = "HyDustryUtility.Machine." + tag;
        
        if (systemRaw.containsKey(fullKey)) {
            String[] values = systemRaw.get(fullKey);
            if (values != null && values.length > 0 && values[0] != null) {
                try {
                    return Integer.parseInt(values[0].trim());
                } catch (NumberFormatException e) { 
                    return 0; 
                }
            }
        }
        return 0; 
    } 
    public static int getMultiplicatorTimer(ConsumerMachineComponent consumerMachine, ScanResult scanResult) {
        
        if (scanResult == null) {
            return (int) consumerMachine.ManufacturingTime;
        }

        
        int baseBlockCount = scanResult.count(); 
        
        
        Map<String, String[]> systemRaw = consumerMachine.DeserializerMap(consumerMachine.SystemRaw); 
        String blockType = scanResult.itemId; 

        
        String key = "HyDustryUtility.Machine.Output.Multiplicator.Timer." + blockType;

        
        if (systemRaw != null && systemRaw.containsKey(key)) {
            String[] values = systemRaw.get(key);
            
            
            if (values != null && values.length > baseBlockCount) {
                String targetValue = values[baseBlockCount - 1]; 
                
                if (targetValue != null) {
                    try {
                        
                        return Integer.parseInt(targetValue.trim());
                    } catch (NumberFormatException ignored) {
                        
                    }
                }
            }
        }

        
        return (int) consumerMachine.ManufacturingTime;
    }

    public record ScanResult(String itemId, int count) {}
}