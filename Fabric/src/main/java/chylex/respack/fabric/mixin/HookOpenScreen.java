package chylex.respack.fabric.mixin;
import chylex.respack.fabric.gui.CustomResourcePackScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class HookOpenScreen{
	@Inject(method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
	private void openScreen(Screen screen, CallbackInfo ci){
		MinecraftClient mc = MinecraftClient.getInstance();
		
		if (screen != null && screen.getClass() == ResourcePackOptionsScreen.class && mc.currentScreen instanceof SettingsScreen && !Screen.hasAltDown()){
			ci.cancel();
			mc.openScreen(new CustomResourcePackScreen(mc.currentScreen, mc.options));
		}
	}
}
