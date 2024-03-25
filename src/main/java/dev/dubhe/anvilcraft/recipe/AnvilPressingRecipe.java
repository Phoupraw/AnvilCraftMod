package dev.dubhe.anvilcraft.recipe;

import com.google.common.base.Predicates;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.dubhe.anvilcraft.inventory.AnvilPressingContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class AnvilPressingRecipe implements Recipe<AnvilPressingContainer> {
    public static final RecipeSerializer<AnvilPressingRecipe> SERIALIZER = new Serializer();
    private final ResourceLocation id;
    private final Collection<Pair<ItemPredicate, Integer>> itemIngredients;
    private final Map<BlockPos, BlockPredicate> blockIngredients;
    private final LootTable itemResults;
    private final Map<BlockPos, BlockState> blockResults;
    @Override
    public boolean matches(AnvilPressingContainer container, Level level) {
        return process(container, level.registryAccess(), true);
    }
    @Override
    public @NotNull ItemStack assemble(AnvilPressingContainer container, RegistryAccess registryAccess) {
        process(container, registryAccess, false);
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }
    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }
    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
    @Override
    public @NotNull RecipeType<?> getType() {
        return null;
    }
    public boolean process(AnvilPressingContainer container, RegistryAccess registryAccess, boolean simulate) {
        ServerLevel level = container.getLevel();
        Collection<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, new AABB(container.getPos()), Predicates.alwaysTrue());
        Collection<Pair<ItemEntity, ItemStack>> tracedItems = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            tracedItems.add(Pair.of(itemEntity, itemEntity.getItem().copy()));
        }
        Collection<Pair<ItemEntity, ItemStack>> modifiedItems = new ArrayList<>();
        Collection<MutablePair<ItemPredicate, Integer>> itemIngredients = getItemIngredients().stream().map(MutablePair::of).toList();
        for (MutablePair<ItemPredicate, Integer> itemIngredient : itemIngredients) {
            for (Pair<ItemEntity, ItemStack> tracedItem : tracedItems) {
                ItemStack itemStack = tracedItem.getRight();
                if (itemIngredient.getLeft().matches(itemStack)) {
                    int delta = Math.min(itemStack.getCount(), itemIngredient.getRight());
                    itemStack.shrink(delta);
                    modifiedItems.add(tracedItem);
                    int rest = itemIngredient.getRight() - delta;
                    if (rest > 0) {
                        itemIngredient.setRight(rest);
                    } else {
                        break;
                    }
                }
            }
            if (itemIngredient.getRight() > 0) {
                return false;
            }
        }
        for (Map.Entry<BlockPos, BlockPredicate> entry : getBlockIngredients().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockPredicate blockPredicate = entry.getValue();
            if (!blockPredicate.matches(level, pos)) {
                return false;
            }
        }
        if (!simulate) {
            for (Pair<ItemEntity, ItemStack> modifiedItem : modifiedItems) {
                modifiedItem.getLeft().setItem(modifiedItem.getRight());
            }
            Containers.dropContents(level, container.getPos(), NonNullList.of(ItemStack.EMPTY, getItemResults().getRandomItems(new LootParams(level, Map.of(), Map.of(), container.getLuck())).toArray(new ItemStack[0])));
            Set<BlockPos> blocksToRemove = new HashSet<>(getBlockIngredients().keySet());
            for (Map.Entry<BlockPos, BlockState> entry : getBlockResults().entrySet()) {
                BlockPos pos = entry.getKey();
                BlockState blockState = entry.getValue();
                level.setBlockAndUpdate(pos, blockState);
                blocksToRemove.remove(pos);
            }
            for (BlockPos pos : blocksToRemove) {
                level.destroyBlock(pos, false);
            }
        }
        return true;
    }
    private final static class Serializer implements RecipeSerializer<AnvilPressingRecipe> {
        @Override
        public AnvilPressingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            JsonArray jsonItemIngredients = GsonHelper.getAsJsonArray(serializedRecipe, "itemIngredients", new JsonArray(0));
            Collection<Pair<ItemPredicate, Integer>> itemIngredients = new ArrayList<>(jsonItemIngredients.size());
            for (JsonElement jsonItemIngredient0 : jsonItemIngredients) {
                JsonObject jsonItemIngredient = jsonItemIngredient0.getAsJsonObject();
                itemIngredients.add(Pair.of(ItemPredicate.fromJson(jsonItemIngredient.get("item")), GsonHelper.getAsInt(jsonItemIngredient, "count", 1)));
            }
            JsonArray jsonBlockIngredients = GsonHelper.getAsJsonArray(serializedRecipe, "blockIngredients", new JsonArray(0));
            Map<BlockPos, BlockPredicate> blockIngredients = new HashMap<>(jsonBlockIngredients.size());
            for (JsonElement jsonBlockIngredient0 : jsonBlockIngredients) {
                JsonObject jsonBlockIngredient = jsonBlockIngredient0.getAsJsonObject();
                //GsonHelper.getAsJsonArray()
                //blockIngredients.put()
                //TODO
            }
            LootTable itemResults = LootTable.EMPTY;
            Map<BlockPos, BlockState> blockResults = new HashMap<>();
            return new AnvilPressingRecipe(recipeId, itemIngredients, blockIngredients, itemResults, blockResults);
        }
        @Override
        public AnvilPressingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return null;
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, AnvilPressingRecipe recipe) {
        
        }
    }
}
