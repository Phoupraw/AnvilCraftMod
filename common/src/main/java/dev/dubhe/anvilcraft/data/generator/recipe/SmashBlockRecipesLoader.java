package dev.dubhe.anvilcraft.data.generator.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.data.generator.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.data.recipe.anvil.AnvilRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class SmashBlockRecipesLoader {
    public static void init(RegistrateRecipeProvider provider)  {
        smash(Blocks.COBBLESTONE, Blocks.GRAVEL, provider);
        smash(Blocks.GRAVEL, Blocks.SAND, provider);
        smash(Blocks.POLISHED_GRANITE, Blocks.GRANITE, provider);
        smash(Blocks.GRANITE, Blocks.RED_SAND, provider);
        smash(Blocks.POLISHED_ANDESITE, Blocks.ANDESITE, provider);
        smash(Blocks.ANDESITE, Blocks.TUFF, provider);
        smash(Blocks.POLISHED_DIORITE, Blocks.DIORITE, provider);
        smash(Blocks.DIORITE, Blocks.CALCITE, provider);
        smash(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, provider);
        smash(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, provider);
        smash(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS, provider);
        smash(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES, provider);
        smash(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, provider);
    }

    public static void smash(Block block, @NotNull Block block1, RegistrateRecipeProvider provider) {
        AnvilRecipe.Builder.create(RecipeCategory.MISC)
                .hasBlock(block)
                .setBlock(block1)
                .unlockedBy(AnvilCraftDatagen.hasItem(block.asItem()), AnvilCraftDatagen.has(block.asItem()))
                .save(provider, AnvilCraft.of("smash_block/" + BuiltInRegistries.BLOCK.getKey(block1).getPath()));
    }
}
