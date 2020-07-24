package chylex.respack.gui;
import chylex.respack.ConfigHandler;
import chylex.respack.ConfigHandler.DisplayPosition;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static chylex.respack.ConfigHandler.DisplayPosition.BOTTOM_LEFT;
import static chylex.respack.ConfigHandler.DisplayPosition.BOTTOM_RIGHT;
import static chylex.respack.ConfigHandler.DisplayPosition.TOP_LEFT;
import static chylex.respack.ConfigHandler.DisplayPosition.TOP_RIGHT;

@OnlyIn(Dist.CLIENT)
public final class RenderPackListOverlay{
	private static final RenderPackListOverlay instance = new RenderPackListOverlay();
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static boolean isRegistered;
	
	public static void register(){
		if (isRegistered){
			return;
		}
		
		isRegistered = true;
		MinecraftForge.EVENT_BUS.register(instance);
		instance.refresh();
	}
	
	public static void unregister(){
		if (!isRegistered){
			return;
		}
		
		isRegistered = false;
		MinecraftForge.EVENT_BUS.unregister(instance);
	}
	
	private final List<String> packNames = new ArrayList<>(4);
	
	private void refresh(){
		packNames.clear();
		
		final List<ClientResourcePackInfo> packs = new ArrayList<>(mc.getResourcePackList().getEnabledPacks());
		Collections.reverse(packs);
		
		for(final ClientResourcePackInfo pack : packs){
			if (!pack.isAlwaysEnabled()){
				packNames.add(StringUtils.removeEndIgnoreCase(pack.getTitle().getString(), ".zip"));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onGuiOpen(final GuiOpenEvent e){
		if (mc.currentScreen instanceof ResourcePacksScreen){
			refresh();
		}
	}
	
	@SubscribeEvent
	public void onRenderGameOverlay(final RenderGameOverlayEvent.Post e){
		if (e.getType() == ElementType.TEXT && !packNames.isEmpty()){
			final DisplayPosition position = ConfigHandler.DISPLAY_POSITION.get();
			
			if ((position == TOP_LEFT || position == TOP_RIGHT) && mc.gameSettings.showDebugInfo){
				return;
			}
			
			final FontRenderer font = mc.fontRenderer;
			final MatrixStack matrix = e.getMatrixStack();
			final int color = Objects.requireNonNull(ConfigHandler.DISPLAY_COLOR.get().getColor());
			
			final int edgeDist = 3;
			final int topOffset = 2;
			final int ySpacing = font.FONT_HEIGHT;
			
			final int x = position == TOP_LEFT || position == BOTTOM_LEFT ? edgeDist : e.getWindow().getScaledWidth() - edgeDist;
			final int y = position == TOP_LEFT || position == TOP_RIGHT   ? edgeDist : e.getWindow().getScaledHeight() - edgeDist - topOffset - ySpacing * (1 + packNames.size());
			final boolean alignRight = position == TOP_RIGHT || position == BOTTOM_RIGHT;
			
			renderText(matrix, font, TextFormatting.UNDERLINE + "Resource Packs", x, y, color, alignRight);
			
			for(int line = 0; line < packNames.size(); line++){
				renderText(matrix, font, packNames.get(line), x, y + topOffset + (line + 1) * ySpacing, color, alignRight);
			}
		}
	}
	
	private static void renderText(final MatrixStack matrix, final FontRenderer renderer, final String line, final int x, final int y, final int color, final boolean alignRight){
		if (color == 0){
			renderer.drawString(matrix, line, alignRight ? x - renderer.getStringWidth(line) : x, y, color);
		}
		else{
			renderer.drawStringWithShadow(matrix, line, alignRight ? x - renderer.getStringWidth(line) : x, y, color);
		}
	}
}
