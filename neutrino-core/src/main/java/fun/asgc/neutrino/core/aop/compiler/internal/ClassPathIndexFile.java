package fun.asgc.neutrino.core.aop.compiler.internal;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class path index file that provides ordering information for JARs.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
final class ClassPathIndexFile {

	private final File root;

	private final List<String> lines;

	private ClassPathIndexFile(File root, List<String> lines) {
		this.root = root;
		this.lines = lines.stream().map(this::extractName).collect(Collectors.toList());
	}

	private String extractName(String line) {
		if (line.startsWith("- \"") && line.endsWith("\"")) {
			return line.substring(3, line.length() - 1);
		}
		throw new IllegalStateException("Malformed classpath index line [" + line + "]");
	}

	int size() {
		return this.lines.size();
	}

	boolean containsEntry(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		return this.lines.contains(name);
	}

	List<URL> getUrls() {
		return this.lines.stream().map(this::asUrl).collect(Collectors.toList());
	}

	private URL asUrl(String line) {
		try {
			return new File(this.root, line).toURI().toURL();
		}
		catch (MalformedURLException ex) {
			throw new IllegalStateException(ex);
		}
	}

	static ClassPathIndexFile loadIfPossible(URL root, String location) throws IOException {
		return loadIfPossible(asFile(root), location);
	}

	private static ClassPathIndexFile loadIfPossible(File root, String location) throws IOException {
		return loadIfPossible(root, new File(root, location));
	}

	private static ClassPathIndexFile loadIfPossible(File root, File indexFile) throws IOException {
		if (indexFile.exists() && indexFile.isFile()) {
			try (InputStream inputStream = new FileInputStream(indexFile)) {
				return new ClassPathIndexFile(root, loadLines(inputStream));
			}
		}
		return null;
	}

	private static List<String> loadLines(InputStream inputStream) throws IOException {
		List<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		String line = reader.readLine();
		while (line != null) {
			if (!line.trim().isEmpty()) {
				lines.add(line);
			}
			line = reader.readLine();
		}
		return Collections.unmodifiableList(lines);
	}

	private static File asFile(URL url) {
		if (!"file".equals(url.getProtocol())) {
			throw new IllegalArgumentException("URL does not reference a file");
		}
		try {
			return new File(url.toURI());
		}
		catch (URISyntaxException ex) {
			return new File(url.getPath());
		}
	}

}
