package chylex.respack;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.IConfigElement;
import com.google.common.collect.ImmutableList;

public class ConfigHandler{
	private final Configuration config;
	public final Options options;
	
	ConfigHandler(File file){
		this.config = new Configuration(file);
		this.config.load();
		this.options = new Options();
		
		reload();
	}
	
	public List<IConfigElement> getGuiConfigElements(){
		List<IConfigElement> originalElements = new ConfigElement(config.getCategory("client")).getChildElements();
		List<IConfigElement> usedElements = new ArrayList<>();
		
		for(IConfigElement element : originalElements){
			if (element.getName().equals("displayColor")){
				usedElements.add(new ConfigElement(config.getCategory("client").get("displayColor")){
					@Override
					public ConfigGuiType getType(){
						return ConfigGuiType.COLOR;
					}
				});
			}
			else{
				usedElements.add(element);
			}
		}
		
		return usedElements;
	}
	
	public String getGuiConfigString(){
		return GuiConfig.getAbridgedConfigPath(config.toString());
	}
	
	public void reload(){
		options.updateOptions();
		
		if (config.hasChanged()){
			config.save();
		}
	}
	
	public class Options{
		private String displayPosition = "disabled";
		private char displayColor = 'f';
		private String[] enabledPacks;
		
		private Property propEnabledPacks;
		
		void updateOptions(){
			displayPosition = config.getString("displayPosition", "client", "disabled", "Sets the position of active resource pack list on the screen. Valid values are: disabled, top left, top right, bottom right, bottom left", DisplayPosition.validValues);
			
			String color = config.getString("displayColor", "client", "f", "Sets the color of active resource pack list. Valid value is a single lowercase hexadecimal character (0 to f) where '0' is black and 'f' is white", "f0123456789abcde".split(""));
			if (!color.isEmpty())displayColor = color.charAt(0);
			
			propEnabledPacks = config.get("client", "enabledPacks", new String[0], "Internal list of enabled resource packs.");
			propEnabledPacks.setShowInGui(false);
			enabledPacks = propEnabledPacks.getStringList();
			
			if (enabledPacks.length == 0){
				List<String> packs = Minecraft.getMinecraft().gameSettings.resourcePacks;
				enabledPacks = packs.toArray(new String[packs.size()]);
			}
		}
		
		public DisplayPosition getDisplayPosition(){
			for(int index = 0; index < DisplayPosition.validValues.length; index++){
				if (DisplayPosition.validValues[index].equals(displayPosition)){
					return DisplayPosition.values()[index];
				}
			}
			
			return DisplayPosition.DISABLED;
		}
		
		public int getDisplayColor(){
			return GuiUtils.getColorCode(displayColor, true);
		}
		
		public List<String> getEnabledPacks(){
			return ImmutableList.copyOf(enabledPacks);
		}
		
		public void updateEnabledPacks(){
			List<String> packs = Minecraft.getMinecraft().gameSettings.resourcePacks;
			propEnabledPacks.set(packs.toArray(new String[packs.size()]));
			config.save();
		}
	}
	
	public enum DisplayPosition{
		DISABLED, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT;
		
		static String[] validValues = new String[]{ "disabled", "top left", "top right", "bottom right", "bottom left" };
	}
}
