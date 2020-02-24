package chylex.respack.fabric.packs;
import chylex.respack.fabric.gui.CustomResourcePackScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget.ResourcePackEntry;
import net.minecraft.text.LiteralText;
import java.io.File;
import java.util.List;

public final class ResourcePackFolderListEntry extends ResourcePackEntry{
	public static final String upText = "..";
	
	private final CustomResourcePackScreen ownerScreen;
	public final File folder;
	public final boolean isUp;
	
	public ResourcePackFolderListEntry(ResourcePackListWidget list, CustomResourcePackScreen ownerScreen, File folder, boolean isUp){
		super(list, ownerScreen, new ResourcePackFolder("RPO/" + folder.getAbsolutePath(), new LiteralText(isUp ? upText : folder.getName()), new LiteralText(isUp ? "(Back)" : "(Folder)")));
		this.ownerScreen = ownerScreen;
		this.folder = folder;
		this.isUp = isUp;
	}
	
	public ResourcePackFolderListEntry(ResourcePackListWidget list, CustomResourcePackScreen ownerScreen, File folder){
		this(list, ownerScreen, folder, false);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		ownerScreen.moveToFolder(folder);
		return true;
	}
	
	@Override
	public void render(int index, int y, int x, int w, int h, int parentX, int parentY, boolean isMouseOver, float partialTicks){
		drawIcon();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		DrawableHelper.blit(x, y, 0F, 0F, 32, 32, 32, 32);
		
		String title = getDisplayName();
		String desc = getDescription();
		
		if (client.options.touchscreen || isMouseOver){
			DrawableHelper.fill(x, y, x + 32, y + 32, -1601138544);
			RenderSystem.color4f(1F, 1F, 1F, 1F);
		}
		
		TextRenderer fontRenderer = client.textRenderer;
		int titleWidth = fontRenderer.getStringWidth(title);
		
		if (titleWidth > 157){
			title = fontRenderer.trimToWidth(title, 157 - fontRenderer.getStringWidth("...")) + "...";
		}
		
		fontRenderer.drawWithShadow(title, x + 32 + 2, y + 1, 16777215);
		List<String> lines = fontRenderer.wrapStringToWidthAsList(desc, 157);
		
		for(int line = 0; line < 2 && line < lines.size(); ++line){
			fontRenderer.drawWithShadow(lines.get(line), x + 32 + 2, y + 12 + 10 * line, 8421504);
		}
	}
}
