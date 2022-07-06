package engineer.pol.utils;


import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class SelfCleaningDynamicTexture extends NativeImageBackedTexture {

    public SelfCleaningDynamicTexture(NativeImage nativeImage) {
        super(nativeImage);
    }

    @Override
    public void setImage(NativeImage nativeImage) {
        super.setImage(nativeImage);
        if (this.getImage() != null) {
            TextureUtil.prepareImage(this.getGlId(), this.getImage().getWidth(), this.getImage().getHeight());
            this.upload();
        } else {
            //Constants.LOG.error("Called setPixels in {} with NativeImage.getPixels == null", this.getClass().getName());
            System.out.println("Called setPixels in {} with NativeImage.getPixels == null");
        }
    }

}
