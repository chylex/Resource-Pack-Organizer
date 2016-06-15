package chylex.respack.gui;
import java.io.File;
import java.net.URI;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Util;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import chylex.respack.packs.ResourcePackListEntryFolder;

@SideOnly(Side.CLIENT)
public final class GuiUtils{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static void openFolder(File file){
		String s = file.getAbsolutePath();
		
		if (Util.getOSType() == Util.EnumOS.OSX){
			try{
				Runtime.getRuntime().exec(new String[]{ "/usr/bin/open", s });
				return;
			}catch(Exception e){}
		}
		else if (Util.getOSType() == Util.EnumOS.WINDOWS){
			String command = String.format("cmd.exe /C start \"Open file\" \"%s\"",s);
			
			try{
				Runtime.getRuntime().exec(command);
				return;
			}catch(Exception e){}
		}
		
		try{
			final Class cls = Class.forName("java.awt.Desktop");
			final Object desktop = cls.getMethod("getDesktop").invoke(null);
			
			cls.getMethod("browse",URI.class).invoke(desktop,file.toURI());
		}catch(Throwable t){
			Sys.openURL("file://"+s);
		}
	}
	
	public static void renderFolderEntry(ResourcePackListEntryFolder entry, int x, int y, boolean isSelected){
		entry.bindResourcePackIcon();
		GlStateManager.color(1F,1F,1F,1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gui.drawModalRectWithCustomSizedTexture(x,y,0F,0F,32,32,32F,32F);
		GlStateManager.disableBlend();
		
		int i2;

		if ((mc.gameSettings.touchscreen||isSelected)&&entry.showHoverOverlay()){
			Gui.drawRect(x,y,x+32,y+32,-1601138544);
			GlStateManager.color(1F,1F,1F,1F);
		}
		
		String s = entry.getResourcePackName();
		i2 = mc.fontRendererObj.getStringWidth(s);
		
		if (i2>157){
			s = mc.fontRendererObj.trimStringToWidth(s,157-mc.fontRendererObj.getStringWidth("..."))+"...";
		}
		
		mc.fontRendererObj.drawStringWithShadow(s,x+32+2,y+1,16777215);
		List list = mc.fontRendererObj.listFormattedStringToWidth(entry.getResourcePackDescription(),157);
		
		for(int j2 = 0; j2<2&&j2<list.size(); ++j2){
			mc.fontRendererObj.drawStringWithShadow((String)list.get(j2),x+32+2,y+12+10*j2,8421504);
		}
	}
}
