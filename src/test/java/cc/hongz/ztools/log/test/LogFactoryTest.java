package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;
import cc.hongz.ztools.log.dialect.console.ConsoleLogFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 日志工厂单元测试。
 *
 * @author hongz
 */
@DisplayName("日志工厂测试")
public class LogFactoryTest {

    @Test
    @DisplayName("测试自定义日志工厂的设置与获取")
    public void testSetAndGetCurrentLogFactory() {
        LogFactory.setCurrentLogFactory(ConsoleLogFactory.class);
        final LogFactory factory = LogFactory.getCurrentLogFactory();
        assertNotNull(factory);
        assertEquals("Hutool Console Logging", factory.getName());
    }

    @Test
    @DisplayName("测试日志工厂名称")
    public void testGetName() {
        final LogFactory factory = new ConsoleLogFactory();
        assertEquals("Hutool Console Logging", factory.getName());
    }

    @Test
    @DisplayName("测试按名称获取日志对象")
    public void testGetByName() {
        LogFactory.setCurrentLogFactory(ConsoleLogFactory.class);
        final Log log = LogFactory.get("test-logger");
        assertNotNull(log);
        assertEquals("test-logger", log.getName());
    }

    @Test
    @DisplayName("测试按类获取日志对象")
    public void testGetByClass() {
        LogFactory.setCurrentLogFactory(ConsoleLogFactory.class);
        final Log log = LogFactory.get(String.class);
        assertNotNull(log);
        assertEquals(String.class.getName(), log.getName());
    }

    @Test
    @DisplayName("测试日志对象缓存")
    public void testLogCache() {
        final LogFactory factory = new ConsoleLogFactory();
        final Log log1 = factory.getLog("cache-test");
        final Log log2 = factory.getLog("cache-test");
        assertSame(log1, log2);
    }
}
