package cc.hongz.ztools.log.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;
import cc.hongz.ztools.log.level.Level;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志门面单元测试
 * @author Looly
 *
 */
@DisplayName("日志门面统一接口测试")
public class LogTest {

    @Test
    @DisplayName("测试自动选择日志实现")
    public void logTest(){
        Log log = LogFactory.get();

        // 自动选择日志实现
        log.debug("This is {} log", Level.DEBUG);
        log.info("This is {} log", Level.INFO);
        log.warn("This is {} log", Level.WARN);

//        Exception e = new Exception("test Exception");
//        log.error(e, "This is {} log", Level.ERROR);
    }

    /**
     * 兼容slf4j日志消息格式测试，即第二个参数是异常对象时正常输出异常信息
     */
    @Test
    @DisplayName("测试 slf4j 异常消息格式")
    @Disabled
    public void logWithExceptionTest() {
        Log log = LogFactory.get();
        Exception e = new Exception("test Exception");
        log.error("我是错误消息", e);
    }

    @Test
    @DisplayName("测试空参数日志")
    public void logNullTest(){
        final Log log = Log.get();
        log.debug(null);
        log.info(null);
        log.warn(null);
    }

    @Test
    @DisplayName("测试参数化消息边界情况")
    public void parameterizedMessageEdgeCasesTest() {
        Log log = LogFactory.get();

        // 测试不同数量的参数
        log.info("No parameters");
        log.info("One: {}", "param1");
        log.info("Two: {} and {}", "param1", "param2");
        log.info("Three: {}, {}, {}", "param1", "param2", "param3");
        log.info("Four: {}, {}, {}, {}", "param1", "param2", "param3", "param4");

        // 测试参数不足的情况
        log.info("Missing param: {} and {}", "only_one");

        // 测试参数过多的情况
        log.info("Extra param: {}", "param1", "extra_param");
    }

    @Test
    @DisplayName("测试国际化消息")
    public void i18nMessageTest() {
        Log log = LogFactory.get();
        // 国际化消息测试
        log.info("中文消息测试");
        log.info("Message with unicode: {}", "特殊字符©®™✓✗★☆");
        log.info("多语言混排: 中文, English, 日本語, 한글");
        log.info("Emoji测试: 😀🚀🌏");
    }

    @Test
    @DisplayName("测试复杂对象参数")
    public void complexObjectTest() {
        Log log = LogFactory.get();
        // 复杂对象参数测试
        List<String> list = Arrays.asList("item1", "item2");
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");

        log.info("List: {}", list);
        log.info("Map: {}", map);
        log.info("Null object: {}", (Object)null);
    }
}
