package eu.kennytv.upsidedown.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    private ShaderEffect shader;

    @Shadow
    public abstract void onCameraEntitySet(Entity entity);

    @Shadow
    protected abstract void loadShader(Identifier identifier);

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(MinecraftClient client, ResourceManager resourceManager, BufferBuilderStorage bufferBuilderStorage, CallbackInfo ci) {
        onCameraEntitySet(null);
    }

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (shader == null) {
            loadShader(new Identifier("shaders/post/flip.json"));
        }
    }
}
