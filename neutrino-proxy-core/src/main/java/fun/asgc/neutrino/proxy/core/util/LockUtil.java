package fun.asgc.neutrino.proxy.core.util;

import fun.asgc.neutrino.proxy.core.base.CodeBlock;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class LockUtil {

	/**
	 * 双重校验处理
	 * @param isLock
	 * @param lock
	 * @param lockProcess
	 */
	public static void doubleCheckProcess(BooleanSupplier isLock, Object lock, CodeBlock lockProcess) throws Exception {
		if (isLock.getAsBoolean()) {
			synchronized (lock) {
				if (isLock.getAsBoolean()) {
					lockProcess.execute();
				}
			}
		}
	}

	/**
	 * 双重校验处理
	 * @param isLock
	 * @param lock
	 * @param lockProcess
	 */
	public static void doubleCheckProcessForNoException(BooleanSupplier isLock, Object lock, CodeBlock lockProcess) {
		try {
			doubleCheckProcess(isLock, lock, lockProcess);
		} catch (Exception e) {
			// ignore
			e.printStackTrace();
		}
	}

	/**
	 * 双重校验处理
	 * @param isLock
	 * @param lock
	 * @param lockProcess
	 * @param nonLockProcess
	 */
	public static <T> T doubleCheckProcess(BooleanSupplier isLock, Object lock, CodeBlock lockProcess, Supplier<T> nonLockProcess) throws Exception {
		doubleCheckProcess(isLock, lock, lockProcess);
		return nonLockProcess.get();
	}

	/**
	 * 双重校验处理
	 * @param isLock
	 * @param lock
	 * @param lockProcess
	 * @param nonLockProcess
	 */
	public static <T> T doubleCheckProcessForNoException(BooleanSupplier isLock, Object lock, CodeBlock lockProcess, Supplier<T> nonLockProcess) {
		doubleCheckProcessForNoException(isLock, lock, lockProcess);
		return nonLockProcess.get();
	}
}
