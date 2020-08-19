package eu.kennytv.upsidedown.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ProtectedMemberInFinalClass")
@Mixin(Camera.class)
public final class CameraMixin {
    @Shadow
    @Final
    private Vector3f horizontalPlane;
    @Shadow
    @Final
    private Vector3f verticalPlane;
    @Shadow
    @Final
    private Vector3f diagonalPlane;
    @Shadow
    @Final
    private Quaternion rotation;
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;
    /*
    @Shadow
    private float cameraY;
    @Shadow
    private float lastCameraY;
     */

    @Inject(at = @At("RETURN"), method = "update")
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        /*setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
        setPos(MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.getX()),
                MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY()) + MathHelper.lerp(tickDelta, lastCameraY, cameraY),
                MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.getZ()));
        stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180f));*/
    }

    @Inject(at = @At("HEAD"), method = "setRotation", cancellable = true)
    public void setRotation(float yaw, float pitch, CallbackInfo ci) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.rotation.set(0F, 0F, 0F, 1F);
        this.rotation.hamiltonProduct(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        this.rotation.hamiltonProduct(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch));
        //rotation.hamiltonProduct(new Quaternion(Vector3f.POSITIVE_X, 180F, true)); // upsidedown
        this.horizontalPlane.set(0.0f, 0.0f, 1.0f);
        this.horizontalPlane.rotate(this.rotation);
        this.verticalPlane.set(0.0f, 1.0f, 0.0f);
        this.verticalPlane.rotate(this.rotation);
        this.diagonalPlane.set(1.0f, 0.0f, 0.0f);
        this.diagonalPlane.rotate(this.rotation);
        diagonalPlane.rotate(new Quaternion(Vector3f.POSITIVE_Z, 180F, true)); // upsidedown
        ci.cancel();
    }

    /*@Shadow
    protected void setRotation(float yaw, float pitch) {
    }*/

    @Shadow
    protected void setPos(double x, double y, double z) {
    }
}
