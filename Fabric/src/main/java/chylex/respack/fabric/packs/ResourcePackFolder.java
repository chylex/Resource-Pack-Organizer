package chylex.respack.fabric.packs;
import chylex.respack.fabric.ResourcePackOrganizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.io.InputStream;

public final class ResourcePackFolder extends ClientResourcePackProfile{
	private static final Identifier folderResource = new Identifier("resourcepackorganizer:textures/gui/folder.png"); // http://www.iconspedia.com/icon/folion-icon-27237.html
	
	static{
		// for some reason the texture fails to load in the actual game
		try(InputStream stream = ResourcePackFolder.class.getResourceAsStream("/assets/resourcepackorganizer/textures/gui/folder.png")){
			MinecraftClient.getInstance().getTextureManager().registerTexture(folderResource, new NativeImageBackedTexture(NativeImage.read(stream)));
		}catch(IOException e){
			LogManager.getLogger(ResourcePackOrganizer.class).warn("Error loading folder texture:");
			e.printStackTrace();
		}
	}
	
	public ResourcePackFolder(String name, Text title, Text description){
		super(name, true, () -> null, title, description, ResourcePackCompatibility.COMPATIBLE, InsertionPosition.TOP, true, null);
	}
	
	@Override
	public void drawIcon(TextureManager manager){
		manager.bindTexture(folderResource);
	}
}
