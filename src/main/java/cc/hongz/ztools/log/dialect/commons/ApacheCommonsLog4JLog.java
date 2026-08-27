package cc.hongz.ztools.log.dialect.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;

import cc.hongz.ztools.log.dialect.log4j.Log4jLog;

/**
 * Apache Commons Logging for Log4j。
 * @author Looly
 *
 */
public class ApacheCommonsLog4JLog extends Log4jLog {
    private static final long serialVersionUID = -6843151523380063975L;
    
    // ------------------------------------------------------------------------- Constructor
    /** 构造。 */
    public ApacheCommonsLog4JLog(final Log logger) {
        super(((Log4JLogger) logger).getLogger());
    }

    /** 构造。 */
    public ApacheCommonsLog4JLog(final Class<?> clazz) {
        super(clazz);
    }

    /** 构造。 */
    public ApacheCommonsLog4JLog(final String name) {
        super(name);
    }
}
