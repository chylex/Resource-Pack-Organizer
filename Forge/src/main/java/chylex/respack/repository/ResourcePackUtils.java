package chylex.respack.repository;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.LegacyResourcePackWrapperV4;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import java.io.File;

public final class ResourcePackUtils{
	private static final File[] EMPTY_FILE_ARRAY = new File[0];
	
	public static File[] wrap(File[] filesOrNull){
		return filesOrNull == null ? EMPTY_FILE_ARRAY : filesOrNull;
	}
	
	public static boolean isFolderBasedPack(File folder){
		return new File(folder, "pack.mcmeta").exists();
	}
	
	public static boolean isFolderButNotFolderBasedPack(File folder){
		return folder.isDirectory() && !isFolderBasedPack(folder);
	}
	
	public static File determinePackFolder(IResourcePack pack){
		Class<? extends IResourcePack> cls = pack.getClass();
		
		if (cls == FilePack.class || cls == FolderPack.class){
			return ((ResourcePack)pack).file;
		}
		else if (pack instanceof LegacyResourcePackWrapper){
			return determinePackFolder(((LegacyResourcePackWrapper)pack).locationMap);
		}
		else if (pack instanceof LegacyResourcePackWrapperV4){
			return determinePackFolder(((LegacyResourcePackWrapperV4)pack).resourcePack);
		}
		else{
			return null;
		}
	}
}
