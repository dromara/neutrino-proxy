package fun.asgc.neutrino.core.aop.compiler.internal;


import fun.asgc.neutrino.core.aop.compiler.internal.archive.Archive;
import fun.asgc.neutrino.core.aop.compiler.internal.archive.JarFileArchive;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link ClassLoader} used by the {@link Launcher}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @since 1.0.0
 */
public class SpringBootJarClassLoader extends URLClassLoader {
	protected static final String BOOT_CLASSPATH_INDEX_ATTRIBUTE = "Spring-Boot-Classpath-Index";
	protected static final String DEFAULT_CLASSPATH_INDEX_FILE_NAME = "classpath.idx";

	static {
		ClassLoader.registerAsParallelCapable();
	}

	/**
	 * Create a new {@link SpringBootJarClassLoader} instance.
	 * @param parent the parent class loader for delegation
	 */
	public SpringBootJarClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}

	/**
	 * Create a new {@link SpringBootJarClassLoader} instance.
	 */
	public SpringBootJarClassLoader() {
		super(new URL[0], ClassLoader.getSystemClassLoader());
	}

	public void addJar(String path) throws Exception {
		Archive archive = createArchive(path);
		Iterator<Archive> archives = getClassPathArchivesIterator(archive);
		List<URL> urls = new ArrayList<>(50);
		while (archives.hasNext()) {
			urls.add(archives.next().getUrl());
		}
		urls.forEach(url -> addURL(url));
	}

	private Archive createArchive(String path) throws Exception {
		if (path == null) {
			throw new IllegalStateException("Unable to determine code source archive");
		}
		File root = new File(path);
		if (!root.exists()) {
			throw new IllegalStateException("Unable to determine code source archive from " + root);
		}
		return new JarFileArchive(root);
	}

	protected Iterator<Archive> getClassPathArchivesIterator(Archive archive) throws Exception {
		Archive.EntryFilter searchFilter = this::isSearchCandidate;
		Iterator<Archive> archives = archive.getNestedArchives(searchFilter,
				(entry) -> isNestedArchive(entry));
		archives = applyClassPathArchivePostProcessing(archives);
		return archives;
	}

	private Iterator<Archive> applyClassPathArchivePostProcessing(Iterator<Archive> archives) throws Exception {
		List<Archive> list = new ArrayList<>();
		while (archives.hasNext()) {
			list.add(archives.next());
		}
		return list.iterator();
	}

	static final Archive.EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
		if (entry.isDirectory()) {
			return entry.getName().equals("BOOT-INF/classes/");
		}
		return entry.getName().startsWith("BOOT-INF/lib/");
	};

	protected boolean isNestedArchive(Archive.Entry entry) {
		return NESTED_ARCHIVE_ENTRY_FILTER.matches(entry);
	}

	/**
	 * Determine if the specified entry is a candidate for further searching.
	 * @param entry the entry to check
	 * @return {@code true} if the entry is a candidate for further searching
	 * @since 2.3.0
	 */
	protected boolean isSearchCandidate(Archive.Entry entry) {
		if (getArchiveEntryPathPrefix() == null) {
			return true;
		}
		return entry.getName().startsWith(getArchiveEntryPathPrefix());
	}

	protected String getArchiveEntryPathPrefix() {
		return "BOOT-INF/";
	}
}
