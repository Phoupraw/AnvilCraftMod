package dev.dubhe.anvilcraft.rei;

import dev.dubhe.anvilcraft.AnvilCraft;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Arrow;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
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

public class BlockAnvilCategory implements DisplayCategory<BlockAnvilDisplay> {
    public static final CategoryIdentifier<BlockAnvilDisplay> ID = CategoryIdentifier.of(AnvilCraft.MOD_ID, "anvil_block_processing");
    public static final DisplayCategory<BlockAnvilDisplay> INSTANCE = new BlockAnvilCategory();
    @Override
    public CategoryIdentifier<? extends BlockAnvilDisplay> getCategoryIdentifier() {
        return ID;
    }
    @Override
    public Component getTitle() {
        return Component.translatableWithFallback(ID.getIdentifier().toLanguageKey("category"), "铁砧砸方块");
    }
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.ANVIL);
    }
    @Override
    public List<Widget> setupDisplay(BlockAnvilDisplay display, Rectangle bounds) {
        //TODO
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        Point point = new Point(bounds.getX(), bounds.getY());
        for (EntryIngredient inputEntry : display.getInputEntries()) {
            Slot slot = Widgets.createSlot(point).entries(inputEntry);
            widgets.add(slot);
            point.translate(slot.getBounds().getWidth(), 0);
        }
        Arrow arrow = Widgets.createArrow(point);
        widgets.add(arrow);
        point.translate(arrow.getBounds().getWidth(), 0);
        for (EntryIngredient outputEntry : display.getOutputEntries()) {
            Slot slot = Widgets.createSlot(point).entries(outputEntry);
            widgets.add(slot);
            point.translate(slot.getBounds().getWidth(), 0);
        }
        return widgets;
    }
}
