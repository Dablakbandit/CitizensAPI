package net.citizensnpcs.api.astar.pathfinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class AsyncBlockSource extends BlockSource {

	private World world;

	public AsyncBlockSource(World world) {
		this.world = world;
	}

	@Override
	public int getBlockTypeIdAt(int x, int y, int z) {
		return getId(world, x, y, z);
	}

	@Override
	public World getWorld() {
		return world;
	}

	public static Integer getId(World world, int x, int y, int z) {
		try {
			Object nms_world = world_method_get_handle.invoke(world);
			Object nms_chunk = world_method_get_chunk.invoke(nms_world, x >> 4, z >> 4);
			Object nms_bp = con_block_position.newInstance(x, y, z);

			Object nms_ibd = chunk_method_get_block_data.invoke(nms_chunk, nms_bp);
			Object nms_block = iblock_method_get_block.invoke(nms_ibd);
			return (Integer) block_method_get_id.invoke(null, nms_block);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// NMS Classes
	private static Class<?> nms_world_class = getNMSClass("World");
	private static Class<?> nms_block_class = getNMSClass("Block");
	private static Class<?> nms_block_position_class = getNMSClass("BlockPosition");
	private static Class<?> nms_chunk_class = getNMSClass("Chunk");
	private static Class<?> nms_iblock_data_class = getNMSClass("IBlockData");

	// NMS Constructor
	private static Constructor<?> con_block_position = getConstructor(nms_block_position_class, int.class, int.class,
			int.class);

	// NMS Methods
	private static Method world_method_get_chunk = getMethod(nms_world_class, "getChunkAt", int.class, int.class);
	private static Method chunk_method_get_block_data = getMethod(nms_chunk_class, "getBlockData",
			nms_block_position_class);
	private static Method iblock_method_get_block = getMethod(nms_iblock_data_class, "getBlock");
	private static Method block_method_get_id = getMethod(nms_block_class, "getId", nms_block_class);

	// OBC Classes
	private static Class<?> obc_craft_world_class = getOBCClass("CraftWorld");

	// OBS Methods
	private static Method world_method_get_handle = getMethod(obc_craft_world_class, "getHandle");

	private static String version = getVersion();

	public static String getVersion() {
		if (version != null)
			return version;
		String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
	}

	public static Class<?> getClassWithException(String name) throws Exception {
		return Class.forName(name);
	}

	public static Class<?> getClass(String name) {
		try {
			return getClassWithException(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getClassSilent(String name) {
		try {
			return getClassWithException(name);
		} catch (Exception e) {
		}
		return null;
	}

	public static Class<?> getNMSClassWithException(String className) throws Exception {
		return Class.forName("net.minecraft.server." + getVersion() + className);
	}

	public static Class<?> getNMSClass(String className) {
		try {
			return getNMSClassWithException(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getNMSClassSilent(String className) {
		try {
			return getNMSClassWithException(className);
		} catch (Exception e) {
		}
		return null;
	}

	public static Class<?> getOBCClassWithException(String className) throws Exception {
		return Class.forName("org.bukkit.craftbukkit." + getVersion() + className);
	}

	public static Class<?> getOBCClass(String className) {
		try {
			return getOBCClassWithException(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getOBCClassSilent(String className) {
		try {
			return getOBCClassWithException(className);
		} catch (Exception e) {
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
		for (Method m : clazz.getDeclaredMethods())
			if (m.getName().equals(name) && (args.length == 0 && m.getParameterTypes().length == 0
					|| classListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		for (Method m : clazz.getMethods())
			if (m.getName().equals(name) && (args.length == 0 && m.getParameterTypes().length == 0
					|| classListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		return null;
	}

	public static Method getMethodSilent(Class<?> clazz, String name, Class<?>... args) {
		try {
			return getMethod(clazz, name, args);
		} catch (Exception e) {
		}
		return null;
	}

	public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
		if (l1.length != l2.length)
			return false;
		for (int i = 0; i < l1.length; i++)
			if (l1[i] != l2[i])
				return false;
		return true;
	}

	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) {
		for (Constructor<?> c : clazz.getDeclaredConstructors())
			if (args.length == 0 && c.getParameterTypes().length == 0 || classListEqual(args, c.getParameterTypes())) {
				c.setAccessible(true);
				return c;
			}
		for (Constructor<?> c : clazz.getConstructors())
			if (args.length == 0 && c.getParameterTypes().length == 0 || classListEqual(args, c.getParameterTypes())) {
				c.setAccessible(true);
				return c;
			}
		return null;
	}

	public static Constructor<?> getConstructorSilent(Class<?> clazz, Class<?>... args) {
		try {
			return getConstructor(clazz, args);
		} catch (Exception e) {
		}
		return null;
	}

}
