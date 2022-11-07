package fun.asgc.neutrino.core.db.crisp.tx;

import fun.asgc.neutrino.core.db.crisp.JdbcException;

/**
 * @author: aoshiguchen
 * @date: 2022/11/7
 */
public class TransactionException extends JdbcException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
