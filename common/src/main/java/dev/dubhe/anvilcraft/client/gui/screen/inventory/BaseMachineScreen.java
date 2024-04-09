package dev.dubhe.anvilcraft.client.gui.screen.inventory;

import dev.dubhe.anvilcraft.client.gui.component.OutputDirectionButton;
import dev.dubhe.anvilcraft.inventory.BaseMachineMenu;
import dev.dubhe.anvilcraft.network.MachineOutputDirectionPack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.BiFunction;

public abstract class BaseMachineScreen<T extends BaseMachineMenu> extends AbstractContainerScreen<T> {
    @Setter
    private BiFunction<Integer, Integer, OutputDirectionButton> directionButtonSupplier;
    @Getter
    private OutputDirectionButton directionButton = null;
    @Getter
    private final Player player;

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.directionButtonSupplier = BaseMachineScreen.getDirectionButtonSupplier(134, 18);
        this.player = playerInventory.player;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.directionButton = directionButtonSupplier.apply(this.leftPos, this.topPos);
        this.addRenderableWidget(directionButton);
    }

    @Contract(pure = true)
    protected static @NotNull BiFunction<Integer, Integer, OutputDirectionButton> getDirectionButtonSupplier(int x, int y, Direction... skip) {
        return (i, j) -> new OutputDirectionButton(i + x, j + y, button -> {
            if (button instanceof OutputDirectionButton button1) {
                Arrays.stream(skip).forEach(button1::skip);
                MachineOutputDirectionPack packet = new MachineOutputDirectionPack(button1.next());
                packet.send();
            }
        }, Direction.DOWN);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
