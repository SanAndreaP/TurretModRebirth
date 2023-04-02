/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.datagenerator;

import com.google.common.collect.Sets;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public class PatchouliProvider
        implements IDataProvider
{
    private final DataGenerator generator;

    public PatchouliProvider(DataGenerator generator) {
        this.generator = generator;
    }

    public void run(@Nonnull DirectoryCache cache) {
        Path        outDir = this.generator.getOutputFolder();
        Set<String> set    = Sets.newHashSet();
        Consumer<PatchouliBuilder> consumer = builder -> {
            if( !set.add(String.format("%s/%s", builder.getCategory(), builder.getName())) ) {
                throw new IllegalStateException(String.format("Duplicate entry %s/%s", builder.getCategory(), builder.getName()));
            } else {
                Path path = createPath(outDir, builder);

                try {
                    IDataProvider.save(JsonUtils.GSON, cache, builder.toJson(), path);
                } catch( IOException ioexception ) {
                    TmrConstants.LOG.error("Couldn't save book entry {}", path, ioexception);
                }

            }
        };

        registerEntries(consumer);
    }

    /**
     * Override this method for registering and generating custom book entries. <p>
     * Just use {@link PatchouliBuilder} to build your entry.
     * @param consumer used for the build function from {@link PatchouliBuilder}
     */
    protected void registerEntries(Consumer<PatchouliBuilder> consumer) {
        PatchouliEntries.registerTurrets(consumer);
        PatchouliEntries.registerAmmo(consumer);
        PatchouliEntries.registerUpgrades(consumer);
        PatchouliEntries.registerMisc(consumer);
    }

    private Path createPath(Path basePath, PatchouliBuilder builder) {
        String  nm = builder.getName().toLowerCase(Locale.ROOT)
                                      .trim()
                                      .replaceAll("\\W", "_")
                                      .replace("__", "_")
                                      .replaceAll("_$", "")
                                      .replaceAll("^_", "");

        return basePath.resolve(String.format("data/%s/patchouli_books/%s/en_us/entries/%s/%s.json",
                                              builder.getCategory().getNamespace(),
                                              this.getBookName(),
                                              builder.getCategory().getPath(),
                                              nm));
    }

    @SuppressWarnings("java:S3400")
    public String getBookName() {
        return "turret_lexicon";
    }

    @Nonnull
    @Override
    public String getName() {
        return "PatchouliBooks";
    }
}
