package sanandreasp.mods.TurretMod3.client.model.turret;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTurret_Base extends ModelBase {

	public boolean isGlowTexture = false;

	public ModelTurret_Base setGlowModel() {
		isGlowTexture = true;
		return this;
	}

	public void renderBase() {

	}

    protected void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
