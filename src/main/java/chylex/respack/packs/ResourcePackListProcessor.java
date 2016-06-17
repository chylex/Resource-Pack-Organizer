package chylex.respack.packs;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.resources.ResourcePackListEntryFound;

public class ResourcePackListProcessor{
	private static String name(ResourcePackListEntryFound entry){
		if (entry instanceof ResourcePackListEntryCustom)return ((ResourcePackListEntryCustom)entry).func_148312_b();
		return entry.func_148318_i().getResourcePackName();
	}
	
	private static String nameSort(ResourcePackListEntryFound entry, boolean reverse){
		String pfx1 = !reverse ? "a" : "z";
		String pfx2 = !reverse ? "b" : "z";
		String pfx3 = !reverse ? "z" : "a";
		
		if (entry instanceof ResourcePackListEntryFolder){
			ResourcePackListEntryFolder folder = (ResourcePackListEntryFolder)entry;
			return folder.isUp ? pfx1+folder.folderName : pfx2+folder.folderName; // sort folders first
		}
		
		if (entry instanceof ResourcePackListEntryCustom)return pfx3+((ResourcePackListEntryCustom)entry).func_148312_b();
		return pfx3+entry.func_148318_i().getResourcePackName();
	}
	
	private static String description(ResourcePackListEntryFound entry){
		if (entry instanceof ResourcePackListEntryCustom)return ((ResourcePackListEntryCustom)entry).func_148311_a();
		return entry.func_148318_i().getTexturePackDescription();
	}
	
	public static final Comparator<ResourcePackListEntryFound> sortAZ = new Comparator<ResourcePackListEntryFound>(){
		@Override
		public int compare(ResourcePackListEntryFound entry1, ResourcePackListEntryFound entry2){
			return String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1,false),nameSort(entry2,false));
		};
	};
	
	public static final Comparator<ResourcePackListEntryFound> sortZA = new Comparator<ResourcePackListEntryFound>(){
		@Override
		public int compare(ResourcePackListEntryFound entry1, ResourcePackListEntryFound entry2){
			return -String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1,true),nameSort(entry2,true));
		};
	};
	
	private final List<ResourcePackListEntryFound> sourceList, targetList;
	
	private Comparator<ResourcePackListEntryFound> sorter;
	private Pattern textFilter;
	
	public ResourcePackListProcessor(List<ResourcePackListEntryFound> sourceList, List<ResourcePackListEntryFound> targetList){
		this.sourceList = sourceList;
		this.targetList = targetList;
		refresh();
	}
	
	public void setSorter(Comparator<ResourcePackListEntryFound> comparator){
		this.sorter = comparator;
		refresh();
	}
	
	public void setFilter(String text){
		if (text == null || text.isEmpty()){
			textFilter = null;
		}
		else{
			textFilter = Pattern.compile("\\Q"+text.replace("*","\\E.*\\Q")+"\\E",Pattern.CASE_INSENSITIVE);
		}
		
		refresh();
	}
	
	public void refresh(){
		targetList.clear();
		
		for(ResourcePackListEntryFound entry:sourceList){
			if (checkFilter(name(entry)) || checkFilter(description(entry))){
				targetList.add(entry);
			}
		}
		
		if (sorter != null){
			Collections.sort(targetList,sorter);
		}
	}
	
	private boolean checkFilter(String entryText){
		return textFilter == null || textFilter.matcher(entryText.toLowerCase(Locale.ENGLISH)).find();
	}
}
