/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class TmrConstants
{
    public static final String ID = "sapturretmod";
    public static final Logger LOG = LogManager.getLogger(ID);
    public static final String VERSION = "4.0.0-beta.4";
    public static final String CHANNEL = "SapTurretModNWCH";
    public static final String NAME = "Turret Mod Rebirth";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.2.2611,];required-after:sanlib@[1.4.4,]";

    public static final String API_ID = "sapturretmod_api";
    public static final String API_VERSION = "1.0.0-beta.4";
}
