package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProj_Arrow;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;

public class EntityTurret_T1Arrow extends EntityTurret_Base {

	public EntityTurret_T1Arrow(World par1World) {
		super(par1World);
        setTextures("t1Arrow");
	}

	@Override
	public TurretProjectile getProjectile() {
		return new TurretProj_Arrow(this.worldObj);
	}

	@Override
	public int getMaxShootTicks() {
		return 20;
	}
}
