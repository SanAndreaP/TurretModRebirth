package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTargets;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public abstract class TargetType<T>
{
    public static final TargetType<UUID> PLAYER = new Player();
    public static final TargetType<ResourceLocation> CREATURE = new Creature();
    private static final Map<String, TargetType<?>> TYPE_MAP = new HashMap<String, TargetType<?>>() {
        private static final long serialVersionUID = 4510806692403902032L;

        {
            this.put("player", PLAYER);
            this.put("creature", CREATURE);
        }
    };

    public abstract void updateTarget(ITurretInst turretInst, T id, boolean enabled);

    public abstract String getName(ITurretInst turretInst, T id);

    public abstract EntityType getType(ITurretInst turretInst, T id);

    public abstract boolean isTargeted(ITurretInst turretInst, T id);

    public abstract Map<T, Boolean> getTargets(ITurretInst turretInst);

    public abstract boolean isBlacklist(ITurretInst turretInst);

    public abstract void toggleBlacklist(ITurretInst turretInst);

    public abstract void toggleAllTargets(EntityType type, ITurretInst turretInst, boolean enable);

    void buildElements(IGui gui, ITurretInst turretInst, JsonObject nodeData, int width, String filter, List<GuiElementInst> elements) {
        MutableInt posY = new MutableInt(0);
        this.getTargets(turretInst).forEach((id, enabled) -> {
            GuiElementInst elem = new GuiElementInst();
            elem.element = new TargetNode<>(id, this, width);
            gui.getDefinition().initElement(elem);
            elem.data = nodeData;
            elem.get().bakeData(gui, elem.data);
            if( Strings.isNullOrEmpty(filter) || elem.get(TargetNode.class).getName().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)) ) {
                elements.add(elem);
                elem.pos[1] = posY.getAndAdd(elem.get().getHeight());
            }
        });
    }

    public static TargetType<?> fromString(String s) {
        return MiscUtils.defIfNull(TYPE_MAP.get(s.toLowerCase(Locale.ROOT)),
                                   () -> { throw new IllegalArgumentException(String.format("No type %s.%s", TargetType.class.getCanonicalName(), s)); });
    }

    Map<T, Boolean> getSortedEntities(ITurretInst turretInst, Map<T, Boolean> entities) {
        Map<T, Boolean> sortedEntities = new TreeMap<>((e1, e2) -> {
            EntityType c1 = this.getType(turretInst, e1);
            EntityType c2 = this.getType(turretInst, e2);

            int p = Integer.compare(c2.ordinal(), c1.ordinal());
            if( p == 0 ) {
                return this.getName(turretInst, e1).compareTo(this.getName(turretInst, e2));
            } else {
                return p;
            }
        });
        sortedEntities.putAll(entities);

        return sortedEntities;
    }

    public enum EntityType
    {
        NEUTRAL,
        PEACEFUL,
        HOSTILE,
    }

    private static final class Player
            extends TargetType<UUID>
    {
        @Override
        public void updateTarget(ITurretInst turretInst, UUID id, boolean enabled) {
            turretInst.getTargetProcessor().updatePlayerTarget(id, enabled);
            PacketRegistry.sendToServer(PacketUpdateTargets.updateTarget(turretInst, id, enabled));
        }

        @Override
        public String getName(ITurretInst turretInst, UUID id) {
            return PlayerList.INSTANCE.getPlayerName(id);
        }

        @Override
        public EntityType getType(ITurretInst turretInst, UUID id) {
            return EntityType.NEUTRAL;
        }

        @Override
        public boolean isTargeted(ITurretInst turretInst, UUID id) {
            return turretInst.getTargetProcessor().isPlayerTargeted(id);
        }

        @Override
        public boolean isBlacklist(ITurretInst turretInst) {
            return turretInst.getTargetProcessor().isPlayerBlacklist();
        }

        @Override
        public void toggleBlacklist(ITurretInst turretInst) {
            boolean b = !turretInst.getTargetProcessor().isPlayerBlacklist();
            turretInst.getTargetProcessor().setPlayerBlacklist(b);
            PacketRegistry.sendToServer(PacketUpdateTargets.updatePlayerBlacklist(turretInst, b));
        }

        @Override
        public Map<UUID, Boolean> getTargets(ITurretInst turretInst) {
            return getSortedEntities(turretInst, turretInst.getTargetProcessor().getPlayerTargets());
        }

        @Override
        public void toggleAllTargets(EntityType type, ITurretInst turretInst, boolean enable) {
            ITargetProcessor processor = turretInst.getTargetProcessor();

            UUID[] players = processor.getPlayerTargets().keySet().toArray(new UUID[0]);
            for( UUID id : players ) {
                processor.updatePlayerTarget(id, enable);
            }

            PacketRegistry.sendToServer(PacketUpdateTargets.updateTargets(turretInst, players, enable));
        }
    }

    private static final class Creature
            extends TargetType<ResourceLocation>
    {
        @Override
        public void updateTarget(ITurretInst turretInst, ResourceLocation id, boolean enabled) {
            turretInst.getTargetProcessor().updateEntityTarget(id, enabled);
            PacketRegistry.sendToServer(PacketUpdateTargets.updateTarget(turretInst, id, enabled));
        }

        @Override
        public String getName(ITurretInst turretInst, ResourceLocation id) {
            return LangUtils.translateEntityCls(MiscUtils.defIfNull(EntityList.getClass(id), Entity.class));
        }

        @Override
        public EntityType getType(ITurretInst turretInst, ResourceLocation id) {
            Class<?> cls = MiscUtils.defIfNull(EntityList.getClass(id), Entity.class);

            return IMob.class.isAssignableFrom(cls) ? EntityType.HOSTILE : IAnimals.class.isAssignableFrom(cls) ? EntityType.PEACEFUL : EntityType.NEUTRAL;
        }

        @Override
        public boolean isTargeted(ITurretInst turretInst, ResourceLocation id) {
            return turretInst.getTargetProcessor().isEntityTargeted(id);
        }

        @Override
        public boolean isBlacklist(ITurretInst turretInst) {
            return turretInst.getTargetProcessor().isEntityBlacklist();
        }

        @Override
        public Map<ResourceLocation, Boolean> getTargets(ITurretInst turretInst) {
            return getSortedEntities(turretInst, turretInst.getTargetProcessor().getEntityTargets());
        }

        @Override
        public void toggleBlacklist(ITurretInst turretInst) {
            boolean b = !turretInst.getTargetProcessor().isEntityBlacklist();
            turretInst.getTargetProcessor().setEntityBlacklist(b);
            PacketRegistry.sendToServer(PacketUpdateTargets.updateEntityBlacklist(turretInst, b));
        }

        @Override
        public void toggleAllTargets(EntityType type, ITurretInst turretInst, boolean enable) {
            ITargetProcessor processor = turretInst.getTargetProcessor();

            ResourceLocation[] entities = processor.getEntityTargets().keySet().stream()
                                                   .filter(id -> type == null || this.getType(turretInst, id) == type).toArray(ResourceLocation[]::new);
            for( ResourceLocation id : entities ) {
                processor.updateEntityTarget(id, enable);
            }

            PacketRegistry.sendToServer(PacketUpdateTargets.updateTargets(turretInst, entities, enable));
        }
    }
}
