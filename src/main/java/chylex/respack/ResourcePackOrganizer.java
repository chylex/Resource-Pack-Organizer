package chylex.respack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ResourcePackOrganizer.MODID)
public final class ResourcePackOrganizer{
	public static final String MODID = "resourcepackorganizer";
	
	public ResourcePackOrganizer(){
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}
	
	@SubscribeEvent
	public void onDedicatedServerSetup(final FMLDedicatedServerSetupEvent e){
		throw new IllegalStateException("Resource Pack Organizer cannot be installed on a server!");
	}
}
