package fun.asgc.neutrino.core.db.crisp;

import fun.asgc.neutrino.core.exception.InternalException;

/**
 * @author: aoshiguchen
 * @date: 2022/11/7
 */
public class JdbcException extends InternalException {

    public JdbcException(String message) {
        super(message);
    }

    public JdbcException(String message, Throwable cause) {
        super(message, cause);
    }
}
