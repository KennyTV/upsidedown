package eu.kennytv.upsidedown.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(at = @At("HEAD"), method = "setupTransforms")
    public void setupTransforms(AbstractClientPlayerEntity player, MatrixStack stack, float bob, float g, float h, CallbackInfo ci) {
        stack.translate(0.0, player.getHeight() + 0.1f, 0);
        stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180f));
    }
}
