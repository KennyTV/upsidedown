package eu.kennytv.upsidedown.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private Vec3d velocity;
    @Shadow
    public boolean verticalCollision;
    @Shadow
    private Vec3d pos;
    @Shadow
    public World world;
    @Shadow
    private Box entityBounds;

    @Shadow
    public abstract void setOnGround(boolean onGround);

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract float getStandingEyeHeight();

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (isPlayer()) {
            final EntityDimensions dimensions = cir.getReturnValue();
            cir.setReturnValue(new EntityDimensions(dimensions.width, -dimensions.height, dimensions.fixed));
        }
    }

    @Inject(at = @At("HEAD"), method = "setBoundingBox", cancellable = true)
    public void setBoundingBox(Box boundingBox, CallbackInfo ci) {
        if (isPlayer()) {
            entityBounds = boundingBox.offset(0, -getStandingEyeHeight(), 0);
        }
    }

    @Inject(at = @At("HEAD"), method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    public void setVelocity(Vec3d velocity, CallbackInfo ci) {
        if (!isPlayer()) return;

        ci.cancel();
        final double yDiff = velocity.y - this.velocity.y;
        double y = this.velocity.y - yDiff;
        if (y > 4) {
            y = 4;
        } else if (y < -4) {
            y = -4;
        }
        this.velocity = new Vec3d(velocity.x, y, velocity.z);
    }

    @ModifyVariable(at = @At("HEAD"), method = "changeLookDirection", ordinal = 1)
    public double changeCursorDeltaY(double cursorDeltaY) {
        return -cursorDeltaY;
    }

    @Inject(at = @At("RETURN"), method = "move")
    public void move(MovementType type, Vec3d movement, CallbackInfo ci) {
        if (isPlayer()) {
            setOnGround(this.verticalCollision && movement.y > 0.0D);
        }
    }

    @Inject(at = @At("RETURN"), method = "getEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F", cancellable = true)
    public void getEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (isPlayer()) {
            cir.setReturnValue(-cir.getReturnValueF());
        }
    }

    @ModifyVariable(at = @At("HEAD"), method = "fall")
    public boolean fall(boolean onGround) {
        // Still called with the wrong onGround in the move method
        return isPlayer() != onGround;
    }

    @Inject(at = @At("RETURN"), method = "getLandingPos", cancellable = true)
    public void getLandingPos(CallbackInfoReturnable<BlockPos> cir) {
        if (!isPlayer()) return;

        int i = MathHelper.floor(this.pos.x);
        int j = MathHelper.floor(this.pos.y + 0.2f); //TODO only need to change this
        int k = MathHelper.floor(this.pos.z);
        BlockPos blockPos = new BlockPos(i, j, k);
        if (this.world.getBlockState(blockPos).isAir()) {
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = this.world.getBlockState(blockPos2);
            Block block = blockState.getBlock();
            if (block.isIn(BlockTags.FENCES) || block.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock) {
                cir.setReturnValue(blockPos2);
            }
        }

        cir.setReturnValue(blockPos);
    }

    private boolean isPlayer() {
        return getType() == EntityType.PLAYER;
    }
}
