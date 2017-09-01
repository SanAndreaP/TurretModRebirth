/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;

import java.util.UUID;

public final class Ammunitions
{
    public static final UUID ARROW = UUID.fromString("7B497E61-4E8D-4E49-AC71-414751E399E8");
    public static final UUID QUIVER = UUID.fromString("E6D51120-B52A-42EA-BF78-BEBBC7D41C09");
    public static final UUID SGSHELL = UUID.fromString("3B3AA3F7-DA37-4B92-8F18-53694361447F");
    public static final UUID SGSHELL_PACK = UUID.fromString("6F3DB2C0-E881-462A-AC3A-6358EA7A1FE8");
    public static final UUID CRYOCELL_MK1 = UUID.fromString("0B567594-E5CA-48B5-A538-E87C213F439C");
    public static final UUID CRYOCELL_PACK_MK1 = UUID.fromString("7DE80386-CE9E-4039-ADA6-F7131996E522");
    public static final UUID CRYOCELL_MK2 = UUID.fromString("CB5BE826-0480-4D30-AF1F-23BE19329B37");
    public static final UUID CRYOCELL_PACK_MK2 = UUID.fromString("82D1E748-ABDE-4911-96B3-B43E5AA716CB");
    public static final UUID CRYOCELL_MK3 = UUID.fromString("3181E328-0151-44E0-ADD2-5FCB6B724AEC");
    public static final UUID CRYOCELL_PACK_MK3 = UUID.fromString("399B8468-B68F-40FD-B442-156760161283");
    public static final UUID BULLET = UUID.fromString("E8CB6C41-00FE-4FA0-AD98-FC8DAD6609AC");
    public static final UUID BULLET_PACK = UUID.fromString("FD7B2FBF-9BB7-437F-9FF0-A21842D3A94A");
    public static final UUID MGSHELL = UUID.fromString("3851173D-3AC3-4F17-A488-68C33716AF26");
    public static final UUID MGSHELL_PACK = UUID.fromString("50634E1E-94C4-4EF6-8D76-7C8CADDCAE85");
    public static final UUID FLUXCELL = UUID.fromString("48800C6A-9A31-4F45-8AD5-DD02B8B18BCB");
    public static final UUID FLUXCELL_PACK = UUID.fromString("1F427D47-1BED-41D4-8810-47FF274424B6");
    public static final UUID FUELTANK = UUID.fromString("0CA51FA8-FD33-4C3D-A9AB-BA29DFFF4ABA");
    public static final UUID FUELTANK_PACK = UUID.fromString("5C41DA09-8C7E-4191-8520-058E69C36DC0");

    public static void initialize(IAmmunitionRegistry registry) {
        AmmunitionRegistry castRegistry = (AmmunitionRegistry) registry;

        castRegistry.registerAmmoType(new TurretAmmoArrow.Single(), true);
        castRegistry.registerAmmoType(new TurretAmmoArrow.Quiver(), true);
        castRegistry.registerAmmoType(new TurretAmmoShotgunShell.Single(), true);
        castRegistry.registerAmmoType(new TurretAmmoShotgunShell.Multi(), true);
        castRegistry.registerAmmoType(new TurretAmmoBullet.Single(), true);
        castRegistry.registerAmmoType(new TurretAmmoBullet.Multi(), true);
        castRegistry.registerAmmoType(new TurretAmmoCryoCell.SingleMK1(), true);
        castRegistry.registerAmmoType(new TurretAmmoCryoCell.MultiMK1(), true);
        castRegistry.registerAmmoType(new TurretAmmoCryoCell.SingleMK2(), true);
        castRegistry.registerAmmoType(new TurretAmmoCryoCell.MultiMK2(), true);
        castRegistry.registerAmmoType(new TurretAmmoCryoCell.SingleMK3(), true);
        castRegistry.registerAmmoType(new TurretAmmoCryoCell.MultiMK3(), true);
        castRegistry.registerAmmoType(new TurretAmmoMinigunShell.Single(), true);
        castRegistry.registerAmmoType(new TurretAmmoMinigunShell.Multi(), true);
        castRegistry.registerAmmoType(new TurretAmmoFluxCell.Single(), true);
        castRegistry.registerAmmoType(new TurretAmmoFluxCell.Multi(), true);
        castRegistry.registerAmmoType(new TurretAmmoFireTank.Single(), true);
        castRegistry.registerAmmoType(new TurretAmmoFireTank.Multi(), true);
    }
}
