package chylex.respack.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ResourcePackList;

public class AvailableResourcePackListCustom extends ResourcePackList{
	public AvailableResourcePackListCustom(final ResourcePackList original){
		super(Minecraft.getInstance(), original.getWidth(), original.getHeight(), original.field_214370_e);
		replaceEntries(original.children());
		setLeftPos(original.getLeft());
	}
}
