package chylex.respack.fabric.gui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;

public class AvailableResourcePackListCustom extends AvailableResourcePackListWidget{
	public AvailableResourcePackListCustom(AvailableResourcePackListWidget original, int width, int height, int left){
		super(MinecraftClient.getInstance(), width, height);
		replaceEntries(original.children());
		setLeftPos(left);
	}
}
