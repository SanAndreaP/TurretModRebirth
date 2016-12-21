package de.sanandrew.mods.turretmod.client.model.block;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class ModelTurretAssembly
        extends ModelBase
        implements ModelJsonHandler<ModelTurretAssembly, ModelJsonLoader.ModelJson>
{
    public ModelRenderer base;
    public ModelRenderer robotBinding;
    public ModelRenderer robotArm;
    public ModelRenderer robotHead;

    public ModelJsonLoader<ModelTurretAssembly, ModelJsonLoader.ModelJson> modelJson;

    public ModelTurretAssembly() {
        this.modelJson = ModelJsonLoader.create(this, Resources.TILE_TURRET_ASSEMBLY_MODEL.getResource(), "base", "robotBinding", "robotArm", "robotHead");
    }

    public void render(float scale, TileEntityTurretAssembly te, float armX, float armZ) {
        int meta = te.hasWorldObj() ? BlockRegistry.turret_assembly.getDirection(te.getBlockMetadata()).getHorizontalIndex() : 0;
        this.base.rotateAngleY = (float)(90.0D * meta / 180.0D * Math.PI);

        this.robotBinding.rotationPointX = armX;
        this.robotArm.rotationPointZ = armZ;

        if( this.modelJson.isLoaded() ) {
            Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.render(scale));
        }
    }

    @Override
    public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretAssembly, ModelJsonLoader.ModelJson> loader) {
        loader.load();

        this.base = loader.getBox("base");
        this.robotBinding = loader.getBox("robotBinding");
        this.robotArm = loader.getBox("robotArm");
        this.robotHead = loader.getBox("robotHead");
    }

    @Override
    public void setTexture(String s) { }
}
