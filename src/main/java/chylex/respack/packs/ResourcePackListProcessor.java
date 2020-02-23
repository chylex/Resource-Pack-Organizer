package chylex.respack.packs;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList.ResourcePackEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ResourcePackListProcessor{
	private static String name(ResourcePackEntry entry){
		if (entry != null){
			return entry.func_214418_e().getName();
		}
		else{
			return "<INVALID>";
		}
	}
	
	private static String nameSort(ResourcePackEntry entry, boolean reverse){
		String pfx1 = !reverse ? "a" : "z";
		String pfx2 = !reverse ? "b" : "z";
		String pfx3 = !reverse ? "z" : "a";
		
		if (entry != null){
			return pfx3 + entry.func_214418_e().getName();
		}
		else{
			return pfx3 + "<INVALID>";
		}
	}
	
	private static String description(ResourcePackEntry entry){
		if (entry != null){
			return entry.func_214418_e().getDescription().getFormattedText();
		}
		else{
			return "<INVALID>";
		}
	}
	
	public static final Comparator<ResourcePackEntry> sortAZ = (entry1, entry2) -> String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1, false), nameSort(entry2, false));
	public static final Comparator<ResourcePackEntry> sortZA = (entry1, entry2) -> -String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1, true), nameSort(entry2, true));
	
	private final List<ResourcePackEntry> sourceList, targetList;
	
	private Comparator<ResourcePackEntry> sorter;
	private Pattern textFilter;
	
	public ResourcePackListProcessor(List<ResourcePackEntry> sourceList, List<ResourcePackEntry> targetList){
		this.sourceList = sourceList;
		this.targetList = targetList;
		refresh();
	}
	
	public void setSorter(Comparator<ResourcePackEntry> comparator){
		this.sorter = comparator;
		refresh();
	}
	
	public void setFilter(String text){
		if (text == null || text.isEmpty()){
			textFilter = null;
		}
		else{
			textFilter = Pattern.compile("\\Q" + text.replace("*", "\\E.*\\Q") + "\\E", Pattern.CASE_INSENSITIVE);
		}
		
		refresh();
	}
	
	public void refresh(){
		targetList.clear();
		
		for(ResourcePackEntry entry : sourceList){
			if (checkFilter(name(entry)) || checkFilter(description(entry))){
				targetList.add(entry);
			}
		}
		
		if (sorter != null){
			targetList.sort(sorter);
		}
	}
	
	private boolean checkFilter(String entryText){
		return textFilter == null || textFilter.matcher(entryText.toLowerCase(Locale.ENGLISH)).find();
	}
}
