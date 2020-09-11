package chylex.respack;
import chylex.respack.gui.CustomResourcePackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = ResourcePackOrganizer.MODID)
public final class EventHandler{
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void onGuiOpen(final GuiOpenEvent e){
		final Minecraft mc = Minecraft.getInstance();
		final Screen gui = e.getGui();
		
		if (gui != null && gui.getClass() == PackScreen.class && mc.currentScreen instanceof OptionsScreen && !Screen.hasAltDown()){
			e.setGui(new CustomResourcePackScreen((PackScreen)gui));
		}
	}
}
