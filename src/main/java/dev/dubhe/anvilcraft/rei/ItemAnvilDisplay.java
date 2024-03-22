package dev.dubhe.anvilcraft.rei;

import dev.dubhe.anvilcraft.data.recipe.anvil.item.ItemAnvilRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ItemAnvilDisplay extends BasicDisplay {
    public static ItemAnvilDisplay of(ItemAnvilRecipe recipe) {
        return new ItemAnvilDisplay(null, null, recipe.getId());//TODO
    }
    public ItemAnvilDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, @Nullable ResourceLocation location) {
        super(inputs, outputs, Optional.ofNullable(location));
    }
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ItemAnvilCategory.ID;
    }
}
