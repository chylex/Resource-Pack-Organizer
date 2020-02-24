package chylex.respack.fabric.gui;
import chylex.respack.fabric.packs.ResourcePackListProcessor;
import chylex.respack.fabric.packs.ResourcePackFolderListEntry;
import chylex.respack.fabric.repository.ResourcePackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget.ResourcePackEntry;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import static chylex.respack.fabric.repository.ResourcePackUtils.wrap;

public final class CustomResourcePackScreen extends ResourcePackOptionsScreen{
	private final MinecraftClient minecraft = MinecraftClient.getInstance();
	private final ResourcePackListProcessor listProcessor = new ResourcePackListProcessor(this::onFiltersUpdated);
	private Comparator<ResourcePackEntry> currentSorter;
	
	private AvailableResourcePackListWidget originalAvailablePacks;
	private AvailableResourcePackListCustom customAvailablePacks;
	private TextFieldWidget searchField;
	
	private File currentFolder = minecraft.getResourcePackDir();
	private boolean folderView = true;
	
	public CustomResourcePackScreen(Screen parentScreen, GameOptions settings){
		super(parentScreen, settings);
	}
	
	// Components
	
	@Override
	protected void init(){
		minecraft.keyboard.enableRepeatEvents(true);
		super.init();
		
		String openFolderText = I18n.translate("resourcePack.openFolder");
		String doneText = I18n.translate("gui.done");
		
		findButton(openFolderText).ifPresent(btn -> {
			btn.x = width / 2 + 25;
			btn.y = height - 48;
		});
		
		findButton(doneText).ifPresent(btn -> {
			btn.x = width / 2 + 25;
			btn.y = height - 26;
		});
		
		addButton(new ButtonWidget(width / 2 - 204, height - 26, 30, 20, "A-Z", btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortAZ);
		}));
		
		addButton(new ButtonWidget(width / 2 - 204 + 34, height - 26, 30, 20, "Z-A", btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortZA);
		}));
		
		addButton(new ButtonWidget(width / 2 - 132, height - 26, 68, 20, folderView ? "Folder View" : "Flat View", btn -> {
			folderView = !folderView;
			btn.setMessage(folderView ? "Folder View" : "Flat View");
			
			onFiltersUpdated();
			customAvailablePacks.setScrollAmount(0.0);
		}));
		
		addButton(new ButtonWidget(width / 2 - 56, height - 26, 52, 20, "Refresh", btn -> {
			CustomResourcePackScreen refreshed = new CustomResourcePackScreen(parent, gameOptions);
			refreshed.currentSorter = currentSorter;
			refreshed.folderView = folderView;
			refreshed.listProcessor.pauseCallback();
			
			findButton(doneText).ifPresent(done -> done.onClick(-1, -1));
			minecraft.openScreen(refreshed);
			
			if (minecraft.currentScreen == refreshed){
				refreshed.searchField.setText(searchField.getText());
				
				if (currentFolder.exists() && notInRoot()){
					refreshed.moveToFolder(currentFolder);
				}
			}
			
			refreshed.listProcessor.resumeCallback();
		}));
		
		searchField = new TextFieldWidget(font, width / 2 - 203, height - 46, 198, 16, searchField, "");
		searchField.setFocusUnlocked(true);
		searchField.setChangedListener(listProcessor::setFilter);
		children.add(searchField);
		
		originalAvailablePacks = (AvailableResourcePackListWidget)children.stream().filter(widget -> widget instanceof AvailableResourcePackListWidget).findFirst().orElse(null);
		
		if (originalAvailablePacks == null){
			minecraft.openScreen(parent);
			return;
		}
		
		children.remove(originalAvailablePacks);
		children.add(customAvailablePacks = new AvailableResourcePackListCustom(originalAvailablePacks, 200, height, width / 2 - 204));
		
		listProcessor.pauseCallback();
		listProcessor.setSorter(currentSorter == null ? (currentSorter = ResourcePackListProcessor.sortAZ) : currentSorter);
		listProcessor.setFilter(searchField.getText());
		listProcessor.resumeCallback();
	}
	
	private Optional<AbstractButtonWidget> findButton(String text){
		return buttons.stream().filter(btn -> text.equals(btn.getMessage())).findFirst();
	}
	
	@Override
	public void markDirty(){
		super.markDirty();
		onFiltersUpdated();
	}
	
	// Processing
	
	private boolean notInRoot(){
		return folderView && !currentFolder.equals(minecraft.getResourcePackDir());
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
				
				File file = ResourcePackUtils.determinePackFolder(entry.getPack().createResourcePack());
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
		minecraft.keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderDirtBackground(0);
		
		for(Element widget : children){
			if (widget instanceof SelectedResourcePackListWidget){
				((SelectedResourcePackListWidget)widget).render(mouseX, mouseY, partialTicks);
				break;
			}
		}
		
		customAvailablePacks.render(mouseX, mouseY, partialTicks);
		searchField.render(mouseX, mouseY, partialTicks);
		
		for(AbstractButtonWidget btn : buttons){
			btn.render(mouseX, mouseY, partialTicks);
		}
		
		drawCenteredString(font, title.asFormattedString(), width / 2, 16, 16777215);
	}
}
