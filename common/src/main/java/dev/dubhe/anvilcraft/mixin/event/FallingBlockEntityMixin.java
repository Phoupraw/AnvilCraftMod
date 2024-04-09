package dev.dubhe.anvilcraft.mixin.event;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.event.entity.AnvilFallOnLandEvent;
import dev.dubhe.anvilcraft.api.event.entity.AnvilHurtEntityEvent;
import dev.dubhe.anvilcraft.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FallingBlockEntity.class)
abstract class FallingBlockEntityMixin extends Entity {
    @Shadow
    private BlockState blockState;
    @Shadow
    private boolean cancelDrop;
    @Unique
    private float anvilcraft$fallDistance;

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @SuppressWarnings("resource")
    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;level()Lnet/minecraft/world/level/Level;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void anvilPerFallOnGround(CallbackInfo ci, Block block) {
        if (this.level().isClientSide()) return;
        if (this.onGround()) return;
        this.anvilcraft$fallDistance = this.fallDistance;
    }

    @SuppressWarnings("resource")
    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 10, target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;level()Lnet/minecraft/world/level/Level;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void anvilFallOnGround(CallbackInfo ci, Block block, BlockPos blockPos) {
        if (this.level().isClientSide()) return;
        if (!this.blockState.is(BlockTags.ANVIL)) return;
        AnvilFallOnLandEvent event = new AnvilFallOnLandEvent(this.level(), blockPos, (FallingBlockEntity) (Object) this, this.anvilcraft$fallDistance);
        AnvilCraft.EVENT_BUS.post(event);
        if (event.isAnvilDamage()) {
            BlockState state = this.blockState.is(ModBlocks.ROYAL_ANVIL.get()) ? this.blockState : AnvilBlock.damage(this.blockState);
            if (state != null) this.level().setBlockAndUpdate(blockPos, state);
            else {
                this.level().setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                if (!this.isSilent()) this.level().levelEvent(1029, this.getOnPos(), 0);
                this.cancelDrop = true;
            }
        }
    }

    @Redirect(method = "method_32879", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private static boolean anvilHurtEntity(@NotNull Entity instance, DamageSource source, float amount) {
        boolean bl = instance.hurt(source, amount);
        Entity directEntity = source.getDirectEntity();
        if (!bl || !(directEntity instanceof FallingBlockEntity entity)) return bl;
        AnvilCraft.EVENT_BUS.post(new AnvilHurtEntityEvent(entity, entity.getOnPos(), entity.level(), instance, amount));
        return true;
    }
}
