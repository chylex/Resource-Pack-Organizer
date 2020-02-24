package chylex.respack.fabric.repository;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ZipResourcePack;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.Map;

public final class NestedFolderPackFinder implements ResourcePackProvider{
	public static void register(){
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getResourcePackManager().registerProvider(new NestedFolderPackFinder(mc.getResourcePackDir()));
	}
	
	private final File root;
	private final int rootLength;
	
	private NestedFolderPackFinder(File root){
		this.root = root;
		this.rootLength = root.getAbsolutePath().length();
	}
	
	@Override
	public <T extends ResourcePackProfile> void register(Map<String, T> registry, Factory<T> factory){
		File[] folders = root.listFiles(ResourcePackUtils::isFolderButNotFolderBasedPack);
		
		for(File folder : ResourcePackUtils.wrap(folders)){
			processFolder(folder, registry, factory);
		}
	}
	
	public <T extends ResourcePackProfile> void processFolder(File folder, Map<String, T> registry, Factory<T> factory){
		if (ResourcePackUtils.isFolderBasedPack(folder)){
			addPack(folder, registry, factory);
			return;
		}
		
		File[] zipFiles = folder.listFiles(file -> file.isFile() && file.getName().endsWith(".zip"));
		
		for(File zipFile : ResourcePackUtils.wrap(zipFiles)){
			addPack(zipFile, registry, factory);
		}
		
		File[] nestedFolders = folder.listFiles(File::isDirectory);
		
		for(File nestedFolder : ResourcePackUtils.wrap(nestedFolders)){
			processFolder(nestedFolder, registry, factory);
		}
	}
	
	public <T extends ResourcePackProfile> void addPack(File fileOrFolder, Map<String, T> registry, Factory<T> factory){
		String name = "file/" + StringUtils.removeStart(fileOrFolder.getAbsolutePath().substring(rootLength).replace('\\', '/'), "/");
		T info;
		
		if (fileOrFolder.isDirectory()){
			info = ResourcePackProfile.of(name, false, () -> new DirectoryResourcePack(fileOrFolder), factory, InsertionPosition.TOP);
		}
		else{
			info = ResourcePackProfile.of(name, false, () -> new ZipResourcePack(fileOrFolder), factory, InsertionPosition.TOP);
		}
		
		if (info != null){
			registry.put(name, info);
		}
	}
}
