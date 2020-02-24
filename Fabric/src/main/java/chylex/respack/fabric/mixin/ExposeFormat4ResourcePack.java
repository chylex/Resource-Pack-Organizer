package chylex.respack.fabric.mixin;
import chylex.respack.fabric.repository.ResourcePackUtils.IExposedResourcePackDelegate;
import net.minecraft.client.resource.Format4ResourcePack;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Format4ResourcePack.class)
public abstract class ExposeFormat4ResourcePack implements IExposedResourcePackDelegate{
	@Shadow
	private ResourcePack parent;
	
	@Override
	public ResourcePack getDelegate(){
		return parent;
	}
}
