package eu.kennytv.upsidedown.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin {

    /*@Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        final EntityDimensions dimensions = cir.getReturnValue();
        cir.setReturnValue(new EntityDimensions(dimensions.width, -dimensions.height, dimensions.fixed));
    }*/

    /*@ModifyArg(at = @At(value = "INVOKE", target = ""), method = "travel", index = 1)
    public double travel(double y) {

    }*/

    @Inject(at = @At("RETURN"), method = "getActiveEyeHeight", cancellable = true)
    public void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(-cir.getReturnValueF());
    }
}
