package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.Stage;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncTurretStages
        extends SimpleMessage
{
    private final Map<ResourceLocation, Stage> stages;

    public SyncTurretStages(Map<ResourceLocation, Stage> stages) {
        this.stages = stages;
    }

    public SyncTurretStages(PacketBuffer buffer) {
        this.stages = new HashMap<>();

        for( int i = 0, sMax = buffer.readInt(); i < sMax; i++ ) {
            ResourceLocation id   = buffer.readResourceLocation();
            int              lvl  = buffer.readInt();
            Stage.Modifier[] mods = new Stage.Modifier[buffer.readInt()];

            for( int j = 0; j < mods.length; j++ ) {
                UUID   modId = buffer.readUUID();
                String mName = buffer.readUtf();
                ResourceLocation attribId = buffer.readResourceLocation();
                double           amt      = buffer.readDouble();
                int              op       = buffer.readInt();
                ResourceLocation turretId = buffer.readBoolean() ? buffer.readResourceLocation() : null;

                mods[j] = new Stage.Modifier(ForgeRegistries.ATTRIBUTES.getValue(attribId),
                                             new AttributeModifier(modId, mName, amt, AttributeModifier.Operation.fromValue(op)), turretId);
            }
            this.stages.put(id, new Stage(lvl, mods));
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.stages.size());
        this.stages.forEach((id, s) -> {
            buffer.writeResourceLocation(id);
            buffer.writeInt(s.level);
            buffer.writeInt(s.modifiers.length);
            Arrays.stream(s.modifiers).forEach(m -> {
                buffer.writeUUID(m.mod.getId());
                buffer.writeUtf(m.mod.getName());
                buffer.writeResourceLocation(Objects.requireNonNull(m.attribute.getRegistryName()));
                buffer.writeDouble(m.mod.getAmount());
                buffer.writeInt(m.mod.getOperation().toValue());
                PacketRegistry.writeOptional(m.turret != null, buffer, b -> b.writeResourceLocation(Objects.requireNonNull(m.turret)));
            });
        });
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            LevelStorage.applyStages(this.stages);
            return null;
        });
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
