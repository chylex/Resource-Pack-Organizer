package chylex.respack;
import chylex.respack.repository.NestedFolderPackFinder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

@Mod(ResourcePackOrganizer.MODID)
public final class ResourcePackOrganizer{
	public static final String MODID = "resourcepackorganizer";
	
	public ResourcePackOrganizer(){
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> NestedFolderPackFinder::register);
	}
	
	@SubscribeEvent
	public void onDedicatedServerSetup(final FMLDedicatedServerSetupEvent e){
		LogManager.getLogger(this).warn("Resource Pack Organizer is deactivated when installed on a dedicated server");
	}
}
