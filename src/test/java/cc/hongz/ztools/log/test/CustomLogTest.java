package cc.hongz.ztools.log.test;

import cc.hongz.ztools.log.Log;
import cc.hongz.ztools.log.LogFactory;
import cc.hongz.ztools.log.dialect.commons.ApacheCommonsLogFactory;
import cc.hongz.ztools.log.dialect.console.ConsoleLogFactory;
import cc.hongz.ztools.log.dialect.jboss.JbossLogFactory;
import cc.hongz.ztools.log.dialect.jdk.JdkLogFactory;
import cc.hongz.ztools.log.dialect.log4j.Log4jLogFactory;
import cc.hongz.ztools.log.dialect.log4j2.Log4j2LogFactory;
import cc.hongz.ztools.log.dialect.slf4j.Slf4jLogFactory;
import cc.hongz.ztools.log.dialect.tinylog.TinyLog2Factory;
import cc.hongz.ztools.log.dialect.tinylog.TinyLogFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 日志门面单元测试
 * @author Looly
 *
 */
@DisplayName("日志门面自定义实现测试")
public class CustomLogTest {

    private static final String LINE = "----------------------------------------------------------------------";

    @Test
    @DisplayName("测试控制台日志输出")
    public void consoleLogTest(){
        LogFactory factory = new ConsoleLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试控制台日志空参数")
    public void consoleLogNullTest(){
        LogFactory factory = new ConsoleLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
    }

    @Test
    @DisplayName("测试 Apache Commons Logging 日志")
    public void commonsLogTest(){
        LogFactory factory = new ApacheCommonsLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试 TinyLog 日志")
    public void tinyLogTest(){
        LogFactory factory = new TinyLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试 TinyLog2 日志")
    public void tinyLog2Test(){
        LogFactory factory = new TinyLog2Factory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试 Log4j2 日志")
    public void log4j2LogTest(){
        LogFactory factory = new Log4j2LogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.debug(null);
        log.debug("This is custom '{}' log\n{}", factory.getName(), LINE);
        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试 Log4j 日志")
    public void log4jLogTest(){
        LogFactory factory = new Log4jLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);

    }

    @Test
    @DisplayName("测试 JBoss Logging 日志")
    public void jbossLogTest(){
        LogFactory factory = new JbossLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试 JDK 日志")
    public void jdkLogTest(){
        LogFactory factory = new JdkLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    @DisplayName("测试 SLF4J 日志")
    public void slf4jTest(){
        LogFactory factory = new Slf4jLogFactory(false);
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String)null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }
}
