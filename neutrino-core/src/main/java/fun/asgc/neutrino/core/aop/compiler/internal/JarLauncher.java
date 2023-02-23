package fun.asgc.neutrino.core.aop.compiler.internal;

import fun.asgc.neutrino.core.aop.compiler.internal.archive.Archive;
import fun.asgc.neutrino.core.aop.compiler.internal.archive.ExplodedArchive;
import fun.asgc.neutrino.core.aop.compiler.internal.archive.JarFileArchive;

import java.io.File;

/**
 * {@link Launcher} for JAR based archives. This launcher assumes that dependency jars are
 * included inside a {@code /BOOT-INF/lib} directory and that application classes are
 * included inside a {@code /BOOT-INF/classes} directory.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Madhura Bhave
 * @author Scott Frederick
 * @since 1.0.0
 */
public class JarLauncher extends ExecutableArchiveLauncher {

	private String path;

	static final Archive.EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
		if (entry.isDirectory()) {
			return entry.getName().equals("BOOT-INF/classes/");
		}
		return entry.getName().startsWith("BOOT-INF/lib/");
	};

	public JarLauncher(String path) {
		this.path = path;
		try {
			this.setArchive(createArchive(this.path));
			this.setClassPathIndex(getClassPathIndex(this.getArchive()));
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public JarLauncher(ClassLoader classLoader, String path) {
		this.path = path;
		try {
			this.setArchive(createArchive(this.path));
			this.setClassPathIndex(getClassPathIndex(this.getArchive()));
			this.setClassLoader(classLoader);
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public ClassLoader createClassLoader() throws Exception {
		return createClassLoader(getClassPathArchivesIterator());
	}

	protected final Archive createArchive(String path) throws Exception {
		if (path == null) {
			throw new IllegalStateException("Unable to determine code source archive");
		}
		File root = new File(path);
		if (!root.exists()) {
			throw new IllegalStateException("Unable to determine code source archive from " + root);
		}
		return (root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root));
	}

	protected JarLauncher(Archive archive) {
		super(archive);
	}

	@Override
	protected boolean isPostProcessingClassPathArchives() {
		return false;
	}

	@Override
	protected boolean isNestedArchive(Archive.Entry entry) {
		return NESTED_ARCHIVE_ENTRY_FILTER.matches(entry);
	}

	@Override
	protected String getArchiveEntryPathPrefix() {
		return "BOOT-INF/";
	}

}
