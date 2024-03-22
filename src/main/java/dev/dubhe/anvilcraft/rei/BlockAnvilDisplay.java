package dev.dubhe.anvilcraft.rei;

import dev.dubhe.anvilcraft.data.recipe.anvil.block.BlockAnvilRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlockAnvilDisplay extends BasicDisplay {
    public static BlockAnvilDisplay testOf() {
        return new BlockAnvilDisplay(
          List.of(EntryIngredient.of(EntryStack.of(BlockStateEntryDefinition.TYPE, Blocks.OAK_FENCE.defaultBlockState()
            .setValue(FenceBlock.EAST, true)
            .setValue(FenceBlock.WEST, true)
            .setValue(FenceBlock.NORTH, true)
            .setValue(FenceBlock.SOUTH, true)))),
          List.of(EntryIngredient.of(EntryStack.of(BlockStateEntryDefinition.TYPE, Blocks.GRASS.defaultBlockState()))),
          null);
    }
    public static BlockAnvilDisplay of(BlockAnvilRecipe recipe) {
        return new BlockAnvilDisplay(null, null, recipe.getId());//TODO
    }
    public BlockAnvilDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, @Nullable ResourceLocation location) {
        super(inputs, outputs, Optional.ofNullable(location));
    }
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BlockAnvilCategory.ID;
    }
}
