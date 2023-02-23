package fun.asgc.neutrino.core.aop.compiler.internal.jar;

import fun.asgc.neutrino.core.aop.compiler.internal.data.RandomAccessData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the central directory from a JAR file.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @see CentralDirectoryVisitor
 */
class CentralDirectoryParser {

	private static final int CENTRAL_DIRECTORY_HEADER_BASE_SIZE = 46;

	private final List<CentralDirectoryVisitor> visitors = new ArrayList<>();

	<T extends CentralDirectoryVisitor> T addVisitor(T visitor) {
		this.visitors.add(visitor);
		return visitor;
	}

	/**
	 * Parse the source data, triggering {@link CentralDirectoryVisitor visitors}.
	 * @param data the source data
	 * @param skipPrefixBytes if prefix bytes should be skipped
	 * @return the actual archive data without any prefix bytes
	 * @throws IOException on error
	 */
	RandomAccessData parse(RandomAccessData data, boolean skipPrefixBytes) throws IOException {
		CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
		if (skipPrefixBytes) {
			data = getArchiveData(endRecord, data);
		}
		RandomAccessData centralDirectoryData = endRecord.getCentralDirectory(data);
		visitStart(endRecord, centralDirectoryData);
		parseEntries(endRecord, centralDirectoryData);
		visitEnd();
		return data;
	}

	private void parseEntries(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData)
			throws IOException {
		byte[] bytes = centralDirectoryData.read(0, centralDirectoryData.getSize());
		CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
		int dataOffset = 0;
		for (int i = 0; i < endRecord.getNumberOfRecords(); i++) {
			fileHeader.load(bytes, dataOffset, null, 0, null);
			visitFileHeader(dataOffset, fileHeader);
			dataOffset += CENTRAL_DIRECTORY_HEADER_BASE_SIZE + fileHeader.getName().length()
					+ fileHeader.getComment().length() + fileHeader.getExtra().length;
		}
	}

	private RandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, RandomAccessData data) {
		long offset = endRecord.getStartOfArchive(data);
		if (offset == 0) {
			return data;
		}
		return data.getSubsection(offset, data.getSize() - offset);
	}

	private void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
		for (CentralDirectoryVisitor visitor : this.visitors) {
			visitor.visitStart(endRecord, centralDirectoryData);
		}
	}

	private void visitFileHeader(long dataOffset, CentralDirectoryFileHeader fileHeader) {
		for (CentralDirectoryVisitor visitor : this.visitors) {
			visitor.visitFileHeader(fileHeader, dataOffset);
		}
	}

	private void visitEnd() {
		for (CentralDirectoryVisitor visitor : this.visitors) {
			visitor.visitEnd();
		}
	}

}
