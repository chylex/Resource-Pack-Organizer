package chylex.respack.gui;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.respack.packs.ResourcePackListEntryFolder;

@SideOnly(Side.CLIENT)
public final class GuiUtils{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static void renderFolderEntry(ResourcePackListEntryFolder entry, int x, int y, boolean isSelected){
		entry.bindResourcePackIcon();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0F, 0F, 32, 32, 32F, 32F);
		GlStateManager.disableBlend();
		
		if ((mc.gameSettings.touchscreen || isSelected) && entry.showHoverOverlay()){
			Gui.drawRect(x, y, x+32, y+32, -1601138544);
			GlStateManager.color(1F, 1F, 1F, 1F);
		}
		
		String s = entry.getResourcePackName();
		
		if (mc.fontRenderer.getStringWidth(s) > 157){
			s = mc.fontRenderer.trimStringToWidth(s, 157-mc.fontRenderer.getStringWidth("..."))+"...";
		}
		
		mc.fontRenderer.drawStringWithShadow(s, x+32+2, y+1, 16777215);
		List<String> list = mc.fontRenderer.listFormattedStringToWidth(entry.getResourcePackDescription(), 157);
		
		for(int index = 0; index < 2 && index < list.size(); ++index){
			mc.fontRenderer.drawStringWithShadow(list.get(index), x+32+2, y+12+10*index, 8421504);
		}
	}
}
