package fun.asgc.neutrino.core.aop.compiler.internal.jar;

/**
 * Utilities for dealing with bytes from ZIP files.
 *
 * @author Phillip Webb
 */
final class Bytes {

	private Bytes() {
	}

	static long littleEndianValue(byte[] bytes, int offset, int length) {
		long value = 0;
		for (int i = length - 1; i >= 0; i--) {
			value = ((value << 8) | (bytes[offset + i] & 0xFF));
		}
		return value;
	}

}
