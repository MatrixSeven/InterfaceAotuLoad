package autoload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
 * @author Seven<p>
 * @date   2016年6月20日-上午9:41:52
 */
public class HelpBase {

	private static HelpBase Instance = null;
	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };

	private HelpBase() {
	}

	public static HelpBase getInstance() {
		if (Instance == null) {
			Instance = new HelpBase();
		}
		return Instance;
	}

	public List<String> list(String path) throws IOException {
		List<String> names = new ArrayList<String>();
		for (URL url : getResources(path)) {
			names.addAll(list(url, path));
		}
		return names;
	}

	protected List<String> listResources(JarInputStream jar, String path) throws IOException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		List<String> resources = new ArrayList<String>();
		for (JarEntry entry; (entry = jar.getNextJarEntry()) != null;) {
			if (!entry.isDirectory()) {
				String name = entry.getName();
				if (!name.startsWith("/")) {
					name = "/" + name;
				}
				if (name.startsWith(path)) {
					resources.add(name.substring(1));
				}
			}
		}
		return resources;
	}

	protected static List<URL> getResources(String path) throws IOException {
		return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
	}

	public List<String> list(URL url, String path) throws IOException {
		InputStream is = null;
		try {
			List<String> resources = new ArrayList<String>();
			URL jarUrl = findJarForResource(url);
			if (jarUrl != null) {
				is = jarUrl.openStream();
				resources = listResources(new JarInputStream(is), path);
			} else {
				List<String> children = new ArrayList<String>();
				try {
					if (isJar(url)) {
						is = url.openStream();
						JarInputStream jarInput = new JarInputStream(is);
						for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null;) {
							children.add(entry.getName());
						}
						jarInput.close();
					} else {
						is = url.openStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						List<String> lines = new ArrayList<String>();
						for (String line; (line = reader.readLine()) != null;) {
							lines.add(line);
							if (getResources(path + "/" + line).isEmpty()) {
								lines.clear();
								break;
							}
						}

						if (!lines.isEmpty()) {
							children.addAll(lines);
						}
					}
				} catch (FileNotFoundException e) {
					if ("file".equals(url.getProtocol())) {
						File file = new File(url.getFile());
						if (file.isDirectory()) {
							children = Arrays.asList(file.list());
						}
					} else {
						throw e;
					}
				}
				String prefix = url.toExternalForm();
				if (!prefix.endsWith("/")) {
					prefix = prefix + "/";
				}
				for (String child : children) {
					String resourcePath = path + "/" + child;
					resources.add(resourcePath);
					URL childUrl = new URL(prefix + child);
					resources.addAll(list(childUrl, resourcePath));
				}
			}

			return resources;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

	protected boolean isJar(URL url, byte[] buffer) {
		InputStream is = null;
		try {
			is = url.openStream();
			is.read(buffer, 0, JAR_MAGIC.length);
			if (Arrays.equals(buffer, JAR_MAGIC)) {
				return true;
			}
		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}

		return false;
	}

	protected URL findJarForResource(URL url) throws MalformedURLException {
		try {
			for (;;) {
				url = new URL(url.getFile());
			}
		} catch (MalformedURLException e) {
		}

		StringBuilder jarUrl = new StringBuilder(url.toExternalForm());
		int index = jarUrl.lastIndexOf(".jar");
		if (index >= 0) {
			jarUrl.setLength(index + 4);
		} else {
			return null;
		}
		try {
			URL testUrl = new URL(jarUrl.toString());
			if (isJar(testUrl)) {
				return testUrl;
			} else {
				jarUrl.replace(0, jarUrl.length(), testUrl.getFile());
				File file = new File(jarUrl.toString());
				if (!file.exists()) {
					try {
						file = new File(URLEncoder.encode(jarUrl.toString(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException("Unsupported encoding?  UTF-8?  That's unpossible.");
					}
				}

				if (file.exists()) {
					testUrl = file.toURI().toURL();
					if (isJar(testUrl)) {
						return testUrl;
					}
				}
			}
		} catch (MalformedURLException e) {
		}

		return null;
	}

	protected boolean isJar(URL url) {
		return isJar(url, new byte[JAR_MAGIC.length]);
	}
}
