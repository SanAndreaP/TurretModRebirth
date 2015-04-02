package sanandreasp.mods.TurretMod3.registry;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import sanandreasp.mods.turretmod3.entity.EntityDismantleStorage;
import sanandreasp.mods.turretmod3.entity.EntityMobileBase;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

import java.util.*;

public class TurretTargetRegistry {
	public static TurretTargetRegistry trTargets;

	private static List<Class> targetCreatures;
	private static List<String> preDefCreatures;
	private static Map<Integer, String> targetList;
	private static List<Class> excludedEntities = new ArrayList<Class>();

	public static void initTargetReg() {
		excludedEntities.add(EntityTurret_Base.class);
		excludedEntities.add(EntityDismantleStorage.class);
		excludedEntities.add(EntityMobileBase.class);

		targetCreatures = initTargetableCreatures();
		preDefCreatures = initStandardCreatures();
		targetList = initTargetList();
	}

	public static List<Class> getTargetClasses() {
		return new ArrayList<Class>(targetCreatures);
	}

	public static List<String> getTargetStrings() {
		return new ArrayList<String>(preDefCreatures);
	}

	public static Map<Integer, String> getTargetList() {
		return new HashMap<Integer, String>(targetList);
	}

	private static List<Class> getAllEntities() {
		return new ArrayList<Class>(EntityList.stringToClassMapping.values());
	}

	private static List<Class> initTargetableCreatures() {
		List<Class> allEntities = getAllEntities();
		List<Class> entities = new ArrayList<Class>();

		tgtListLabel:
		for (Class entityCls : allEntities) {
			for (Class excludes : excludedEntities) {
				if (excludes!= null && excludes.isAssignableFrom(entityCls))
                    continue tgtListLabel;
			}
			String eStr = (String) EntityList.classToStringMapping.get(entityCls);
			if (eStr.equals("Mob") || eStr.equals("Monster"))
				continue tgtListLabel;

			if ((EntityLiving.class.isAssignableFrom(entityCls) || IMob.class.isAssignableFrom(entityCls) || IAnimals.class.isAssignableFrom(entityCls))
					&& EntityLiving.class.isAssignableFrom(entityCls)) {
				entities.add(entityCls);
			}
		}

		return entities;
	}

	private static List<String> initStandardCreatures() {
		List<String> retVal = new ArrayList<String>();
		for (Class entityCls : initTargetableCreatures()) {
			if (IMob.class.isAssignableFrom(entityCls)) {
				retVal.add((String) EntityList.classToStringMapping.get(entityCls));
			}
		}
		return retVal;
	}

	private static Map<Integer, String> initTargetList() {
		Map<Integer, String> tList = Maps.newHashMap();
		int index = 0;

		tList.put(index++, "\ngui.tcu.tgtMonster");
		for (Iterator<Class> tCreatures = targetCreatures.iterator();tCreatures.hasNext();) {
			Class entity = tCreatures.next();
			if (IMob.class.isAssignableFrom(entity)) {
				tList.put(index++, (String) EntityList.classToStringMapping.get(entity));
			}
		}

		tList.put(index++, "\ngui.tcu.tgtAnimals");
		for (Iterator<Class> tCreatures = targetCreatures.iterator();tCreatures.hasNext();) {
			Class entity = tCreatures.next();
			if (IAnimals.class.isAssignableFrom(entity) && !IMob.class.isAssignableFrom(entity)) {
				tList.put(index++, (String) EntityList.classToStringMapping.get(entity));
			}
		}

		tList.put(index++, "\ngui.tcu.tgtOthers");
		for (Iterator<Class> tCreatures = targetCreatures.iterator();tCreatures.hasNext();) {
			Class entity = tCreatures.next();
			if (!IAnimals.class.isAssignableFrom(entity) && !IMob.class.isAssignableFrom(entity)) {
				tList.put(index++, (String) EntityList.classToStringMapping.get(entity));
			}
		}

		return tList;
	}
}
