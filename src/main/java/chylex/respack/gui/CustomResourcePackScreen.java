package chylex.respack.gui;
import chylex.respack.packs.ResourcePackFolderListEntry;
import chylex.respack.packs.ResourcePackListProcessor;
import chylex.respack.repository.ResourcePackUtils;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList.ResourcePackEntry;
import net.minecraft.client.gui.widget.list.AvailableResourcePackList;
import net.minecraft.client.gui.widget.list.SelectedResourcePackList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import static chylex.respack.repository.ResourcePackUtils.wrap;

@OnlyIn(Dist.CLIENT)
public final class CustomResourcePackScreen extends ResourcePacksScreen{
	private final ResourcePackListProcessor listProcessor = new ResourcePackListProcessor(this::onFiltersUpdated);
	private Comparator<ResourcePackEntry> currentSorter;
	
	private AvailableResourcePackList originalAvailablePacks;
	private AvailableResourcePackListCustom customAvailablePacks;
	private TextFieldWidget searchField;
	
	private File currentFolder = Minecraft.getInstance().getFileResourcePacks();
	private boolean folderView = true;
	
	public CustomResourcePackScreen(Screen parentScreen, GameSettings settings){
		super(parentScreen, settings);
	}
	
	// Components
	
	@Override
	protected void init(){
		getMinecraft().keyboardListener.enableRepeatEvents(true);
		super.init();
		
		String openFolderText = I18n.format("resourcePack.openFolder");
		String doneText = I18n.format("gui.done");
		
		findButton(openFolderText).ifPresent(btn -> {
			btn.x = width / 2 + 25;
			btn.y = height - 48;
		});
		
		findButton(doneText).ifPresent(btn -> {
			btn.x = width / 2 + 25;
			btn.y = height - 26;
		});
		
		addButton(new Button(width / 2 - 204, height - 26, 30, 20, "A-Z", btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortAZ);
		}));
		
		addButton(new Button(width / 2 - 204 + 34, height - 26, 30, 20, "Z-A", btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortZA);
		}));
		
		addButton(new Button(width / 2 - 132, height - 26, 68, 20, folderView ? "Folder View" : "Flat View", btn -> {
			folderView = !folderView;
			btn.setMessage(folderView ? "Folder View" : "Flat View");
			
			onFiltersUpdated();
			customAvailablePacks.setScrollAmount(0.0);
		}));
		
		addButton(new Button(width / 2 - 56, height - 26, 52, 20, "Refresh", btn -> {
			CustomResourcePackScreen refreshed = new CustomResourcePackScreen(parentScreen, gameSettings);
			refreshed.currentSorter = currentSorter;
			refreshed.folderView = folderView;
			refreshed.listProcessor.pauseCallback();
			
			findButton(doneText).ifPresent(done -> done.onClick(-1, -1));
			getMinecraft().displayGuiScreen(refreshed);
			
			if (getMinecraft().currentScreen == refreshed){
				refreshed.searchField.setText(searchField.getText());
				
				if (currentFolder.exists() && notInRoot()){
					refreshed.moveToFolder(currentFolder);
				}
			}
			
			refreshed.listProcessor.resumeCallback();
		}));
		
		searchField = new TextFieldWidget(font, width / 2 - 203, height - 46, 198, 16, searchField, "");
		searchField.setCanLoseFocus(true);
		searchField.setResponder(listProcessor::setFilter);
		children.add(searchField);
		
		originalAvailablePacks = (AvailableResourcePackList)children.stream().filter(widget -> widget instanceof AvailableResourcePackList).findFirst().orElse(null);
		
		if (originalAvailablePacks == null){
			getMinecraft().displayGuiScreen(parentScreen);
			return;
		}
		
		children.remove(originalAvailablePacks);
		children.add(customAvailablePacks = new AvailableResourcePackListCustom(originalAvailablePacks));
		
		listProcessor.pauseCallback();
		listProcessor.setSorter(currentSorter == null ? (currentSorter = ResourcePackListProcessor.sortAZ) : currentSorter);
		listProcessor.setFilter(searchField.getText());
		listProcessor.resumeCallback();
	}
	
	private Optional<Widget> findButton(String text){
		return buttons.stream().filter(btn -> text.equals(btn.getMessage())).findFirst();
	}
	
	@Override
	public void markChanged(){
		super.markChanged();
		onFiltersUpdated();
	}
	
	// Processing
	
	private boolean notInRoot(){
		return folderView && !currentFolder.equals(getMinecraft().getFileResourcePacks());
	}
	
	private void onFiltersUpdated(){
		List<ResourcePackEntry> folders = null;
		
		if (folderView){
			folders = new ArrayList<>();
			
			if (notInRoot()){
				folders.add(new ResourcePackFolderListEntry(customAvailablePacks, this, currentFolder.getParentFile(), true));
			}
			
			for(File folder : wrap(currentFolder.listFiles(ResourcePackUtils::isFolderButNotFolderBasedPack))){
				folders.add(new ResourcePackFolderListEntry(customAvailablePacks, this, folder));
			}
		}
		
		listProcessor.apply(originalAvailablePacks.children(), folders, customAvailablePacks.children());
		
		if (folderView){
			customAvailablePacks.children().removeIf(entry -> {
				if (entry instanceof ResourcePackFolderListEntry){
					ResourcePackFolderListEntry folder = (ResourcePackFolderListEntry)entry;
					return !folder.isUp && !currentFolder.equals(folder.folder.getParentFile());
				}
				
				File file = ResourcePackUtils.determinePackFolder(entry.func_214418_e().getResourcePack());
				return file == null ? notInRoot() : !currentFolder.equals(file.getParentFile());
			});
		}
		
		customAvailablePacks.setScrollAmount(customAvailablePacks.getScrollAmount());
	}
	
	public void moveToFolder(File folder){
		currentFolder = folder;
		onFiltersUpdated();
		customAvailablePacks.setScrollAmount(0.0);
	}
	
	// UI Overrides
	
	@Override
	public void tick(){
		super.tick();
		searchField.tick();
	}
	
	@Override
	public void removed(){
		super.removed();
		getMinecraft().keyboardListener.enableRepeatEvents(false);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderDirtBackground(0);
		
		for(IGuiEventListener widget : children){
			if (widget instanceof SelectedResourcePackList){
				((IRenderable)widget).render(mouseX, mouseY, partialTicks);
				break;
			}
		}
		
		customAvailablePacks.render(mouseX, mouseY, partialTicks);
		searchField.render(mouseX, mouseY, partialTicks);
		
		for(Widget btn : buttons){
			btn.render(mouseX, mouseY, partialTicks);
		}
		
		drawCenteredString(font, title.getFormattedText(), width / 2, 16, 16777215);
	}
}
