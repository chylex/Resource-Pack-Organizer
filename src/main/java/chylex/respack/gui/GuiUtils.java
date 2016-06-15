package chylex.respack.gui;
import java.io.File;
import java.net.URI;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Util;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import chylex.respack.packs.ResourcePackListEntryFolder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	
	public static void renderFolderEntry(ResourcePackListEntryFolder entry, int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_){
		entry.func_148313_c();
		GL11.glColor4f(1.0F,1.0F,1.0F,1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gui.func_146110_a(p_148279_2_,p_148279_3_,0.0F,0.0F,32,32,32.0F,32.0F);
		GL11.glDisable(GL11.GL_BLEND);
		
		int i2;

		if ((mc.gameSettings.touchscreen||p_148279_9_)&&entry.func_148310_d()){
			Gui.drawRect(p_148279_2_,p_148279_3_,p_148279_2_+32,p_148279_3_+32,-1601138544);
			GL11.glColor4f(1.0F,1.0F,1.0F,1.0F);
		}
		
		String s = entry.func_148312_b();
		i2 = mc.fontRenderer.getStringWidth(s);
		
		if (i2>157){
			s = mc.fontRenderer.trimStringToWidth(s,157-mc.fontRenderer.getStringWidth("..."))+"...";
		}
		
		mc.fontRenderer.drawStringWithShadow(s,p_148279_2_+32+2,p_148279_3_+1,16777215);
		List list = mc.fontRenderer.listFormattedStringToWidth(entry.func_148311_a(),157);
		
		for(int j2 = 0; j2<2&&j2<list.size(); ++j2){
			mc.fontRenderer.drawStringWithShadow((String)list.get(j2),p_148279_2_+32+2,p_148279_3_+12+10*j2,8421504);
		}
	}
}
