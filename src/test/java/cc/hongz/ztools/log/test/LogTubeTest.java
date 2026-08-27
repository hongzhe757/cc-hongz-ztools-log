package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;
import cc.hongz.ztools.log.dialect.logtube.LogTubeLogFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogTube 日志测试")
public class LogTubeTest {

    @Test
    @DisplayName("测试 LogTube 日志输出")
    public void logTest(){
        LogFactory factory = new LogTubeLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();
        log.debug("LogTube debug test.");
    }
}
