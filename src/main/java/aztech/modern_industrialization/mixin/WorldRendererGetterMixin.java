package aztech.modern_industrialization.mixin;


import aztech.modern_industrialization.mixin_impl.WorldRendererGetter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientWorld.class)
public class WorldRendererGetterMixin implements WorldRendererGetter
{
    @Shadow
    private WorldRenderer worldRenderer;

    @Override
    public WorldRenderer modern_industrialization_getWorldRenderer() {
        return this.worldRenderer;
    }
}
