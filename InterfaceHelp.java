package autoload;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//=======================================================
//		          .----.
//		       _.'__    `.
//		   .--(^)(^^)---/#\
//		 .' @          /###\
//		 :         ,   #####
//		  `-..__.-' _.-\###/
//		        `;_:    `"'
//		      .'"""""`.
//		     /,  ya ,\\
//		    //狗神保佑  \\
//		    `-._______.-'
//		    ___`. | .'___
//		   (______|______)
//=======================================================
/**
 * 
 * @author Seven
 *         <p>
 * @date 2016年4月19日-上午10:40:17
 */
@SuppressWarnings({ "unchecked"})
public class InterfaceHelp {

	private static final String PACKPATH = InterfaceHelp.class.getName().substring(0,InterfaceHelp.class.getName().lastIndexOf("."));

	/**
	 * getDataClass
	 * 
	 * @param t
	 * @return ArrayList<T> 2016年5月11日-下午1:48:11
	 */
	public static <T> ArrayList<T> getDataClass(T t) {
		return getDataClass(null, t);
	}

	/**
	 * getDataClass
	 * 
	 * @param PackPath
	 * @param t
	 * @return ArrayList<T> 2016年5月11日-下午1:48:00
	 */
	public static <T> ArrayList<T> getDataClass(String PackPath, T t) {
		ArrayList<T> list = new ArrayList<T>();
		try {
			String path = getPackagePath((PackPath == null || PackPath.equals("")) ? PACKPATH : PackPath);
			List<String> children = HelpBase.getInstance().list(path);
			for (String child : children) {
				if (child.endsWith(".class")) {
					String externalName = child.substring(0, child.indexOf('.')).replace('/', '.');
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					Class<?> type = loader.loadClass(externalName);
					if ((((Class<T>) t).isAssignableFrom(type)) && !isABSorINT(type))
						list.add((T) type);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * isABSorINT
	 * 
	 * @param clazz
	 * @return boolean 2016年5月11日-下午1:47:47
	 */
	public static boolean isABSorINT(Class<?> clazz) {
		return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * 
	 * @param clazz_
	 *            接口
	 * @param clazz
	 *            验证
	 * @return boolean 2016年5月24日-上午9:41:26
	 */
	public static boolean IsCanLoadClass(Class<?> clazz_, Class<?> clazz) {
		return clazz_.isAssignableFrom(clazz) && isABSorINT(clazz);
	}

	/**
	 * getPackagePath
	 * 
	 * @param packageName
	 * @return String 2016年5月11日-下午1:47:40
	 */
	private static String getPackagePath(String packageName) {
		return packageName == null ? null : packageName.replace('.', '/');
	}

	/**
	 * getInstance
	 * 
	 * @param clazz
	 * @return T 2016年5月11日-下午1:47:30
	 */
	public static <T> T getInstance(Class<T> clazz) {
		T t = null;
		Constructor<?> constructor;
		try {
			constructor = clazz.getDeclaredConstructor(new Class<?>[] {});
			constructor.setAccessible(true);
			t = (T) constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * getInstance
	 * 
	 * @param clazz
	 * @param parType
	 * @param pars
	 * @return T 2016年5月13日-上午11:00:50
	 */
	public static <T> T getInstance(Class<T> clazz, Class<?>[] parType, Object[] pars) {
		T t = null;
		Constructor<?> constructor;
		try {
			constructor = clazz.getDeclaredConstructor(parType);
			constructor.setAccessible(true);
			t = (T) constructor.newInstance(pars);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * getInstance_
	 * 
	 * @param clazz
	 * @param parType
	 * @param pars
	 * @return T 2016年5月13日-上午11:02:53
	 */
	public static <T> T getInstance_(Class<T> clazz, Class<?>[] parType, Object... pars) {
		return getInstance(clazz, parType, pars);
	}

	/**
	 * getInstance
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 *             T 2016年5月11日-下午1:47:06
	 */
	public static <T> T getInstance(String clazz) {

		T t = null;
		try {
			t = (T) getInstance(Class.forName(clazz));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * CovertType
	 * <li>去除可恶SAX小黄线
	 * <p>
	 * 
	 * @param t
	 * @return T 2016年5月11日-下午1:46:53
	 */
	public static <T> T CovertType(Object t) {
		return (T) t;
	}

	/**
	 * CovertType
	 * <p>
	 * <li>必须继承List
	 * <p>
	 * <li>去除可恶SAX小黄线
	 * <p>
	 * 
	 * @param t
	 * @param type
	 * @return List<T> 2016年5月12日-上午10:54:20
	 */
	public static <T> List<T> CovertType(Object t, T type) {
		return (List<T>) t;
	}

	/**
	 * CovertTypeToSting
	 * 
	 * @param t
	 * @return String 2016年5月11日-下午1:46:46
	 */
	public static <T> String CovertTypeToSting(T t) {
		if (t == null)
			return "";
		if (t instanceof Double)
			return ((Double) t).toString();
		if (t instanceof Long)
			return ((Long) t).toString();
		if (t instanceof Date)
			return new SimpleDateFormat("yyyy-MM-dd").format(CovertType(t));
		return t.toString();
	}

	/**
	 * 尚未测试 返回T
	 * 
	 * @param map
	 * @param clazz
	 * @return T 2016年5月10日-下午3:43:14
	 */
	public static <T> T MapToEntity(HashMap<String, Object> map, Class<T> clazz) {
		T obj = getInstance(clazz);
		try {
			for (Map.Entry<String, Object> m : map.entrySet()) {
				clazz.getField(m.getKey()).setAccessible(true);
				clazz.getField(m.getKey()).set(obj, m.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 尚未测试 返回List<T>
	 * 
	 * @param map
	 * @param clazz
	 * @return List<T> 2016年5月10日-下午3:51:28
	 */
	public static <T> List<T> MapToEntityList(List<HashMap<String, Object>> map, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		for (HashMap<String, Object> tmap : map) {
			list.add(MapToEntity(tmap, clazz));
		}
		return list;
	}

}
