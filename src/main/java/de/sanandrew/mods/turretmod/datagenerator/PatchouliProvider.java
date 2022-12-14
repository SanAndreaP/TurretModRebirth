package de.sanandrew.mods.turretmod.datagenerator;

import com.google.common.collect.Sets;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class PatchouliProvider
        implements IDataProvider
{
    private final DataGenerator generator;

    public PatchouliProvider(DataGenerator p_i48869_1_) {
        this.generator = p_i48869_1_;
    }

    public void run(DirectoryCache cache) throws IOException {
        Path                  path = this.generator.getOutputFolder();
        Set<String> set  = Sets.newHashSet();
        Consumer<PatchouliBuilder> consumer = (p_204017_3_) -> {
            if( !set.add(String.format("%s/%s", p_204017_3_.getCategory(), p_204017_3_.getName()) ) {
                throw new IllegalStateException(String.format("Duplicate entry %s/%s", p_204017_3_.getCategory(), p_204017_3_.getName()));
            } else {
                Path path1 = createPath(path, p_204017_3_);

                try {
                    IDataProvider.save(JsonUtils.GSON, cache, p_204017_3_., path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };

        registerAdvancements(consumer, fileHelper);
    }

    /**
     * Override this method for registering and generating custom book entries. <p>
     * Just use {@link PatchouliBuilder} to build your entry.
     * @param consumer used for the register function from {@link PatchouliBuilder}
     * @param fileHelper used for the register function from {@link PatchouliBuilder}
     */
    protected void registerEntries(Consumer<PatchouliBuilder> consumer, net.minecraftforge.common.data.ExistingFileHelper fileHelper) {

    }

    private Path createPath(Path basePath, PatchouliBuilder builder) {
        return basePath.resolve(String.format("data/%s/patchouli_books/%s/en_us/entries/%s.json",
                                              builder.getCategory().getNamespace(),
                                              this.getBookName(),
                                              builder.getCategory().getPath()));
    }

    public String getBookName() {
        return "turret_lexicon";
    }

    @Override
    public String getName() {
        return "PatchouliBooks";
    }
}
