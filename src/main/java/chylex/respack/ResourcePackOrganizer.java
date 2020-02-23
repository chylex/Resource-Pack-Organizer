package chylex.respack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ResourcePackOrganizer.MODID)
public final class ResourcePackOrganizer{
	public static final String MODID = "resourcepackorganizer";
	
	public ResourcePackOrganizer(){
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		ModLoadingContext.get().registerConfig(Type.CLIENT, ConfigHandler.SPEC);
	}
	
	@SubscribeEvent
	public void onConfigLoading(ModConfig.Loading e){
		ConfigHandler.onConfigUpdated();
	}
	
	@SubscribeEvent
	public void onConfigReloading(ModConfig.Reloading e){
		ConfigHandler.onConfigUpdated();
	}
	
	@SubscribeEvent
	public void onDedicatedServerSetup(final FMLDedicatedServerSetupEvent e){
		throw new IllegalStateException("Resource Pack Organizer cannot be installed on a server!");
	}
}
