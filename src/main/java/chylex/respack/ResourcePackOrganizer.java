package chylex.respack;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import chylex.respack.ConfigHandler.DisplayPosition;
import chylex.respack.gui.GuiCustomResourcePacks;
import chylex.respack.render.RenderPackListOverlay;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = ResourcePackOrganizer.MODID, name = ResourcePackOrganizer.MODNAME, useMetadata = true, guiFactory = "chylex.respack.gui.GuiModConfig")
public final class ResourcePackOrganizer{
	public static final String MODID = "ResourcePackOrganizer";
	public static final String MODNAME = "Resource Pack Organizer";
	
	private static ConfigHandler config;
	
	public static ConfigHandler getConfig(){
		return config;
	}
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		if (FMLCommonHandler.instance().getSide() == Side.SERVER){
			FMLLog.bigWarning(MODNAME+" cannot be installed on a server!");
			FMLCommonHandler.instance().exitJava(1,false);
		}
		
		config = new ConfigHandler(e.getSuggestedConfigurationFile());
		onConfigLoaded();
		
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		
		RenderPackListOverlay.refreshPackNames();
	}
	
	@NetworkCheckHandler
	public boolean onNetworkCheck(Map<String,String> versions, Side side){
		return true;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	@SideOnly(Side.CLIENT)
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui != null && e.gui.getClass() == GuiScreenResourcePacks.class){
			e.gui = new GuiCustomResourcePacks(Minecraft.getMinecraft().currentScreen);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onConfigChanged(OnConfigChangedEvent e){
		if (e.modID.equals(MODID)){
			config.reload();
			onConfigLoaded();
		}
	}
	
	private void onConfigLoaded(){
		if (config.options.getDisplayPosition() == DisplayPosition.DISABLED){
			RenderPackListOverlay.unregister();
		}
		else{
			RenderPackListOverlay.register();
		}
	}
}
