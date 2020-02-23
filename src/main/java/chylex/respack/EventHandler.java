package chylex.respack;
import chylex.respack.gui.CustomResourcePackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = ResourcePackOrganizer.MODID)
public final class EventHandler{
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void onGuiOpen(GuiOpenEvent e){
		if (e.getGui() != null && e.getGui().getClass() == ResourcePacksScreen.class && !Screen.hasAltDown()){
			Minecraft mc = Minecraft.getInstance();
			e.setGui(new CustomResourcePackScreen(mc.currentScreen, mc.gameSettings));
		}
	}
}
