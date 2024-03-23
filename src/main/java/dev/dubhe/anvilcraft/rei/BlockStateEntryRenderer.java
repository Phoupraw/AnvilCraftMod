package dev.dubhe.anvilcraft.rei;

import com.google.common.base.Functions;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BlockStateEntryRenderer implements EntryRenderer<BlockState> {
    public static final BlockStateEntryRenderer INSTANCE = new BlockStateEntryRenderer();
    public static final ItemTransform TRANSFORM = new ItemTransform(new Vector3f(30, 120, 0), new Vector3f(0.925f, -0.8125f, 0), new Vector3f(0.625f));
    //FIXME 无法渲染半透明方块（比如染色玻璃）
    //FIXME 无法渲染以方块实体渲染的方块（比如箱子和告示牌）
    @Override
    public void render(EntryStack<BlockState> entry, GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        if (entry.isEmpty()) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level==null)return;
        BlockState blockState = entry.getValue();
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(bounds.getX(), bounds.getY(), 0);
        poseStack.mulPoseMatrix(new Matrix4f()
          .scaling(1, -1, 1)
          .scale(16 * 0.625f)
          .translate(0, -1, 0)
          .rotateX(0.5f)
          .rotateY(-0.5f)
        );
        //poseStack.translate(-0.5F, -0.5F, -0.5F);
        //new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(1, -1, 0), new Vector3f(0.625f)).apply(false, poseStack);
        //poseStack.scale(16, 16, 16);
        //new ItemTransform(new Vector3f(30, -30, 0), new Vector3f(0.925f, -0.8125f, 0), new Vector3f(0.625f)).apply(false, poseStack);
        //Lighting.setupForFlatItems();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, Functions.constant(graphics.bufferSource().getBuffer(Sheets.translucentCullBlockSheet()))::apply, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        graphics.bufferSource().endBatch();
        //Lighting.setupFor3DItems();
    }
    @Override
    public @Nullable Tooltip getTooltip(EntryStack<BlockState> entry, TooltipContext context) {
        if (entry.isEmpty()) return null;
        Tooltip tooltip = Tooltip.create(
          entry.asFormattedText(context),
          Component.translatable("soundCategory.block").withStyle(ChatFormatting.YELLOW));
        if (context.getFlag().isAdvanced()) {
            BlockState blockState = entry.getValue();
            MutableComponent currentLine = Component.empty().append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY));
            boolean first = true;
            for (var e : blockState.getValues().entrySet()) {
                Property<?> property = e.getKey();
                Comparable<?> value = e.getValue();
                if (!first) {
                    currentLine.append(Component.literal(",").withStyle(ChatFormatting.DARK_GRAY));
                    tooltip.add(currentLine);
                    currentLine = Component.empty().append(" ");
                }
                currentLine
                  .append(Component.literal(property.getName()).withStyle(ChatFormatting.GRAY))
                  .append(Component.literal("=").withStyle(ChatFormatting.DARK_GRAY))
                  .append(Component.literal(BlockStateEntryDefinition.getName(property, value)).withStyle(ChatFormatting.GRAY));
                first = false;
            }
            currentLine.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(currentLine);
            tooltip.add(Component.literal(Objects.toString(entry.getIdentifier())).withStyle(ChatFormatting.DARK_GRAY));
        }
        return tooltip;
    }
}
