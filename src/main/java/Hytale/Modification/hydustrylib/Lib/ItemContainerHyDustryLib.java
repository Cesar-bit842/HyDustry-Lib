package Hytale.Modification.hydustrylib.Lib; 

import javax.annotation.Nonnull;

import org.joml.Vector3d;
import org.joml.Vector3i;
  
import com.hypixel.hytale.server.core.universe.world.World;  
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;

public class ItemContainerHyDustryLib {
    public static ItemContainerBlock GetItemContainerComponent(World world, Vector3i Position){  
        return ((ItemContainerBlock) EssentialsUtils.GetComponentType(
            world, 
            Position, 
            ItemContainerBlock.getComponentType()
        ));
    }
    public static ItemContainerBlock GetItemContainerComponent(World world, Vector3i Position, Vector3d NextPosition){  
        return ((ItemContainerBlock) EssentialsUtils.GetComponentType(
            world, 
            EssentialsUtils.getValidRelativePosition(world, Position, NextPosition), 
            ItemContainerBlock.getComponentType()
        ));
    } 
    public static boolean ValidationItemForAdding(@Nonnull ItemContainerBlock ItemContainer, @Nonnull ItemStack itemStack) {
        for (short i = 0; i < ItemContainer.getItemContainer().getCapacity(); ++i) {
            ItemStack checked = ItemContainer.getItemContainer().getItemStack(i);
            if (!ItemStack.isEmpty(checked)) {
                if(checked.getItem().getId() == itemStack.getItem().getId()){
                    if(checked.getItem().getMaxStack() > checked.getQuantity()){
                        return true;
                    }
                } 
            }
            else{
                return true;
            }
        } 
        return false;
    }
    public static ItemStack getLastItem(@Nonnull ItemContainerBlock ItemContainer) { 
        int lastSlot = ItemContainer.getItemContainer().getCapacity() - 1; 
        for (int i = lastSlot; i >= 0; i--) {
            ItemStack checked = ItemContainer.getItemContainer().getItemStack((short)i); 
            if (checked != null && !ItemStack.isEmpty(checked)) {
                return checked; 
            }
        }
        return null; 
    }
    public static boolean removeLastItem(@Nonnull ItemContainerBlock ItemContainer) {
        int lastSlot = ItemContainer.getItemContainer().getCapacity() - 1; 
        for (int i = lastSlot; i >= 0; i--) {
            ItemStack checked = ItemContainer.getItemContainer().getItemStack((short)i);
            
            if (checked != null && !ItemStack.isEmpty(checked)) { 
                ItemContainer.getItemContainer().removeItemStackFromSlot((short)i);
                return true;  
            }
        }
        return false; 
    } 
    public static boolean ValidationItemForAdding(@Nonnull SimpleItemContainer simpleItemContainer , @Nonnull ItemStack itemStack) { 
        for (short i = 0; i < simpleItemContainer.getCapacity(); ++i) {
            ItemStack checked = simpleItemContainer.getItemStack(i);
            if (!ItemStack.isEmpty(checked)) {
                if(checked.getItem().getId() == itemStack.getItem().getId()){
                    if(checked.getItem().getMaxStack() > checked.getQuantity()){
                        return true;
                    }
                } 
            }
            else{
                return true;
            }
        }

        return false;
    }
    public static ItemStack getLastItem(@Nonnull SimpleItemContainer simpleItemContainer, int count) {  
        if (count <= 0) return null;

        int lastSlot = simpleItemContainer.getCapacity() - 1; 
        
        for (int i = lastSlot; i >= 0; i--) {
            ItemStack checked = simpleItemContainer.getItemStack((short)i); 
            
            if (checked != null && !ItemStack.isEmpty(checked)) {
                
                ItemStack visualCopy = checked.cleanCopy();
                
                
                int cantidadFinal = Math.min(checked.getQuantity(), count); 
                return visualCopy.withQuantity(cantidadFinal); 
            }
        }
        return null; 
    } 
    public static boolean removeLastItem(@Nonnull SimpleItemContainer simpleItemContainer, int count) { 
        if (count <= 0) return false;

        int lastSlot = simpleItemContainer.getCapacity() - 1; 
        
        for (int i = lastSlot; i >= 0; i--) {
            ItemStack checked = simpleItemContainer.getItemStack((short)i);
            
            if (checked != null && !ItemStack.isEmpty(checked)) {
                
                int currentQuantity = checked.getQuantity();  
                if (count >= currentQuantity) {
                    simpleItemContainer.removeItemStackFromSlot((short)i);
                }  
                else {
                    simpleItemContainer.setItemStackForSlot((short)i, checked.withQuantity(currentQuantity - count));
                }
                return true;  
            }
        }
        return false; 
    }
}
