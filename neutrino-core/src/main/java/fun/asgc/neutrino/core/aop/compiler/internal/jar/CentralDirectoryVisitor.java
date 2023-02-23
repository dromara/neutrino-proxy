package fun.asgc.neutrino.core.aop.compiler.internal.jar;

import fun.asgc.neutrino.core.aop.compiler.internal.data.RandomAccessData;

/**
 * Callback visitor triggered by {@link CentralDirectoryParser}.
 *
 * @author Phillip Webb
 */
interface CentralDirectoryVisitor {

	void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData);

	void visitFileHeader(CentralDirectoryFileHeader fileHeader, long dataOffset);

	void visitEnd();

}
