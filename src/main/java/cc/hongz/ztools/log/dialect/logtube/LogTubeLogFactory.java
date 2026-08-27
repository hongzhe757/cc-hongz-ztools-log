package cc.hongz.ztools.log.dialect.logtube;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;

/**
 * <a href="https://github.com/logtube/logtube-java">LogTube</a> log. 封装<br>
 *
 * @author Looly
 * @since 5.6.6
 */
public class LogTubeLogFactory extends LogFactory {

    /** 构造。 */
    public LogTubeLogFactory() {
        super("LogTube");
        checkLogExist(io.github.logtube.Logtube.class);
    }

    @Override
    public Log createLog(final String name) {
        return new LogTubeLog(name);
    }

    @Override
    public Log createLog(final Class<?> clazz) {
        return new LogTubeLog(clazz);
    }

}
