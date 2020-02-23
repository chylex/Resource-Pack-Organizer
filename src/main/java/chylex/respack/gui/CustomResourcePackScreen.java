package chylex.respack.gui;
import chylex.respack.packs.ResourcePackListProcessor;
import net.minecraft.client.GameSettings;
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
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class CustomResourcePackScreen extends ResourcePacksScreen{
	private ResourcePackListProcessor listProcessor;
	private Comparator<ResourcePackEntry> currentSorter;
	
	private AvailableResourcePackList originalAvailablePacks;
	private AvailableResourcePackListCustom customAvailablePacks;
	private TextFieldWidget searchField;
	
	public CustomResourcePackScreen(Screen parentScreen, GameSettings settings){
		super(parentScreen, settings);
	}
	
	// Components
	
	@Override
	protected void init(){
		getMinecraft().keyboardListener.enableRepeatEvents(false);
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
		
		addButton(new Button(width / 2 - 204, height - 26, 40, 20, "A-Z", btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortAZ);
		}));
		
		addButton(new Button(width / 2 - 204 + 44, height - 26, 40, 20, "Z-A", btn -> {
			listProcessor.setSorter(currentSorter = ResourcePackListProcessor.sortZA);
		}));
		
		addButton(new Button(width / 2 - 74, height - 26, 70, 20, "Refresh", btn -> {
			CustomResourcePackScreen refreshed = new CustomResourcePackScreen(parentScreen, gameSettings);
			refreshed.currentSorter = currentSorter;
			
			findButton(doneText).ifPresent(done -> done.onClick(-1, -1));
			getMinecraft().displayGuiScreen(refreshed);
			
			if (getMinecraft().currentScreen == refreshed){
				refreshed.searchField.setText(searchField.getText());
			}
		}));
		
		searchField = new TextFieldWidget(font, width / 2 - 203, height - 46, 198, 16, searchField, "");
		searchField.setCanLoseFocus(true);
		children.add(searchField);
		
		originalAvailablePacks = (AvailableResourcePackList)children.stream().filter(widget -> widget instanceof AvailableResourcePackList).findFirst().orElse(null);
		
		if (originalAvailablePacks == null){
			getMinecraft().displayGuiScreen(parentScreen);
			return;
		}
		
		children.remove(originalAvailablePacks);
		children.add(customAvailablePacks = new AvailableResourcePackListCustom(originalAvailablePacks));
		refreshListProcessor();
	}
	
	private Optional<Widget> findButton(String text){
		return buttons.stream().filter(btn -> text.equals(btn.getMessage())).findFirst();
	}
	
	private void refreshListProcessor(){
		listProcessor = new ResourcePackListProcessor(new ArrayList<>(originalAvailablePacks.children()), customAvailablePacks.children());
		listProcessor.setSorter(currentSorter == null ? (currentSorter = ResourcePackListProcessor.sortAZ) : currentSorter);
		listProcessor.setFilter(searchField.getText().trim());
		searchField.setResponder(listProcessor::setFilter);
	}
	
	@Override
	public void markChanged(){
		super.markChanged();
		refreshListProcessor();
	}
	
	// Public
	
	public void moveToFolder(File folder){
		// TODO
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
