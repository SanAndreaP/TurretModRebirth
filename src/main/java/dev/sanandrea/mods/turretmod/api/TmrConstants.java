/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class TmrConstants
{
    private TmrConstants() { }

    public static final String ID = "sapturretmod";
    public static final Logger LOG = LogManager.getLogger(ID);
    public static final String VERSION = "4.0.0-beta.4.4";
    public static final String CHANNEL = "SapTurretModNWCH";
    public static final String NAME = "Turret Mod Rebirth";

    static final String BUILD_MCVER    = "1.16.5";
    static final String BUILD_FORGEVER = "1.16.5-36.2.34";
    static final String BUILD_MAPPINGS_CHANNEL = "official";
    static final String BUILD_MAPPINGS_VERSION = "1.16.5";

    public static final String API_ID = "sapturretmod_api";
    public static final String API_VERSION = "1.2.0-beta.4";
}
