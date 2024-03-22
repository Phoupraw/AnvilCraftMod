package dev.dubhe.anvilcraft.rei;

import dev.dubhe.anvilcraft.AnvilCraft;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemAnvilCategory implements DisplayCategory<ItemAnvilDisplay> {
    public static final CategoryIdentifier<ItemAnvilDisplay> ID = CategoryIdentifier.of(AnvilCraft.MOD_ID, "anvil_item_processing");
    public static final DisplayCategory<ItemAnvilDisplay> INSTANCE = new ItemAnvilCategory();
    @Override
    public CategoryIdentifier<? extends ItemAnvilDisplay> getCategoryIdentifier() {
        return ID;
    }
    @Override
    public Component getTitle() {
        return Component.translatableWithFallback(ID.getIdentifier().toLanguageKey("category"), "铁砧砸物品");
    }
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.ANVIL);
    }
    @Override
    public List<Widget> setupDisplay(ItemAnvilDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        for (EntryIngredient inputEntry : display.getInputEntries()) {
            widgets.add(Widgets.createSlot(new Point()).entries(inputEntry));
        }
        widgets.add(Widgets.createArrow(new Point()));
        for (EntryIngredient outputEntry : display.getOutputEntries()) {
            widgets.add(Widgets.createSlot(new Point()).entries(outputEntry));
        }
        return widgets;
    }
}
