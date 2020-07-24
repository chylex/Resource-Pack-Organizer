package chylex.respack.gui;
import chylex.respack.packs.ResourcePackFolderListEntry;
import chylex.respack.packs.ResourcePackListProcessor;
import chylex.respack.repository.ResourcePackUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.PackLoadingManager;
import net.minecraft.client.gui.screen.PackLoadingManager.AbstractPack;
import net.minecraft.client.gui.screen.PackLoadingManager.IPack;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.client.gui.widget.list.ResourcePackList.ResourcePackEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import static chylex.respack.repository.ResourcePackUtils.wrap;

@OnlyIn(Dist.CLIENT)
public final class CustomResourcePackScreen extends PackScreen{
	private static final ITextComponent TEXT_AZ = new StringTextComponent("A-Z");
	private static final ITextComponent TEXT_ZA = new StringTextComponent("Z-A");
	private static final ITextComponent TEXT_FOLDER_VIEW = new StringTextComponent("Folder View");
	private static final ITextComponent TEXT_FLAT_VIEW = new StringTextComponent("Flat View");
	private static final ITextComponent TEXT_REFRESH = new StringTextComponent("Refresh");
	
	private static Function<Runnable, PackLoadingManager<?>> patchPackLoadingManager(final ResourcePacksScreen original){
		return runnable -> {
			final PackLoadingManager<?> manager = original.field_238887_q_;
			
			manager.field_238863_d_ = () -> {
				runnable.run();
				
				final Screen screen = Minecraft.getInstance().currentScreen;
				
				if (screen instanceof CustomResourcePackScreen){
					((CustomResourcePackScreen)screen).onFiltersUpdated();
				}
			};
			
			return manager;
		};
	}
	
	// Instance
	
	private final ResourcePackListProcessor listProcessor = new ResourcePackListProcessor(this::onFiltersUpdated);
	private Comparator<ResourcePackEntry> currentSorter;
	
	private final ResourcePacksScreen originalScreen;
	private ResourcePackList originalAvailablePacks;
	private AvailableResourcePackListCustom customAvailablePacks;
	private TextFieldWidget searchField;
	
	private File currentFolder = Minecraft.getInstance().getFileResourcePacks();
	private boolean folderView = true;
	
	public CustomResourcePackScreen(final ResourcePacksScreen original){
		super(original.field_238888_r_, (TranslationTextComponent)original.getTitle(), patchPackLoadingManager(original), original.field_241817_w_);
		this.originalScreen = original;
	}
	
	// Components
	
	@Override
	protected void init(){
		getMinecraft().keyboardListener.enableRepeatEvents(true);
		super.init();
		
		final ITextComponent openFolderText = new TranslationTextComponent("pack.openFolder");
		final ITextComponent doneText = new TranslationTextComponent("gui.done");
		
		findButton(openFolderText).ifPresent(btn -> {
			btn.x = width / 2 + 25;
			btn.y = height - 48;
		});
		
		findButton(doneText).ifPresent(btn -> {
			btn.x = width / 2 + 25;
			btn.y = height - 26;
		});
		
		addButton(new Button(width / 2 - 204, height - 26, 30, 20, TEXT_AZ, btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortAZ);
		}));
		
		addButton(new Button(width / 2 - 204 + 34, height - 26, 30, 20, TEXT_ZA, btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortZA);
		}));
		
		addButton(new Button(width / 2 - 132, height - 26, 68, 20, folderView ? TEXT_FOLDER_VIEW : TEXT_FLAT_VIEW, btn -> {
			folderView = !folderView;
			btn.setMessage(folderView ? TEXT_FOLDER_VIEW : TEXT_FLAT_VIEW);
			
			onFiltersUpdated();
			customAvailablePacks.setScrollAmount(0.0);
		}));
		
		addButton(new Button(width / 2 - 56, height - 26, 52, 20, TEXT_REFRESH, btn -> {
			final CustomResourcePackScreen refreshed = new CustomResourcePackScreen(originalScreen);
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
		
		searchField = new TextFieldWidget(font, width / 2 - 203, height - 46, 198, 16, searchField, StringTextComponent.EMPTY);
		searchField.setCanLoseFocus(true);
		searchField.setResponder(listProcessor::setFilter);
		children.add(searchField);
		
		originalAvailablePacks = field_238891_u_;
		
		children.remove(originalAvailablePacks);
		children.add(customAvailablePacks = new AvailableResourcePackListCustom(originalAvailablePacks));
		
		listProcessor.pauseCallback();
		listProcessor.setSorter(currentSorter == null ? (currentSorter = ResourcePackListProcessor.sortAZ) : currentSorter);
		listProcessor.setFilter(searchField.getText());
		listProcessor.resumeCallback();
	}
	
	private Optional<Widget> findButton(final ITextComponent text){
		return buttons.stream().filter(btn -> text.equals(btn.getMessage())).findFirst();
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
			
			for(final File folder : wrap(currentFolder.listFiles(ResourcePackUtils::isFolderButNotFolderBasedPack))){
				folders.add(new ResourcePackFolderListEntry(customAvailablePacks, this, folder));
			}
		}
		
		listProcessor.apply(originalAvailablePacks.children(), folders, customAvailablePacks.children());
		
		if (folderView){
			customAvailablePacks.children().removeIf(entry -> {
				if (entry instanceof ResourcePackFolderListEntry){
					final ResourcePackFolderListEntry folder = (ResourcePackFolderListEntry)entry;
					return !folder.isUp && !currentFolder.equals(folder.folder.getParentFile());
				}
				
				File file = null;
				final IPack pack = entry.field_214431_d;
				
				if (pack instanceof PackLoadingManager.AbstractPack){
					file = ResourcePackUtils.determinePackFolder(((AbstractPack)pack).field_238878_b_.getResourcePack());
				}
				
				return file == null ? notInRoot() : !currentFolder.equals(file.getParentFile());
			});
		}
		
		customAvailablePacks.setScrollAmount(customAvailablePacks.getScrollAmount());
	}
	
	public void moveToFolder(final File folder){
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
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks){
		renderDirtBackground(0);
		
		field_238892_v_.render(matrix, mouseX, mouseY, partialTicks);
		customAvailablePacks.render(matrix, mouseX, mouseY, partialTicks);
		searchField.render(matrix, mouseX, mouseY, partialTicks);
		
		for(final Widget btn : buttons){
			btn.render(matrix, mouseX, mouseY, partialTicks);
		}
		
		drawCenteredString(matrix, font, title, width / 2, 16, 16777215);
	}
}
