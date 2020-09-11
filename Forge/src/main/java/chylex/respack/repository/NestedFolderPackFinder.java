package chylex.respack.repository;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.ResourcePackInfo.Priority;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.function.Consumer;
import static chylex.respack.repository.ResourcePackUtils.isFolderBasedPack;
import static chylex.respack.repository.ResourcePackUtils.wrap;

public final class NestedFolderPackFinder implements IPackFinder{
	public static void register(){
		final Minecraft mc = Minecraft.getInstance();
		mc.getResourcePackList().addPackFinder(new NestedFolderPackFinder(mc.getFileResourcePacks()));
	}
	
	private final File root;
	private final int rootLength;
	
	private NestedFolderPackFinder(final File root){
		this.root = root;
		this.rootLength = root.getAbsolutePath().length();
	}
	
	@Override
	public void func_230230_a_(final Consumer<ResourcePackInfo> packInfoConsumer, final IFactory packInfoFactory){
		final File[] folders = root.listFiles(ResourcePackUtils::isFolderButNotFolderBasedPack);
		
		for(final File folder : wrap(folders)){
			processFolder(folder, packInfoConsumer, packInfoFactory);
		}
	}
	
	private <T extends ResourcePackInfo> void processFolder(final File folder, final Consumer<ResourcePackInfo> packInfoConsumer, final IFactory packInfoFactory){
		if (isFolderBasedPack(folder)){
			addPack(folder, packInfoConsumer, packInfoFactory);
			return;
		}
		
		final File[] zipFiles = folder.listFiles(file -> file.isFile() && file.getName().endsWith(".zip"));
		
		for(final File zipFile : wrap(zipFiles)){
			addPack(zipFile, packInfoConsumer, packInfoFactory);
		}
		
		final File[] nestedFolders = folder.listFiles(File::isDirectory);
		
		for(final File nestedFolder : wrap(nestedFolders)){
			processFolder(nestedFolder, packInfoConsumer, packInfoFactory);
		}
	}
	
	private void addPack(final File fileOrFolder, final Consumer<ResourcePackInfo> packInfoConsumer, final IFactory packInfoFactory){
		final String name = "file/" + StringUtils.removeStart(fileOrFolder.getAbsolutePath().substring(rootLength).replace('\\', '/'), "/");
		final ResourcePackInfo info;
		
		if (fileOrFolder.isDirectory()){
			info = ResourcePackInfo.createResourcePack(name, false, () -> new FolderPack(fileOrFolder), packInfoFactory, Priority.TOP, IPackNameDecorator.field_232625_a_);
		}
		else{
			info = ResourcePackInfo.createResourcePack(name, false, () -> new FilePack(fileOrFolder), packInfoFactory, Priority.TOP, IPackNameDecorator.field_232625_a_);
		}
		
		if (info != null){
			packInfoConsumer.accept(info);
		}
	}
}
