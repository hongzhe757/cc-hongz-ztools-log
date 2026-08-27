# 日志能力分析与改进建议

> 分析对象：`cc-hongz-ztools-log`（对齐 hutool-log 的日志封装模块）
> 目标：梳理当前日志能力的短板，并给出面向金融 / 银行等对日志有强监管、强检索诉求场景的改进方向。

---

## 1. 现状概述

`cc-hongz-ztools-log` 是一套**日志门面（Facade）**，通过统一的 `Log` 接口屏蔽底层日志框架差异，运行时按依赖自动探测（SPI）或手动指定具体的日志实现。

- 门面入口：`Log` / `AbstractLog` / `StaticLog` / `LogFactory`
- 已适配的方言（`dialect/` 下 9 个）：`console`、`jdk`、`log4j`、`log4j2`、`slf4j`、`commons`、`jboss`、`tinylog`、`logtube`
- 探测优先级（SPI）：logtube → slf4j → log4j2 → log4j → commons → tinylog → tinylog2 → jboss，最终兜底 `console` / `jdk`

当前能力本质是「**能打印**」，但缺少「**能分类、能检索、能观测**」的能力。下文逐项分析短板与改进方向。

---

## 2. 特殊形式日志输出（报文 / 请求入参出参隔离）

### 2.1 痛点

金融、银行、支付等场景中，日志不止业务流水，还包括大量「**敏感、结构化、需单独归档**」的内容：

- 请求 / 响应**报文**（接口 XML、JSON、8583 报文、ISO20022 报文等）
- 请求的**入参、出参**、耗时、返回码
- 交易流水号、客户号、机构号等**业务上下文**

当前这些日志和普通业务日志**混在一条流里**，问题突出：

1. **无法单独归档**：报文往往有独立的留存期、权限、脱敏要求，混在一起无法按文件分离。
2. **检索困难**：在海量日志里找一条特定报文的入参出参，只能靠关键字硬筛，效率极低。
3. **无法结构化**：纯文本日志难以被下游解析、入库、做交易链路追踪。

### 2.2 解决思路

门面本身已支持 `LogFactory.get(String name)` 自定义 logger 名，这是分类的入口。可在此之上叠加：

| 手段 | 说明 |
|------|------|
| **Logger 命名分层** | 按业务域命名：`trade.order`、`trade.pay`、`msg.in`、`msg.out`，而非统一一个 logger |
| **独立 Appender** | 在 log4j2 / logback 配置里把 `msg.*` 路由到独立的 `message.log`、`request.log`，实现「报文单独成文件」 |
| **MDC（Mapped Diagnostic Context）** | 给每条日志附加 `traceId` / `transNo` / `userId` / `msgType`，检索与链路追踪靠它 |
| **结构化输出** | 输出 JSON（logtube 已是 JSON 结构），字段化 `request/response/cost/retCode` |
| **报文脱敏** | 卡号、身份证、手机号在落盘前做掩码，可结合 ztools 的 `StrUtil.hide` / `DesensitizedUtil` |
| **异步落盘** | 大报文走 Log4j2 AsyncLogger / Logback AsyncAppender，避免阻塞主流程 |

**结论**：门面层不改，靠「logger 分层 + 独立 appender + MDC + 结构化 + 脱敏」即可满足报文 / 入参出参的单独输出与检索，核心工作在下游框架配置与业务侧约定。

---

## 3. 日志搜集

「日志搜集」指把分布在多台机器、多个进程里的日志集中采集、存储、检索。Linux 生态主流方案如下。

### 3.1 采集器（Agent）

| 采集器 | 特点 |
|--------|------|
| **Filebeat** | Elastic 官方，轻量，专做文件日志采集（tail + 增量读取） |
| **Fluent Bit** | 极轻量，适合容器 / 边缘场景 |
| **Fluentd** | 功能强、插件多，偏重聚合与路由 |
| **Logstash** | 重，擅长解析、过滤、多源汇聚，常与 Filebeat 搭配 |
| **rsyslog / syslog-ng** | 系统级，syslog 协议，适合传统 Linux 服务 |
| **journald** | systemd 自带，采集 systemd 服务的 stdout / stderr |

### 3.2 存储与检索（集中式日志平台）

- **ELK / EFK**：Elasticsearch + Logstash/Fluentd + Kibana，最主流。
- **Loki + Grafana**：轻量、按标签索引，与 Prometheus 生态整合，成本低。
- **ClickHouse**：海量日志高吞吐、低成本分析，常用于自建日志平台。

### 3.3 采集前提

要让日志「可搜集」，程序侧需要配合：

1. **写文件而非仅 stdout**：落盘到固定目录 + 滚动策略（size/time 滚动）。
2. **结构化 / 统一格式**：JSON 一行一条，字段规整，采集器好解析。
3. **带上链路 ID**：`traceId` / `spanId` 贯穿调用链，跨服务检索。

---

## 4. 日志注释补充

### 4.1 现状问题

源码里与「日志定位」相关的注释过于简略，例如各 dialect 里大量出现的：

```java
// fqcn 无效
// fqcn此处无效
```

含义不直观，容易让后续维护者困惑「为什么无效、影响是什么」。建议补齐注释，明确「该实现是否支持通过 FQCN 精确定位日志位置，以及不支持时的替代行为」。

### 4.2 建议补充的注释类型

- 类级说明：该方言是否支持 FQCN 定位、是否支持 MDC、是否支持异步。
- 关键方法：`log(fqcn, level, ...)` 中 `fqcn` 参数的作用与「无效」的原因。
- 彩色输出：颜色映射规则、可扩展点。

---

## 5. 「// fqcn 无效」是什么意思

### 5.1 FQCN 是什么

**FQCN = Fully Qualified Class Name，完全限定类名。**

日志框架打印日志时，通常要输出「**哪个类、第几行、哪个方法**」打出来的，例如：

```
[16:40:43][INFO] cc.hongz.ztools.log.test.LogTest:27 - This is INFO log
                                          ^^^^^^^^^ 类名   ^^ 行号
```

问题在于：通过门面 / 包装类打印时，调用栈里会夹着门面自己的帧（`AbstractLog`、`StaticLog` 等）。如果不加处理，日志框架会把位置记成「门面类内部」，而非真正的业务代码。

### 5.2 fqcn 参数的机制

以 `Log.log(String fqcn, Level level, ...)` 为例：调用方把**门面类自己的类名**当作 `fqcn` 传进去（见 `AbstractLog` 中的 `FQCN = AbstractLog.class.getName()`）。底层日志框架据此在堆栈里：

1. 找到第一个「类名 == fqcn」的帧（即门面那一帧）；
2. 再往上取一帧，得到真正调用门面的**业务代码位置**。

`JdkLog.fillCallerData(callerFQCN, record)` 就是手动实现了这个过程（逐帧匹配 `callerFQCN` 后取上一帧）。

### 5.3 为什么有的实现「无效」

| 实现 | FQCN 是否有效 | 原因 |
|------|:---:|------|
| Log4j `logger.log(fqcn, ...)` | ✅ | 原生接收 fqcn 定位 |
| Log4j2 `AbstractLogger.logIfEnabled(fqcn, ...)` | ✅ | 原生接收 fqcn |
| Slf4j `LocationAwareLogger.log(null, fqcn, ...)` | ✅ | 原生接收 fqcn |
| Jboss `logger.trace(fqcn, ...)` | ✅ | 原生接收 fqcn |
| Jdk `LogRecord` + `fillCallerData` | ✅ | 手动用 fqcn 填充位置 |
| **Apache Commons Logging** | ❌ | `logger.trace(msg, t)` 不接收 fqcn，位置指向门面自身 |
| **Console** | ❌ | `System.out.println` 无「位置」概念 |
| **LogTube** | ❌ | 用固定堆栈深度 `getStackElement(6)` 定位，不用 fqcn |

因此「`// fqcn 无效`」的准确含义是：**这个日志实现不接收 / 不处理 fqcn 参数，打印出来的位置信息无法精确定位到业务代码（或根本不输出位置）**。这是各底层框架能力的差异，门面无法替它补上。

### 5.4 能否让 fqcn 生效

fqcn 定位的实质是「门面自己算位置」——第 2 步「在堆栈里找」门面自己也能做（`JdkLog.fillCallerData` 就是手动实现）。所以「无效」的真正原因是**这些实现没有能接收「位置结果」的入口**，分三种情况：

| 实现 | 能否生效 | 做法 | 价值 |
|------|:---:|------|------|
| **Console** | ✅ | `log()` 里手动定位 + 拼进格式 | 中（本地调试友好） |
| **LogTube** | ⚠️ | 已用固定深度 `getStackElement(6)` 定位（实测正确，无需改）；fqcn 匹配反而会错（见下） | — |
| **JCL** | ❌ | 接口不接受位置参数，只能拼消息污染内容 | 低，不建议 |

通用思路：抽一个 `getCaller(fqcn)` 方法（本质就是 `JdkLog.fillCallerData` 的逻辑），**从栈底往上**遍历找「类名 == fqcn」的**最后一帧**，再取上一帧 = 业务代码位置。关键坑：`ExceptionUtil.getStackElement(fqcn, i)` 底层用 `ArrayUtil.matchIndex` 是**从前往后找第一帧**，当 `AbstractLog` 有多个重载帧（如 `debug(String)` → `debug(Throwable)` 两个重载）时会定位到门面内部而非业务代码，因此不能直接拿来替代固定深度；正确匹配必须像 `JdkLog.fillCallerData` 那样**从后往前**。

---

## 6. 控制台彩色输出拓展

### 6.1 现状

`ConsoleColorLog` 已经实现了**按日志级别着色**：

| 级别 | 颜色 |
|------|------|
| DEBUG / INFO | 绿色 GREEN |
| WARN | 黄色 YELLOW |
| ERROR | 红色 RED |
| TRACE | 品红 MAGENTA |
| 其他 | 默认色 |

实现位于 `ConsoleColorLog.colorFactory`（`Level → AnsiColor` 的映射函数），通过 `setColorFactory(Function<Level, AnsiColor>)` 可替换。

### 6.2 拓展方向

当前只按「级别」着色，粒度较粗。可考虑：

1. **按关键字高亮**：金额、卡号、交易号、`SUCCESS`/`FAIL`、异常堆栈等关键信息用醒目色。
2. **分区域着色**：时间戳、级别、类名、消息体各用不同颜色（`ConsoleColorLog` 已对类名 / 时间用 `COLOR_CLASSNAME` / `COLOR_TIME`，可继续细分）。
3. **按级别阈值着色 + 加粗 / 闪烁**：ERROR 加粗、告警闪烁（需终端支持）。
4. **主题化**：深色 / 浅色终端切换，或跟随环境变量。
5. **可配置开关**：生产环境默认关闭 ANSI（避免日志文件里混入转义符），仅开发 / 本地开启。

核心扩展点就是 `colorFactory`：把「级别 → 颜色」扩展为「级别 + 消息内容 → 颜色/样式」的规则。

### 6.3 详细实现方案

#### 6.3.1 颜色总开关（生产默认关闭）

生产环境 `System.out` 常被重定向到文件 / 采集器，此时输出 ANSI 就是污染。需要一个「启用判定」，判定顺序（显式 > 协议 > 终端检测）：

```java
private static volatile boolean colorEnabled = detectColorSupport();

private static boolean detectColorSupport() {
    // 1. 显式系统属性（最高优先级）：-Dcc.hongz.ztools.log.color=true/false
    final String prop = System.getProperty("cc.hongz.ztools.log.color");
    if (null != prop) {
        return Boolean.parseBoolean(prop);
    }
    // 2. NO_COLOR 环境变量（https://no-color.org 标准协议）
    if (null != System.getenv("NO_COLOR")) {
        return false;
    }
    // 3. 是否交互式终端（TTY）
    return null != System.console();
}

public static void setEnabled(final boolean enabled) {
    colorEnabled = enabled;
}
```

`log()` 里加一行退化逻辑，非彩色时走 `ConsoleLog` 纯文本：

```java
if (false == colorEnabled) {
    super.log(fqcn, level, t, format, arguments);  // 直接走 ConsoleLog 纯文本
    return;
}
```

这样 `java App > app.log`（非 TTY）时自动输出纯文本，`tail -f` 干净；本地直跑终端才上色。

#### 6.3.2 关键字高亮

消息体的着色不能在模板里做（`%s` 是整段消息），要在**格式化之后、输出之前**对消息文本做正则替换。设计一个「规则列表」，可扩展：

```java
/** 关键字高亮规则：正则 → 颜色 */
private static final List<HighlightRule> HIGHLIGHT_RULES = Arrays.asList(
        new HighlightRule("\\b(FAIL|FAILED|ERROR|Exception|异常|失败)\\b", AnsiColor.BRIGHT_RED),
        new HighlightRule("\\b(SUCCESS|OK|成功)\\b", AnsiColor.BRIGHT_GREEN),
        new HighlightRule("\\d{16,19}", AnsiColor.BRIGHT_RED),                    // 卡号
        new HighlightRule("\\d+(?:\\.\\d{1,2})?(?=\\s*(元|美元|CNY|USD|￥))", AnsiColor.BRIGHT_YELLOW)  // 金额
);

/** 高亮规则：正则 + 颜色 */
private static final class HighlightRule {
    final Pattern pattern;
    final AnsiColor color;
    HighlightRule(final String regex, final AnsiColor color) {
        this.pattern = Pattern.compile(regex);
        this.color = color;
    }
}

/** 对消息文本做关键字高亮，返回插入 ANSI 码后的文本 */
private static String highlight(final String msg) {
    String result = msg;
    for (final HighlightRule rule : HIGHLIGHT_RULES) {
        result = applyRule(result, rule);
    }
    return result;
}

private static String applyRule(final String msg, final HighlightRule rule) {
    final Matcher matcher = rule.pattern.matcher(msg);
    final StringBuffer sb = new StringBuffer();
    final String reset = AnsiEncoder.encode(AnsiColor.DEFAULT);
    while (matcher.find()) {
        // 注意：appendReplacement 里 \ 和 $ 要 quote，否则 ANSI 码里的 \033 会被当转义
        matcher.appendReplacement(sb, Matcher.quoteReplacement(AnsiEncoder.encode(rule.color) + matcher.group() + reset));
    }
    matcher.appendTail(sb);
    return sb.toString();
}
```

#### 6.3.3 改造后的 `log()` 全貌

```java
@Override
public synchronized void log(final String fqcn, final Level level, final Throwable t, final String format,
        final Object... arguments) {
    if (false == isEnabled(level)) {
        return;
    }
    // 非彩色环境退化为纯文本
    if (false == colorEnabled) {
        super.log(fqcn, level, t, format, arguments);
        return;
    }
    // 1. 先格式化，得到完整消息
    final String msg = StrUtil.format(format, arguments);
    // 2. 关键字高亮（在消息内插 ANSI 码）
    final String highlighted = highlight(msg);
    // 3. 用模板输出（级别色保留现有 colorFactory）
    final String template = AnsiEncoder.encode(COLOR_TIME, "[%s]", colorFactory.apply(level), "[%-5s]%s",
            COLOR_CLASSNAME, "%-30s: ", COLOR_NONE, "%s%n");
    System.out.format(template, DateUtil.now(), level.name(), " - ",
            ClassUtil.getShortClassName(getName()), highlighted);
}
```

#### 6.3.4 三个扩展点职责

| 扩展点 | 现状 | 改造 |
|--------|------|------|
| **颜色开关** | 无条件输出 | `colorEnabled` + TTY / NO_COLOR / 系统属性判定，非彩色退化为 `super.log()` |
| **级别着色** | `colorFactory` | 保留，`setColorFactory` 可覆盖 |
| **关键字高亮** | 无 | 新增 `HIGHLIGHT_RULES` 规则列表，对消息文本正则着色 |

#### 6.3.5 注意事项（坑）

1. **`appendReplacement` 必须 `Matcher.quoteReplacement(...)`**：ANSI 码 `\033[31m` 里的 `\` 和 `$` 会被 appendReplacement 当转义 / 捕获组引用，不 quote 会报错或输出错乱。
2. **规则顺序**：卡号 `\d{16,19}` 要放在金额 `\d+` 之前，否则长数字先被金额规则截走。
3. **性能和脱敏**：金额 / 卡号高亮本质是「正则扫消息」，高 QPS 下别开；而且高亮 ≠ 脱敏，落盘前仍要单独脱敏。
4. **`System.console()` 的局限**：某些 IDE 控制台（非真实 TTY）会返回 null 导致误关色，所以保留系统属性 `-D...color=true` 强制开启的兜底。

### 6.4 彩色输出到 Linux（tail / 管道的环境约束）

**`tail` 本身不做彩色**，它只是把文件字节原样搬到屏幕。能不能显示彩色，取决于两个条件——**文件里有没有 ANSI 转义码**，以及 **`tail` 的输出是不是终端（TTY）**。

| 场景 | 结果 |
|------|------|
| `tail -f app.log` 直接跑终端，且文件里有 ANSI 码 | ✅ 彩色（终端解释 ANSI） |
| `tail -f app.log \| grep xxx`（管道） | ❌ ANSI 变 `^[[31m` 乱码，还干扰 grep 匹配 |
| 文件里没存 ANSI 码 | 纯文本，但可用工具**动态着色** |

**正确姿势：彩色别写进文件，用查看工具动态着色**。日志文件里混入 ANSI 转义码是「污染」——后续 `grep` / `awk` / Filebeat 采集都会被垃圾字节干扰。

| 工具 | 作用 |
|------|------|
| `less -R` / `less -r` | less 解释文件里的 ANSI 码（读带色文件用 `-R`） |
| `grep --color=auto` | grep 给匹配部分上色（`auto` = 只在 TTY 下才加色） |
| `ccze` | 给 syslog / 通用日志着色 |
| `grc` | 通用日志着色器 |
| `lnav` | 日志导航 + 语法着色（推荐，能解析时间戳 / 级别） |
| `multitail` | 多文件 tail + 着色 |

这也再次印证 §6.3.1：彩色应只留给「人肉盯屏」的 TTY 场景，落盘永远纯文本。

---

## 7. 日志框架全景（除了 Apache / JBoss / SLF4J 还有哪些）

Java 日志框架分两类——**门面**（只定义接口）与**实现**（真正写日志）。本模块已覆盖的门面 + 实现之外的补充如下。

### 7.1 门面（Facade）

| 门面 | 说明 |
|------|------|
| **SLF4J** | 事实标准门面（本模块已适配） |
| **Apache Commons Logging (JCL)** | 老牌门面（已适配） |
| **JBoss Logging** | Red Hat 门面，Hibernate / Quarkus 生态（已适配） |
| **Log4j 2 API** | Log4j2 自带 API，可同时当门面 + 实现 |

### 7.2 实现（Implementation）

| 实现 | 说明 | 状态 |
|------|------|------|
| **Logback** | SLF4J 作者出品，Spring Boot 默认 | 主流，已通过 slf4j 适配 |
| **Log4j 2** | 异步 / 无锁，性能最佳 | 主流，已适配 |
| **Log4j 1.x** | 老牌 | EOL，已适配 |
| **java.util.logging (JUL)** | JDK 自带 | 兜底，已适配 |
| **tinylog** | 轻量单 jar | 已适配（1.x + 2.x） |
| **LogTube** | 结构化 JSON 输出 | 已适配 |
| **Flogger** | Google 出品，流式 API | **未适配**（小众，可自行写 `XxxLog` + `XxxLogFactory` 扩展） |
| **LogKit** | Apache Avalon 老框架 | 基本废弃 |

### 7.3 一句话结论

本模块已覆盖**主流门面 + 实现**（与 hutool-log 1:1 对齐）。若要再扩展，只剩 Flogger 等冷门框架，且需自行实现 `dialect` 适配类——收益有限，不建议盲目加。

---

## 8. 改进优先级建议

| 优先级 | 事项 | 层级 |
|:---:|------|------|
| P0 | logger 分层 + 独立 appender（报文 / 入参出参单独成文件） | 框架配置 + 业务约定 |
| P0 | 结构化日志（JSON）+ MDC 链路 ID | 框架配置 + 业务约定 |
| P1 | 报文脱敏 | 业务侧工具 |
| P1 | 集中式日志搜集（Filebeat → ELK/Loki） | 运维侧 |
| P2 | 控制台彩色输出按关键字 / 区域拓展 | 本模块 `ConsoleColorLog` |
| P2 | 日志源码注释补齐（fqcn 语义等） | 本模块源码 |
