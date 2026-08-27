package cc.hongz.ztools.log.dialect.console;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;

/**
 * 利用System.out.println()打印彩色日志。
 *
 * @author hongda.li
 * @since 5.8.0
 */
public class ConsoleColorLogFactory extends LogFactory {

    /** 构造。 */
    public ConsoleColorLogFactory() {
        super("Hutool Console Color Logging");
    }

    @Override
    public Log createLog(final String name) {
        return new ConsoleColorLog(name);
    }

    @Override
    public Log createLog(final Class<?> clazz) {
        return new ConsoleColorLog(clazz);
    }
}
