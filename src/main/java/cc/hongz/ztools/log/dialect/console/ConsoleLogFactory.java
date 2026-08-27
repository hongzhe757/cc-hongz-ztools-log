package cc.hongz.ztools.log.dialect.console;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;

/**
 * 利用System.out.println()打印日志。
 * @author Looly
 *
 */
public class ConsoleLogFactory extends LogFactory {
    
    /** 构造。 */
    public ConsoleLogFactory() {
        super("Hutool Console Logging");
    }

    @Override
    public Log createLog(final String name) {
        return new ConsoleLog(name);
    }

    @Override
    public Log createLog(final Class<?> clazz) {
        return new ConsoleLog(clazz);
    }

}
