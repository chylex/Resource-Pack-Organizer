package chylex.respack.packs;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ResourcePackFolder extends ClientResourcePackInfo{
	private static final ResourceLocation folderResource = new ResourceLocation("resourcepackorganizer:textures/gui/folder.png"); // http://www.iconspedia.com/icon/folion-icon-27237.html
	
	public ResourcePackFolder(String name, ITextComponent title, ITextComponent description){
		super(name, true, () -> null, title, description, PackCompatibility.COMPATIBLE, Priority.TOP, true, null, false);
	}
	
	@Override
	public void func_195808_a(TextureManager textureManager){
		textureManager.bindTexture(folderResource);
	}
}
