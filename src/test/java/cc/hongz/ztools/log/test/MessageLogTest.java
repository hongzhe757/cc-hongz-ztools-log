package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;
import cc.hongz.ztools.log.dialect.log4j2.Log4j2LogFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 报文单独输出单元测试。
 *
 * @author hongz
 */
@DisplayName("报文单独输出测试")
public class MessageLogTest {

    @Test
    @DisplayName("测试请求/响应报文分别单独输出到独立文件")
    public void testMessageLogToSeparateFile() throws Exception {
        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);

        final Log msgIn = LogFactory.get("msg.in");
        final Log msgOut = LogFactory.get("msg.out");
        final Log normal = LogFactory.get("normal");

        // 打请求报文（应进 logs/message-in.log）
        msgIn.info("请求报文：<xml><amount>100.00</amount></xml>");
        // 打响应报文（应进 logs/message-out.log）
        msgOut.info("响应报文：<xml><code>SUCCESS</code></xml>");
        // 打普通日志（应进主日志，不进报文文件）
        normal.info("普通业务日志：订单处理中");

        // 验证请求报文文件：含请求、不含响应
        final String inContent = new String(
                Files.readAllBytes(Paths.get("logs/message-in.log")), StandardCharsets.UTF_8);
        assertTrue(inContent.contains("请求报文"), "message-in.log 应包含请求报文");
        assertFalse(inContent.contains("响应报文"), "message-in.log 不应包含响应报文");

        // 验证响应报文文件：含响应、不含请求
        final String outContent = new String(
                Files.readAllBytes(Paths.get("logs/message-out.log")), StandardCharsets.UTF_8);
        assertTrue(outContent.contains("响应报文"), "message-out.log 应包含响应报文");
        assertFalse(outContent.contains("请求报文"), "message-out.log 不应包含请求报文");

        // 验证主日志文件：含普通业务日志、不含报文
        final String appContent = new String(
                Files.readAllBytes(Paths.get("logs/app.log")), StandardCharsets.UTF_8);
        assertTrue(appContent.contains("普通业务日志"), "app.log 应包含普通业务日志");
        assertFalse(appContent.contains("请求报文"), "app.log 不应包含请求报文");
    }

    @Test
    @DisplayName("测试 JSON 报文内容原样单独输出到独立文件")
    public void testJsonMessageLogToSeparateFile() throws Exception {
        LogFactory.setCurrentLogFactory(Log4j2LogFactory.class);

        final Log msgJson = LogFactory.get("msg.json");
        msgJson.info("{\"amount\":100.00,\"card\":\"****\"}");

        final String content = new String(
                Files.readAllBytes(Paths.get("logs/message.json")), StandardCharsets.UTF_8);
        assertTrue(content.contains("amount"), "message.json 应包含 JSON 报文内容");
        // 报文文件应为纯 JSON 内容，不含日志框架元数据
        assertFalse(content.contains("loggerName"), "message.json 不应包含日志元数据 loggerName");
        assertFalse(content.contains("instant"), "message.json 不应包含日志元数据 instant");
    }
}
