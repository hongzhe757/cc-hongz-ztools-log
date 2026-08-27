# cc-hongz-ztools-log

Hongz 平台 **日志门面库**（`jar`），对应 Hutool **`hutool-log`**，为各业务模块提供统一的日志抽象，屏蔽底层日志框架差异。

## 特性

- **门面**：`Log` / `AbstractLog` / `StaticLog` / `LogFactory`
- **9 个方言适配**（`dialect/`）：`console`、`jdk`、`log4j`、`log4j2`、`slf4j`、`commons`、`jboss`、`tinylog`、`logtube`
- **自动探测**（SPI）：按优先级 logtube → slf4j → log4j2 → log4j → commons → tinylog → tinylog2 → jboss，兜底 console / jdk
- **对齐 hutool-log**：46 个主类 + 4 个测试类 1:1 对齐，checkstyle 0 违规，34 个单测用例全绿

## 模块关系

| Hutool | Hongz |
|---|---|
| `hutool-log` | **`cc-hongz-ztools-log`**（本模块） |

## 构建

```bash
mvn -f cc-hongz-ztools-log/pom.xml clean test
```

## 文档

详见 [docs/log-analysis.md](./docs/log-analysis.md)（日志能力分析与改进建议）。
