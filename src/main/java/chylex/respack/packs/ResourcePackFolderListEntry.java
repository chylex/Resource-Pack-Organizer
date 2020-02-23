package chylex.respack.packs;
import chylex.respack.gui.CustomResourcePackScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList.ResourcePackEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.io.File;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ResourcePackFolderListEntry extends ResourcePackEntry{
	private static final ResourceLocation vanillaResource = new ResourceLocation("textures/gui/resource_packs.png");
	public static final String upText = "..";
	
	private final CustomResourcePackScreen ownerScreen;
	public final File folder;
	public final boolean isUp;
	
	public ResourcePackFolderListEntry(AbstractResourcePackList list, CustomResourcePackScreen ownerScreen, File folder, boolean isUp){
		super(list, ownerScreen, new ResourcePackFolder("RPO/" + folder.getAbsolutePath(), new StringTextComponent(isUp ? upText : folder.getName()), new StringTextComponent(isUp ? "(Back)" : "(Folder)")));
		this.ownerScreen = ownerScreen;
		this.folder = folder;
		this.isUp = isUp;
	}
	
	public ResourcePackFolderListEntry(AbstractResourcePackList list, CustomResourcePackScreen ownerScreen, File folder){
		this(list, ownerScreen, folder, false);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		ownerScreen.moveToFolder(folder);
		return true;
	}
	
	@Override
	public void render(int index, int y, int x, int w, int h, int parentX, int parentY, boolean isMouseOver, float partialTicks){
		func_214419_a();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		AbstractGui.blit(x, y, 0F, 0F, 32, 32, 32, 32);
		
		String title = func_214416_d();
		String desc = func_214420_c();
		
		if (field_214428_a.gameSettings.touchscreen || isMouseOver){
			AbstractGui.fill(x, y, x + 32, y + 32, -1601138544);
			RenderSystem.color4f(1F, 1F, 1F, 1F);
		}
		
		FontRenderer fontRenderer = field_214428_a.fontRenderer;
		int titleWidth = fontRenderer.getStringWidth(title);
		
		if (titleWidth > 157){
			title = fontRenderer.trimStringToWidth(title, 157 - fontRenderer.getStringWidth("...")) + "...";
		}
		
		fontRenderer.drawStringWithShadow(title, x + 32 + 2, y + 1, 16777215);
		List<String> lines = fontRenderer.listFormattedStringToWidth(desc, 157);
		
		for(int line = 0; line < 2 && line < lines.size(); ++line){
			fontRenderer.drawStringWithShadow(lines.get(line), x + 32 + 2, y + 12 + 10 * line, 8421504);
		}
	}
}
