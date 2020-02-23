package chylex.respack.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AvailableResourcePackList;

public class AvailableResourcePackListCustom extends AvailableResourcePackList{
	public AvailableResourcePackListCustom(AvailableResourcePackList original){
		super(Minecraft.getInstance(), original.getWidth(), original.getHeight());
		replaceEntries(original.children());
		setLeftPos(original.getLeft());
	}
}
