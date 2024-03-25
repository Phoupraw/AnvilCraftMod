package dev.dubhe.anvilcraft.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@RequiredArgsConstructor
@Getter
public class AnvilPressingContainer implements Container {
    private final ServerLevel level;
    private final BlockPos pos;
    private final float luck;
    @Override
    public int getContainerSize() {
        return 0;
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
    @Override
    public ItemStack getItem(int slot) {
        return null;
    }
    @Override
    public ItemStack removeItem(int slot, int amount) {
        return null;
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return null;
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
    
    }
    @Override
    public void setChanged() {
    
    }
    @Override
    public boolean stillValid(Player player) {
        return false;
    }
    @Override
    public void clearContent() {
    
    }
}
