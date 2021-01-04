package de.sanandrew.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.ShieldHandler;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.ShieldTurret;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class ModelTurretForcefield
        extends ModelTurretBase
{
    private ModelRenderer shieldBar;

    public ModelTurretForcefield(float scale) {
        super(scale, Resources.TURRET_T2_SHIELDGEN_MODEL.resource);
    }

    @Override
    protected List<String> getMandatoryBoxes() {
        List<String> ret = new ArrayList<>(super.getMandatoryBoxes());
        ret.add("shieldBar");

        return ret;
    }

    @Override
    public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase, ModelJsonLoader.ModelJson> loader) {
        super.onReload(iResourceManager, loader);

        this.shieldBar = loader.getBox("shieldBar");
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);

        ITurretInst turret = (ITurretInst) entity;

        ShieldTurret shieldTurret = turret.getRAM(null);

        if( shieldTurret != null ) {
            float maxShield = turret.isInGui() ? 2.0F : shieldTurret.getMaxValue();
            float shield    = turret.isInGui() ? 1.0F : shieldTurret.getValue();

            this.shieldBar.rotateAngleX = ((float) Math.PI / 2.0F) * (Math.max(0.0F, maxShield - shield) / maxShield);
        } else {
            this.shieldBar.rotateAngleX = 0.0F;
        }
    }
}
