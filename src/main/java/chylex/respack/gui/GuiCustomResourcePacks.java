package chylex.respack.gui;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import chylex.respack.ResourcePackOrganizer;
import chylex.respack.packs.ResourcePackListEntryFolder;
import chylex.respack.packs.ResourcePackListProcessor;
import chylex.respack.render.RenderPackListOverlay;
import chylex.respack.repository.ResourcePackRepositoryCustom;
import com.google.common.collect.Lists;

@SideOnly(Side.CLIENT)
public class GuiCustomResourcePacks extends GuiScreenResourcePacks{
	private final GuiScreen parentScreen;
	
	private GuiTextField searchField;
    private GuiResourcePackAvailable guiPacksAvailable;
    private GuiResourcePackSelected guiPacksSelected;
    private List<ResourcePackListEntry> listPacksAvailable, listPacksAvailableProcessed, listPacksDummy;
    private List<ResourcePackListEntry> listPacksSelected;
    private ResourcePackListProcessor listProcessor;
    
    private File currentFolder;
    private GuiButton selectedButton;
    private boolean hasUpdated;
	
	public GuiCustomResourcePacks(GuiScreen parentScreen){
		super(parentScreen);
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui(){
		Keyboard.enableRepeatEvents(true);
		
		buttonList.add(new GuiOptionButton(1,width/2+100-75,height-26,I18n.format("gui.done")));
		buttonList.add(new GuiOptionButton(2,width/2+100-75,height-48,I18n.format("resourcePack.openFolder")));
		
		buttonList.add(new GuiOptionButton(10,width/2-204,height-26,40,20,"A-Z"));
		buttonList.add(new GuiOptionButton(11,width/2-204+44,height-26,40,20,"Z-A"));
		buttonList.add(new GuiOptionButton(20,width/2-74,height-26,70,20,"Refresh"));
		
		searchField = new GuiTextField(30,fontRendererObj,width/2-203,height-46,198,16);

		listPacksAvailable = Lists.newArrayListWithCapacity(8);
		listPacksAvailableProcessed = Lists.newArrayListWithCapacity(8);
		listPacksDummy = Lists.newArrayListWithCapacity(1);
		listPacksSelected = Lists.newArrayListWithCapacity(8);
		
		ResourcePackRepository repository = mc.getResourcePackRepository();
		repository.updateRepositoryEntriesAll();
		
		currentFolder = repository.getDirResourcepacks();
		listPacksAvailable.addAll(createAvailablePackList(repository));
        
        for(Entry entry:Lists.reverse(repository.getRepositoryEntries())){
        	listPacksSelected.add(new ResourcePackListEntryFound(this,entry));
        }
        
        listPacksSelected.add(new ResourcePackListEntryDefault(this));
		
		guiPacksAvailable = new GuiResourcePackAvailable(mc,200,height,listPacksAvailableProcessed);
		guiPacksAvailable.setSlotXBoundsFromLeft(width/2-204);
		guiPacksAvailable.registerScrollButtons(7,8);
		guiPacksAvailable.top = 4;
		
		guiPacksSelected = new GuiResourcePackSelected(mc,200,height,listPacksSelected);
		guiPacksSelected.setSlotXBoundsFromLeft(width/2+4);
		guiPacksSelected.registerScrollButtons(7,8);
		guiPacksSelected.top = 4;
		
		listProcessor = new ResourcePackListProcessor(listPacksAvailable,listPacksAvailableProcessed);
		listProcessor.setSorter(ResourcePackListProcessor.sortAZ);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 20){
			refreshAvailablePacks();
		}
		else if (button.id == 11){
			listProcessor.setSorter(ResourcePackListProcessor.sortZA);
		}
		else if (button.id == 10){
			listProcessor.setSorter(ResourcePackListProcessor.sortAZ);
		}
		else if (button.id == 2){
			GuiUtils.openFolder(mc.getResourcePackRepository().getDirResourcepacks());
		}
		else if (button.id == 1){
			List<Entry> selected = refreshSelectedPacks();
			mc.gameSettings.resourcePacks.clear();
			
			for(Entry entry:selected){
				mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
			}
			
			mc.gameSettings.saveOptions();
			mc.refreshResources();
			
			ResourcePackOrganizer.getConfig().options.updateEnabledPacks();
			RenderPackListOverlay.refreshPackNames();
			
			mc.displayGuiScreen(parentScreen);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 0){
			for(GuiButton button:buttonList){
				if (button.mousePressed(mc,mouseX,mouseY)){
					selectedButton = button;
					button.playPressSound(mc.getSoundHandler());
					actionPerformed(button);
				}
			}
		}
		
		guiPacksAvailable.mouseClicked(mouseX,mouseY,buttonId);
		guiPacksSelected.mouseClicked(mouseX,mouseY,buttonId);
		searchField.mouseClicked(mouseX,mouseY,buttonId);
		
		listProcessor.refresh();
	}
	
	@Override
	public void handleMouseInput() throws IOException{
		try{
			super.handleMouseInput();
		}catch(NullPointerException e){
			// calls super.handleMouseInput and then attempts to use selectedResourcePacksList and availableResourcePacksList which are null
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int eventType){
		if (eventType == 0 && selectedButton != null){
			selectedButton.mouseReleased(mouseX,mouseY);
			selectedButton = null;
		}
	}
	
	@Override
	protected void keyTyped(char keyChar, int keyCode) throws IOException{
		super.keyTyped(keyChar,keyCode);
		
		if (searchField.isFocused()){
			searchField.textboxKeyTyped(keyChar,keyCode);
			listProcessor.setFilter(searchField.getText().trim());
		}
	}
	
	@Override
	public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen(){
		searchField.updateCursorCounter();
		
		if (hasUpdated){
			hasUpdated = false;
			refreshSelectedPacks();
			refreshAvailablePacks();
		}
	}
	
	public void moveToFolder(File folder){
		currentFolder = folder;
		refreshSelectedPacks();
		refreshAvailablePacks();
	}
	
	public void refreshAvailablePacks(){
		listPacksAvailable.clear();
		listPacksAvailable.addAll(createAvailablePackList(mc.getResourcePackRepository()));
		listProcessor.refresh();
	}
	
	public List<Entry> refreshSelectedPacks(){
		List<Entry> selected = Lists.newArrayListWithCapacity(listPacksSelected.size());
		
		for(ResourcePackListEntry entry:listPacksSelected){
			if (!(entry instanceof ResourcePackListEntryFound))continue;
			
			ResourcePackListEntryFound packEntry = (ResourcePackListEntryFound)entry;
			
			if (packEntry.getResourcePackEntry() != null){
				selected.add(packEntry.getResourcePackEntry());
			}
		}
		
		Collections.reverse(selected);
		
		mc.getResourcePackRepository().setRepositories(selected);
		return selected;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawBackground(0);
		guiPacksAvailable.drawScreen(mouseX,mouseY,partialTickTime);
		guiPacksSelected.drawScreen(mouseX,mouseY,partialTickTime);
		searchField.drawTextBox();
		
		for(GuiButton button:buttonList){
			button.drawButton(mc,mouseX,mouseY);
		}
	}
	private List<ResourcePackListEntryFound> createAvailablePackList(ResourcePackRepository repository){
		final List<ResourcePackListEntryFound> list = Lists.newArrayList();
		
		if (!repository.getDirResourcepacks().equals(currentFolder)){
			list.add(new ResourcePackListEntryFolder(this,currentFolder.getParentFile(),true));
		}
		
		final File[] files = currentFolder.listFiles();
		
		if (files != null){
			for(File file:files){
				if (file.isDirectory() && !new File(file,"pack.mcmeta").isFile()){
					list.add(new ResourcePackListEntryFolder(this,file));
				}
				else{
					Entry entry = ResourcePackRepositoryCustom.createEntryInstance(repository,file);
					
					if (entry != null){
						try{
							entry.updateResourcePack();
							list.add(new ResourcePackListEntryFound(this,entry));
						}catch(Exception e){}
					}
				}
			}
		}
		
		List<Entry> repositoryEntries = repository.getRepositoryEntries();
		
		for(Iterator<ResourcePackListEntryFound> iter = list.iterator(); iter.hasNext();){
			ResourcePackListEntryFound listEntry = iter.next();
			
			if (listEntry.getResourcePackEntry() != null && repositoryEntries.contains(listEntry.getResourcePackEntry())){
				iter.remove();
			}
		}
		
		return list;
	}
	
	// OVERRIDES FROM GuiScreenResourcePacks
	
	@Override
	public boolean hasResourcePackEntry(ResourcePackListEntry entry){
		return listPacksSelected.contains(entry);
	}
	
	@Override
	public List getListContaining(ResourcePackListEntry entry){
		return hasResourcePackEntry(entry) ? listPacksSelected : listPacksAvailable;
	}
	
	@Override
	public List<ResourcePackListEntry> getAvailableResourcePacks(){
		hasUpdated = true;
		listPacksDummy.clear();
		return listPacksDummy;
	}
	
	@Override
	public List<ResourcePackListEntry> getSelectedResourcePacks(){
		hasUpdated = true;
		return listPacksSelected;
	}
}
