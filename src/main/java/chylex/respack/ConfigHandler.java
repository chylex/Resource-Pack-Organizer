package chylex.respack;
import chylex.respack.gui.RenderPackListOverlay;
import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class ConfigHandler{
	public static final ForgeConfigSpec SPEC;
	
	public static EnumValue<DisplayPosition> DISPLAY_POSITION;
	public static EnumValue<TextFormatting> DISPLAY_COLOR;
	
	static{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		EnumGetMethod getter = EnumGetMethod.NAME_IGNORECASE;
		
		DISPLAY_POSITION = builder.defineEnum("displayPosition", DisplayPosition.DISABLED, getter);
		DISPLAY_COLOR = builder.defineEnum("displayColor", TextFormatting.WHITE, getter, value -> getter.validate(value, TextFormatting.class) && getter.get(value, TextFormatting.class).isColor());
		
		SPEC = builder.build();
		
		try{
			String fixedComment = "Allowed Values: " + Arrays.stream(TextFormatting.values()).filter(TextFormatting::isColor).map(Enum::name).collect(Collectors.joining(", "));
			
			Object o = SPEC.get("displayColor");
			Field f = o.getClass().getDeclaredField("comment");
			f.setAccessible(true);
			
			FieldUtils.removeFinalModifier(f, true);
			f.set(o, fixedComment);
		}catch(Throwable ignored){}
	}
	
	public enum DisplayPosition{
		DISABLED, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
	}
	
	public static void onConfigUpdated(){
		if (DISPLAY_POSITION.get() == DisplayPosition.DISABLED){
			RenderPackListOverlay.unregister();
		}
		else{
			RenderPackListOverlay.register();
		}
	}
}
