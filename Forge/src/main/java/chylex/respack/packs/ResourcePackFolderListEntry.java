package chylex.respack.packs;
import chylex.respack.gui.CustomResourcePackScreen;
import chylex.respack.packs.ResourcePackFolder.PackEntry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.PackLoadingManager.IPack;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.client.gui.widget.list.ResourcePackList.ResourcePackEntry;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.io.File;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ResourcePackFolderListEntry extends ResourcePackEntry{
	public static final String UP_TEXT = "..";
	
	private static IPack getPack(final File folder, final boolean isUp){
		final ITextComponent title = new StringTextComponent(isUp ? UP_TEXT : folder.getName());
		final ITextComponent description = new StringTextComponent(isUp ? "(Back)" : "(Folder)");
		final ResourcePackFolder entry = new ResourcePackFolder("RPO/" + folder.getAbsolutePath(), title, description);
		return new PackEntry(entry);
	}
	
	private final CustomResourcePackScreen ownerScreen;
	public final File folder;
	public final boolean isUp;
	
	public ResourcePackFolderListEntry(final ResourcePackList list, final CustomResourcePackScreen ownerScreen, final File folder, final boolean isUp){
		super(Minecraft.getInstance(), list, ownerScreen, getPack(folder, isUp));
		this.ownerScreen = ownerScreen;
		this.folder = folder;
		this.isUp = isUp;
	}
	
	public ResourcePackFolderListEntry(final ResourcePackList list, final CustomResourcePackScreen ownerScreen, final File folder){
		this(list, ownerScreen, folder, false);
	}
	
	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int button){
		ownerScreen.moveToFolder(folder);
		return true;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int index, final int y, final int x, final int w, final int h, final int mouseX, final int mouseY, final boolean isMouseOver, final float partialTicks){
		field_214428_a.textureManager.bindTexture(field_214431_d.func_241868_a());
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		AbstractGui.blit(matrix, x, y, 0F, 0F, 32, 32, 32, 32);
		
		final ITextComponent title = field_214431_d.func_230462_b_();
		final ITextProperties desc = field_214431_d.func_230463_c_();
		
		if (field_214428_a.gameSettings.touchscreen || isMouseOver){
			AbstractGui.fill(matrix, x, y, x + 32, y + 32, -1601138544);
			RenderSystem.color4f(1F, 1F, 1F, 1F);
		}
		
		final FontRenderer fontRenderer = field_214428_a.fontRenderer;
		final int titleWidth = fontRenderer.func_238414_a_(title);
		
		if (titleWidth > 157){
			final ITextProperties shortenedTitle = ITextProperties.func_240655_a_(fontRenderer.func_238417_a_(title, 157 - fontRenderer.getStringWidth("...")), ITextProperties.func_240652_a_("..."));
			fontRenderer.func_238407_a_(matrix, LanguageMap.getInstance().func_241870_a(shortenedTitle), x + 32 + 2, y + 1, 16777215);
		}
		else{
			fontRenderer.func_238407_a_(matrix, title.func_241878_f(), x + 32 + 2, y + 1, 16777215);
		}
		
		final List<IReorderingProcessor> lines = fontRenderer.func_238425_b_(desc, 157);
		
		for(int line = 0; line < 2 && line < lines.size(); ++line){
			fontRenderer.func_238407_a_(matrix, lines.get(line), x + 32 + 2, y + 12 + 10 * line, 8421504);
		}
	}
}
