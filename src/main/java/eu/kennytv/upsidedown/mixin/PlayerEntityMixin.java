package eu.kennytv.upsidedown.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PlayerEntityMixin {

    @Shadow
    private Vec3d velocity;

    @Shadow
    public abstract EntityType<?> getType();

    @Inject(at = @At("RETURN"), method = "getVelocity", cancellable = true)
    public void getVelocity(CallbackInfoReturnable<Vec3d> cir) {
        if (getType() == EntityType.PLAYER) {
            cir.setReturnValue(new Vec3d(velocity.x, -velocity.y, velocity.z));
        }
    }

    @Inject(at = @At("RETURN"), method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    public void setVelocity(Vec3d velocity, CallbackInfo ci) {
        if (getType() == EntityType.PLAYER) {
            this.velocity = new Vec3d(velocity.x, -velocity.y, velocity.z);
        }
    }
}
