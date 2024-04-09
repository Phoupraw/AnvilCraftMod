package dev.dubhe.anvilcraft.data.generator.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.dubhe.anvilcraft.data.generator.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.data.recipe.anvil.AnvilRecipe;
import dev.dubhe.anvilcraft.init.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class CookingRecipesLoader {
    public static void init(RegistrateRecipeProvider provider) {
        AnvilRecipe.Builder.create(RecipeCategory.FOOD)
                .hasBlock(Blocks.IRON_TRAPDOOR)
                .hasItemIngredient(Items.WHEAT)
                .spawnItem(new Vec3(0.0, -1.0, 0.0), ModItems.FLOUR)
                .unlockedBy(AnvilCraftDatagen.hasItem(Items.WHEAT), AnvilCraftDatagen.has(Items.WHEAT))
                .save(provider);
        boil(ModItems.BEEF_MUSHROOM_STEW_RAW.get(), 1, ModItems.BEEF_MUSHROOM_STEW.get(), 1, provider);
        cook(ModItems.UTUSAN_RAW.get(), 1, ModItems.UTUSAN.get(), 1, provider);
    }

    public static void cook(Item item, int count, Item item1, int count1, RegistrateRecipeProvider provider) {
        AnvilRecipe.Builder.create(RecipeCategory.FOOD)
                .hasBlock(Blocks.CAULDRON)
                .hasBlock(new Vec3(0.0, -2.0, 0.0), BlockTags.CAMPFIRES)
                .hasItemIngredient(new Vec3(0.0, -1.0, 0.0), count, item)
                .spawnItem(item1, count1)
                .unlockedBy(AnvilCraftDatagen.hasItem(item), AnvilCraftDatagen.has(item))
                .save(provider, "cook/" + BuiltInRegistries.ITEM.getKey(item1).getPath());
    }

    public static void boil(Item item, int count, Item item1, int count1, RegistrateRecipeProvider provider) {
        AnvilRecipe.Builder.create(RecipeCategory.FOOD)
                .hasBlock(Blocks.WATER_CAULDRON, new Vec3(0.0, -1.0, 0.0), Map.entry(LayeredCauldronBlock.LEVEL, 1))
                .hasBlock(new Vec3(0.0, -2.0, 0.0), BlockTags.CAMPFIRES)
                .hasItemIngredient(new Vec3(0.0, -1.0, 0.0), count, item)
                .setBlock(Blocks.CAULDRON)
                .spawnItem(item1, count1)
                .unlockedBy(AnvilCraftDatagen.hasItem(item), AnvilCraftDatagen.has(item))
                .save(provider, "boil/" + BuiltInRegistries.ITEM.getKey(item1).getPath() + "_1");
        AnvilRecipe.Builder.create(RecipeCategory.FOOD)
                .hasBlock(Blocks.WATER_CAULDRON, new Vec3(0.0, -1.0, 0.0), Map.entry(LayeredCauldronBlock.LEVEL, 2))
                .hasBlock(new Vec3(0.0, -2.0, 0.0), BlockTags.CAMPFIRES)
                .hasItemIngredient(new Vec3(0.0, -1.0, 0.0), count, item)
                .setBlock(Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1))
                .spawnItem(item1, count1)
                .unlockedBy(AnvilCraftDatagen.hasItem(item), AnvilCraftDatagen.has(item))
                .save(provider, "boil/" + BuiltInRegistries.ITEM.getKey(item1).getPath() + "_2");
        AnvilRecipe.Builder.create(RecipeCategory.FOOD)
                .hasBlock(Blocks.WATER_CAULDRON, new Vec3(0.0, -1.0, 0.0), Map.entry(LayeredCauldronBlock.LEVEL, 3))
                .hasBlock(new Vec3(0.0, -2.0, 0.0), BlockTags.CAMPFIRES)
                .hasItemIngredient(new Vec3(0.0, -1.0, 0.0), count, item)
                .setBlock(Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 2))
                .spawnItem(item1, count1)
                .unlockedBy(AnvilCraftDatagen.hasItem(item), AnvilCraftDatagen.has(item))
                .save(provider, "boil/" + BuiltInRegistries.ITEM.getKey(item1).getPath() + "_3");
    }
}
