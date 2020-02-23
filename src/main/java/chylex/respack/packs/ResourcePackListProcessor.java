package chylex.respack.packs;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList.ResourcePackEntry;
import org.apache.commons.lang3.StringUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ResourcePackListProcessor{
	private static String name(ResourcePackEntry entry){
		return entry == null ? "<INVALID>" : entry.func_214418_e().getTitle().getString();
	}
	
	private static String description(ResourcePackEntry entry){
		return entry == null ? "<INVALID>" : entry.func_214418_e().getDescription().getString();
	}
	
	private static String nameSort(ResourcePackEntry entry, boolean reverse){
		String pfx1 = !reverse ? "a" : "z";
		String pfx2 = !reverse ? "b" : "y";
		String pfx3 = !reverse ? "x" : "a";
		
		if (entry instanceof ResourcePackFolderListEntry){
			ResourcePackFolderListEntry folder = (ResourcePackFolderListEntry)entry;
			return (folder.isUp ? pfx1 : pfx2) + name(folder); // sort folders first
		}
		else{
			return pfx3 + name(entry);
		}
	}
	
	public static final Comparator<ResourcePackEntry> sortAZ = (entry1, entry2) -> String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1, false), nameSort(entry2, false));
	public static final Comparator<ResourcePackEntry> sortZA = (entry1, entry2) -> -String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1, true), nameSort(entry2, true));
	
	private final Runnable callback;
	private Comparator<ResourcePackEntry> sorter;
	private Pattern textFilter;
	
	public ResourcePackListProcessor(Runnable callback){
		this.callback = callback;
	}
	
	public void setSorter(Comparator<ResourcePackEntry> comparator){
		this.sorter = comparator;
		callback.run();
	}
	
	public void setFilter(String text){
		text = StringUtils.trimToNull(text);
		
		if (text == null){
			textFilter = null;
		}
		else{
			textFilter = Pattern.compile("\\Q" + text.replace("*", "\\E.*\\Q") + "\\E", Pattern.CASE_INSENSITIVE);
		}
		
		callback.run();
	}
	
	public void apply(List<ResourcePackEntry> sourceList, List<ResourcePackEntry> extraList, List<ResourcePackEntry> targetList){
		targetList.clear();
		addMatching(sourceList, targetList);
		
		if (extraList != null){
			addMatching(extraList, targetList);
		}
		
		if (sorter != null){
			targetList.sort(sorter);
		}
	}
	
	private void addMatching(List<ResourcePackEntry> source, List<ResourcePackEntry> target){
		for(ResourcePackEntry entry : source){
			if (checkFilter(name(entry)) || checkFilter(description(entry))){
				target.add(entry);
			}
		}
	}
	
	private boolean checkFilter(String entryText){
		return textFilter == null || entryText.equals(ResourcePackFolderListEntry.upText) || textFilter.matcher(entryText.toLowerCase(Locale.ENGLISH)).find();
	}
}
