/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.EntryFilter;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;

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
public class MyJarLauncher extends ExecutableArchiveLauncher {

	private String path;

	static final EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
		if (entry.isDirectory()) {
			return entry.getName().equals("BOOT-INF/classes/");
		}
		return entry.getName().startsWith("BOOT-INF/lib/");
	};

	public MyJarLauncher(String path) {
		this.path = path;
		try {
			this.setArchive(createArchive(this.path));
			this.setClassPathIndex(getClassPathIndex(this.getArchive()));
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

	protected MyJarLauncher(Archive archive) {
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

//	public static void main(String[] args) throws Exception {
//		new MyJarLauncher().launch(args);
//	}

}
