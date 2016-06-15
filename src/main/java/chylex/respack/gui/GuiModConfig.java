package chylex.respack.gui;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import chylex.respack.ResourcePackOrganizer;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class GuiModConfig implements IModGuiFactory{
	@Override
	public void initialize(Minecraft mc){}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass(){
		return GuiModConfigInner.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static final class GuiModConfigInner extends GuiConfig{
		public GuiModConfigInner(GuiScreen parent){
			super(parent,ResourcePackOrganizer.getConfig().getGuiConfigElements(),ResourcePackOrganizer.MODID,false,false,ResourcePackOrganizer.getConfig().getGuiConfigString());
		}
	}
}