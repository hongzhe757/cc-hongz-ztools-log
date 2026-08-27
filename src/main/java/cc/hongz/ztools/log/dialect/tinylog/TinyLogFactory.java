package cc.hongz.ztools.log.dialect.tinylog;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;

/**
 * <a href="http://www.tinylog.org/">TinyLog</a> log。 <br>
 * 
 * @author Looly
 *
 */
public class TinyLogFactory extends LogFactory {
    
    /**
     * 构造。
     */
    public TinyLogFactory() {
        super("TinyLog");
        checkLogExist(org.pmw.tinylog.Logger.class);
    }

    @Override
    public Log createLog(final String name) {
        return new TinyLog(name);
    }

    @Override
    public Log createLog(final Class<?> clazz) {
        return new TinyLog(clazz);
    }

}
