package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 日志级别枚举单元测试。
 *
 * @author hongz
 */
@DisplayName("日志级别枚举测试")
public class LevelTest {

    @Test
    @DisplayName("测试日志级别从低到高的顺序")
    public void testLevelOrder() {
        assertTrue(Level.ALL.ordinal() < Level.TRACE.ordinal());
        assertTrue(Level.TRACE.ordinal() < Level.DEBUG.ordinal());
        assertTrue(Level.DEBUG.ordinal() < Level.INFO.ordinal());
        assertTrue(Level.INFO.ordinal() < Level.WARN.ordinal());
        assertTrue(Level.WARN.ordinal() < Level.ERROR.ordinal());
        assertTrue(Level.ERROR.ordinal() < Level.FATAL.ordinal());
        assertTrue(Level.FATAL.ordinal() < Level.OFF.ordinal());
    }

    @Test
    @DisplayName("测试日志级别比较")
    public void testLevelCompare() {
        assertTrue(Level.DEBUG.compareTo(Level.INFO) < 0);
        assertTrue(Level.ERROR.compareTo(Level.WARN) > 0);
        assertEquals(0, Level.INFO.compareTo(Level.INFO));
    }

    @Test
    @DisplayName("测试日志级别取值")
    public void testValueOf() {
        assertEquals(Level.DEBUG, Level.valueOf("DEBUG"));
        assertEquals(Level.ERROR, Level.valueOf("ERROR"));
    }
}
