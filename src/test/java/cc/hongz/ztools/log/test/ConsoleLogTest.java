package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.dialect.console.ConsoleLog;
import cc.hongz.ztools.log.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 控制台日志单元测试。
 *
 * @author hongz
 */
@DisplayName("控制台日志测试")
public class ConsoleLogTest {

    @Test
    @DisplayName("测试设置日志级别后的级别判断")
    public void testSetLevelAndIsEnabled() {
        ConsoleLog.setLevel(Level.WARN);
        try {
            final ConsoleLog log = new ConsoleLog("test");
            assertTrue(log.isEnabled(Level.WARN));
            assertTrue(log.isEnabled(Level.ERROR));
            assertFalse(log.isEnabled(Level.INFO));
            assertFalse(log.isEnabled(Level.DEBUG));
        } finally {
            ConsoleLog.setLevel(Level.DEBUG);
        }
    }

    @Test
    @DisplayName("测试日志对象名称")
    public void testGetName() {
        final ConsoleLog log = new ConsoleLog("test-name");
        assertTrue("test-name".equals(log.getName()));
    }
}
