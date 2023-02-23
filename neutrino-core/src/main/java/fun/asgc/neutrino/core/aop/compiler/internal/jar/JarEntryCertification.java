package fun.asgc.neutrino.core.aop.compiler.internal.jar;

import java.security.CodeSigner;
import java.security.cert.Certificate;

/**
 * {@link Certificate} and {@link CodeSigner} details for a {@link JarEntry} from a signed
 * {@link JarFile}.
 *
 * @author Phillip Webb
 */
class JarEntryCertification {

	static final JarEntryCertification NONE = new JarEntryCertification(null, null);

	private final Certificate[] certificates;

	private final CodeSigner[] codeSigners;

	JarEntryCertification(Certificate[] certificates, CodeSigner[] codeSigners) {
		this.certificates = certificates;
		this.codeSigners = codeSigners;
	}

	Certificate[] getCertificates() {
		return (this.certificates != null) ? this.certificates.clone() : null;
	}

	CodeSigner[] getCodeSigners() {
		return (this.codeSigners != null) ? this.codeSigners.clone() : null;
	}

	static JarEntryCertification from(java.util.jar.JarEntry certifiedEntry) {
		Certificate[] certificates = (certifiedEntry != null) ? certifiedEntry.getCertificates() : null;
		CodeSigner[] codeSigners = (certifiedEntry != null) ? certifiedEntry.getCodeSigners() : null;
		if (certificates == null && codeSigners == null) {
			return NONE;
		}
		return new JarEntryCertification(certificates, codeSigners);
	}

}
