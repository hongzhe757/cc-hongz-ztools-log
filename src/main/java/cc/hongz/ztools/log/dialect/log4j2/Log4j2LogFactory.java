package cc.hongz.ztools.log.dialect.log4j2;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;

/**
 * <a href="http://logging.apache.org/log4j/2.x/index.html">Apache Log4J 2</a> log。 <br>
 * @author Looly
 *
 */
public class Log4j2LogFactory extends LogFactory {
    
    /** 构造。 */
    public Log4j2LogFactory() {
        super("Log4j2");
        checkLogExist(org.apache.logging.log4j.LogManager.class);
    }

    @Override
    public Log createLog(final String name) {
        return new Log4j2Log(name);
    }

    @Override
    public Log createLog(final Class<?> clazz) {
        return new Log4j2Log(clazz);
    }

}
