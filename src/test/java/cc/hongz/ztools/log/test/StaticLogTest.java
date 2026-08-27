package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.AbstractLog;
import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;
import cc.hongz.ztools.log.StaticLog;
import cc.hongz.ztools.log.dialect.console.ConsoleColorLogFactory;
import cc.hongz.ztools.log.dialect.console.ConsoleLogFactory;
import cc.hongz.ztools.log.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 静态日志类单元测试。
 *
 * @author hongz
 */
@DisplayName("静态日志类测试")
public class StaticLogTest {

    /**
     * 记录调用参数的日志实现，用于验证 StaticLog 的转发行为。
     */
    private static class RecordingLog extends AbstractLog {
        private String lastFormat;
        private Object[] lastArguments;
        private Throwable lastThrowable;

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
            return true;
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void trace(final String fqcn, final Throwable t, final String format, final Object... arguments) {
            record(format, arguments, t);
        }

        @Override
        public void debug(final String fqcn, final Throwable t, final String format, final Object... arguments) {
            record(format, arguments, t);
        }

        @Override
        public void info(final String fqcn, final Throwable t, final String format, final Object... arguments) {
            record(format, arguments, t);
        }

        @Override
        public void warn(final String fqcn, final Throwable t, final String format, final Object... arguments) {
            record(format, arguments, t);
        }

        @Override
        public void error(final String fqcn, final Throwable t, final String format, final Object... arguments) {
            record(format, arguments, t);
        }

        @Override
        public void log(final String fqcn, final Level level, final Throwable t, final String format, final Object... arguments) {
            record(format, arguments, t);
        }

        private void record(final String format, final Object[] arguments, final Throwable t) {
            this.lastFormat = format;
            this.lastArguments = arguments;
            this.lastThrowable = t;
        }
    }

    /**
     * 返回固定 RecordingLog 的日志工厂。
     */
    private static class RecordingLogFactory extends LogFactory {
        private final RecordingLog log;

        RecordingLogFactory(final RecordingLog log) {
            super("RecordingFactory");
            this.log = log;
        }

        @Override
        public Log createLog(final String name) {
            return this.log;
        }

        @Override
        public Log createLog(final Class<?> clazz) {
            return this.log;
        }
    }

    @Test
    @DisplayName("测试静态日志 debug 转发")
    public void testDebugForward() {
        final RecordingLog log = new RecordingLog();
        LogFactory.setCurrentLogFactory(new RecordingLogFactory(log));
        StaticLog.debug("msg: {}", "param");
        assertEquals("msg: {}", log.lastFormat);
        assertArrayEquals(new Object[]{"param"}, log.lastArguments);
        assertNull(log.lastThrowable);
    }

    @Test
    @DisplayName("测试静态日志 warn 转发异常对象")
    public void testWarnForwardWithThrowable() {
        final RecordingLog log = new RecordingLog();
        LogFactory.setCurrentLogFactory(new RecordingLogFactory(log));
        final Exception e = new Exception("test");
        StaticLog.warn(e, "warn msg");
        assertSame(e, log.lastThrowable);
        assertEquals("warn msg", log.lastFormat);
    }

    @Test
    @DisplayName("测试静态日志输出")
    public void test() {
        LogFactory.setCurrentLogFactory(ConsoleLogFactory.class);
        StaticLog.debug("This is static {} log", "debug");
        StaticLog.info("This is static {} log", "info");
    }

    @Test
    @DisplayName("测试静态彩色日志输出")
    public void colorTest() {
        LogFactory.setCurrentLogFactory(ConsoleColorLogFactory.class);
        StaticLog.debug("This is static {} log", "debug");
        StaticLog.info("This is static {} log", "info");
        StaticLog.error("This is static {} log", "error");
        StaticLog.warn("This is static {} log", "warn");
        StaticLog.trace("This is static {} log", "trace");
    }
}
