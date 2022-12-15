package de.sanandrew.mods.turretmod.datagenerator;

import com.google.common.collect.Sets;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

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
        registerAmmo(consumer);
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

    private static void registerAmmo(Consumer<PatchouliBuilder> consumer) {
        ResourceLocation category = new ResourceLocation(TmrConstants.ID, "ammo");
        PatchouliBuilder.withIcon("Crossbow Bolts", category, Ammunitions.BOLT.getId()).sort(1)
                .page(new PatchouliBuilder.AmmoInfoPage("Crossbow Bolts")
                              .text("Bolts act like arrows, just smaller, to fit into the turret.$(br2)As with regular arrows, these can be tipped with potions.$(br2)$(5)$(o)Small knockback effect")
                              .type(Ammunitions.BOLT.getId()).type(Ammunitions.TIPPED_BOLT.getId())
                              .turret("turrets/crossbow", "Crossbow Turret"))
                .build(consumer);
    }
}
