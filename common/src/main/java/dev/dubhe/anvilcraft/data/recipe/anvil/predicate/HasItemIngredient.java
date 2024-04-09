package dev.dubhe.anvilcraft.data.recipe.anvil.predicate;

import com.google.common.base.Predicates;
import com.google.gson.JsonObject;
import dev.dubhe.anvilcraft.data.recipe.anvil.AnvilCraftingContainer;
import lombok.Getter;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HasItemIngredient extends HasItem {
    private final String type = "has_item_ingredient";

    public HasItemIngredient(Vec3 offset, ItemPredicate matchItem) {
        super(offset, matchItem);
    }

    public HasItemIngredient(JsonObject serializedRecipe) {
        super(serializedRecipe);
    }

    public HasItemIngredient(@NotNull FriendlyByteBuf buffer) {
        super(buffer);
    }

    public static @NotNull HasItemIngredient of(Vec3 offset, @NotNull Ingredient ingredient) {
        return HasItemIngredient.of(offset, ingredient, 1);
    }

    public static @NotNull HasItemIngredient of(Vec3 offset, @NotNull Ingredient ingredient, int count) {
        ItemPredicate.Builder item = ItemPredicate.Builder.item().withCount(MinMaxBounds.Ints.atLeast(count));
        List<Item> items = new ArrayList<>();
        for (Ingredient.Value value : ingredient.values) {
            if (value instanceof Ingredient.TagValue tagValue)
                item.of(tagValue.tag);
            if (value instanceof Ingredient.ItemValue itemValue)
                items.add(itemValue.item.getItem());
        }
        item.of(items.toArray(ItemLike[]::new));
        return new HasItemIngredient(offset, item.build());
    }

    @Override
    public boolean process(@NotNull AnvilCraftingContainer container) {
        Level level = container.getLevel();
        BlockPos pos = container.getPos();
        AABB aabb = new AABB(pos).move(this.offset);
        List<ItemEntity> entities = level.getEntities(EntityTypeTest.forClass(ItemEntity.class), aabb, Predicates.alwaysTrue());
        for (ItemEntity entity : entities) {
            ItemStack item = entity.getItem();
            if (this.matchItem.matches(item)) {
                int count = this.matchItem.count.getMin() != null ? this.matchItem.count.getMin() : 1;
                if (item.getItem().hasCraftingRemainingItem()) {
                    assert item.getItem().getCraftingRemainingItem() != null;
                    ItemStack stack = new ItemStack(item.getItem().getCraftingRemainingItem(), count);
                    Vec3 vec3 = pos.getCenter().add(this.offset);
                    ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, stack, 0.0, 0.0, 0.0);
                    level.addFreshEntity(itemEntity);
                }
                item.shrink(count);
                entity.setItem(item.copy());
                return true;
            }
        }
        return false;
    }
}
