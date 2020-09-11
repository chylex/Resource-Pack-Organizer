package chylex.respack.packs;
import net.minecraft.client.gui.screen.PackLoadingManager.IPack;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ResourcePackFolder extends ResourcePackInfo{
	private static final ResourceLocation folderResource = new ResourceLocation("resourcepackorganizer:textures/gui/folder.png"); // http://www.iconspedia.com/icon/folion-icon-27237.html
	
	public ResourcePackFolder(final String name, final ITextComponent title, final ITextComponent description){
		super(name, true, () -> null, title, description, PackCompatibility.COMPATIBLE, Priority.TOP, true, IPackNameDecorator.field_232625_a_, false);
	}
	
	static class PackEntry implements IPack{
		private final ResourcePackFolder pack;
		
		public PackEntry(final ResourcePackFolder pack){
			this.pack = pack;
		}
		
		@Override
		public ResourceLocation func_241868_a(){
			return folderResource;
		}
		
		@Override
		public PackCompatibility func_230460_a_(){
			return pack.getCompatibility();
		}
		
		@Override
		public ITextComponent func_230462_b_(){
			return pack.getTitle();
		}
		
		@Override
		public ITextComponent func_230463_c_(){
			return pack.getDescription();
		}
		
		@Override
		public IPackNameDecorator func_230464_d_(){
			return pack.func_232614_i_();
		}
		
		@Override
		public boolean func_230465_f_(){
			return pack.isOrderLocked();
		}
		
		@Override
		public boolean func_230466_g_(){
			return pack.isAlwaysEnabled();
		}
		
		@Override
		public void func_230471_h_(){}
		
		@Override
		public void func_230472_i_(){}
		
		@Override
		public void func_230467_j_(){}
		
		@Override
		public void func_230468_k_(){}
		
		@Override
		public boolean func_230473_l_(){
			return false;
		}
		
		@Override
		public boolean func_230469_o_(){
			return false;
		}
		
		@Override
		public boolean func_230470_p_(){
			return false;
		}
	}
}
