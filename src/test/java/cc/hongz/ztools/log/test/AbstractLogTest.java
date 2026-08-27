package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.AbstractLog;
import cc.hongz.ztools.log.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 抽象日志类单元测试。
 *
 * @author hongz
 */
@DisplayName("抽象日志类测试")
public class AbstractLogTest {

    /**
     * 记录调用参数的日志实现，用于验证 AbstractLog 的分支逻辑。
     */
    private static class RecordingLog extends AbstractLog {
        private Throwable lastThrowable;
        private String lastFormat;

        @Override
        public String getName() {
            return "RecordingLog";
        }

        @Override
        public boolean isTraceEnabled() {
            return true;
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void trace(final String fqcn, final Throwable t, final String format, final Object... arguments) {
        }

        @Override
        public void debug(final String fqcn, final Throwable t, final String format, final Object... arguments) {
            this.lastThrowable = t;
            this.lastFormat = format;
        }

        @Override
        public void info(final String fqcn, final Throwable t, final String format, final Object... arguments) {
        }

        @Override
        public void warn(final String fqcn, final Throwable t, final String format, final Object... arguments) {
        }

        @Override
        public void error(final String fqcn, final Throwable t, final String format, final Object... arguments) {
        }

        @Override
        public void log(final String fqcn, final Level level, final Throwable t, final String format, final Object... arguments) {
        }
    }

    @Test
    @DisplayName("测试 slf4j 兼容：单个 Throwable 参数被识别为异常")
    public void testSlf4jCompatibleThrowable() {
        final RecordingLog log = new RecordingLog();
        final Exception e = new Exception("test-exception");
        log.debug("error msg: {}", e);
        assertSame(e, log.lastThrowable);
        assertEquals("error msg: {}", log.lastFormat);
    }

    @Test
    @DisplayName("测试普通字符串参数不被识别为异常")
    public void testNormalStringArg() {
        final RecordingLog log = new RecordingLog();
        log.debug("msg: {}", "hello");
        assertNull(log.lastThrowable);
        assertEquals("msg: {}", log.lastFormat);
    }

    @Test
    @DisplayName("测试 isEnabled 级别映射")
    public void testIsEnabled() {
        final RecordingLog log = new RecordingLog();
        assertTrue(log.isEnabled(Level.DEBUG));
        assertFalse(log.isEnabled(Level.INFO));
        assertFalse(log.isEnabled(Level.ERROR));
    }
}
