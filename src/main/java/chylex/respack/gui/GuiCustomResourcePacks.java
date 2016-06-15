package chylex.respack.gui;
import java.io.File;
import java.lang.reflect.Constructor;
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
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import org.lwjgl.input.Keyboard;
import chylex.respack.packs.ResourcePackListEntryFolder;
import chylex.respack.packs.ResourcePackListProcessor;
import chylex.respack.render.RenderPackListOverlay;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomResourcePacks extends GuiScreenResourcePacks{
	private final GuiScreen parentScreen;
	
	private GuiTextField searchField;
    private GuiResourcePackAvailable guiPacksAvailable;
    private GuiResourcePackSelected guiPacksSelected;
    private List<ResourcePackListEntryFound> listPacksAvailable, listPacksAvailableProcessed, listPacksDummy;
    private List<ResourcePackListEntryFound> listPacksSelected;
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
		
		searchField = new GuiTextField(fontRendererObj,width/2-203,height-46,198,16);

		listPacksAvailable = Lists.newArrayListWithCapacity(8);
		listPacksAvailableProcessed = Lists.newArrayListWithCapacity(8);
		listPacksDummy = Lists.newArrayListWithCapacity(1);
		listPacksSelected = Lists.newArrayListWithCapacity(8);
		
		ResourcePackRepository repository = mc.getResourcePackRepository();
		repository.updateRepositoryEntriesAll();
		
		currentFolder = repository.getDirResourcepacks();
		listPacksAvailable.addAll(createAvailablePackList(repository));
        
        for(Entry entry:(List<Entry>)Lists.reverse(repository.getRepositoryEntries())){
        	listPacksSelected.add(new ResourcePackListEntryFound(this,entry));
        }
		
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
			
			RenderPackListOverlay.refreshPackNames();
			mc.displayGuiScreen(parentScreen);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 0){
			for(GuiButton button:(List<GuiButton>)buttonList){
				if (button.mousePressed(mc,mouseX,mouseY)){
					selectedButton = button;
					button.func_146113_a(mc.getSoundHandler());
					actionPerformed(button);
				}
			}
		}
		
		guiPacksAvailable.func_148179_a(mouseX,mouseY,buttonId);
		guiPacksSelected.func_148179_a(mouseX,mouseY,buttonId);
		searchField.mouseClicked(mouseX,mouseY,buttonId);
		
		listProcessor.refresh();
	}
	
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int eventType){
		if (eventType == 0 && selectedButton != null){
			selectedButton.mouseReleased(mouseX,mouseY);
			selectedButton = null;
		}
	}
	
	@Override
	protected void keyTyped(char keyChar, int keyCode){
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
		
		for(ResourcePackListEntryFound entry:listPacksSelected){
			if (entry.func_148318_i() != null){
				selected.add(entry.func_148318_i());
			}
		}
		
		Collections.reverse(selected);
		
		mc.getResourcePackRepository().func_148527_a(selected);
		return selected;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawBackground(0);
		guiPacksAvailable.drawScreen(mouseX,mouseY,partialTickTime);
		guiPacksSelected.drawScreen(mouseX,mouseY,partialTickTime);
		searchField.drawTextBox();
		
		for(GuiButton button:(List<GuiButton>)buttonList){
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
					try{
						Constructor<Entry> constructor = Entry.class.getDeclaredConstructor(ResourcePackRepository.class,File.class);
						constructor.setAccessible(true);
						
						Entry entry = constructor.newInstance(repository,file);
						entry.updateResourcePack();
						list.add(new ResourcePackListEntryFound(this,entry));
					}catch(Throwable t){
						t.printStackTrace();
					}
				}
			}
		}
		
		List<Entry> repositoryEntries = repository.getRepositoryEntries();
		
		for(Iterator<ResourcePackListEntryFound> iter = list.iterator(); iter.hasNext();){
			ResourcePackListEntryFound listEntry = iter.next();
			
			if (listEntry.func_148318_i() != null && repositoryEntries.contains(listEntry.func_148318_i())){
				iter.remove();
			}
		}
		
		return list;
	}
	
	// OVERRIDES FROM GuiScreenResourcePacks
	
	@Override
	public boolean func_146961_a(ResourcePackListEntry entry){
		return listPacksSelected.contains(entry);
	}
	
	@Override
	public List func_146962_b(ResourcePackListEntry entry){
		return func_146961_a(entry) ? listPacksSelected : listPacksAvailable;
	}
	
	@Override
	public List func_146964_g(){
		hasUpdated = true;
		listPacksDummy.clear();
		return listPacksDummy;
	}
	
	@Override
	public List func_146963_h(){
		hasUpdated = true;
		return listPacksSelected;
	}
}
