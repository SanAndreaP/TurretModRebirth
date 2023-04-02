/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.init;

public interface IRenderClassProvider
{
    default String getCrossbowBoltRenderClass() { return null; }
}
