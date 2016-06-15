package chylex.respack.packs;
import java.io.File;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import chylex.respack.gui.GuiCustomResourcePacks;
import chylex.respack.gui.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourcePackListEntryFolder extends ResourcePackListEntryCustom{
	private static final ResourceLocation folderResource = new ResourceLocation("betterresourcepacks:textures/gui/folder.png"); // http://www.iconspedia.com/icon/folion-icon-27237.html
	
	private final GuiCustomResourcePacks ownerScreen;
	
	public final File folder;
	public final String folderName;
	public final boolean isUp;
	
	public ResourcePackListEntryFolder(GuiCustomResourcePacks ownerScreen, File folder){
		super(ownerScreen);
		this.ownerScreen = ownerScreen;
		this.folder = folder;
		this.folderName = folder.getName();
		this.isUp = false;
	}
	
	public ResourcePackListEntryFolder(GuiCustomResourcePacks ownerScreen, File folder, boolean isUp){
		super(ownerScreen);
		this.ownerScreen = ownerScreen;
		this.folder = folder;
		this.folderName = "..";
		this.isUp = isUp;
	}
	
	@Override
	public void func_148313_c(){
		field_148317_a.getTextureManager().bindTexture(folderResource);
	}
	
	@Override
	public String func_148312_b(){
		return folderName;
	}
	
	@Override
	public String func_148311_a(){
		return isUp ? "(Back)" : "(Folder)";
	}
	
	@Override
	public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_){
		ownerScreen.moveToFolder(folder);
		return true;
	}
	
	@Override
	public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_){
		GuiUtils.renderFolderEntry(this,p_148279_1_,p_148279_2_,p_148279_3_,p_148279_4_,p_148279_5_,p_148279_6_,p_148279_7_,p_148279_8_,p_148279_9_);
	}
}
