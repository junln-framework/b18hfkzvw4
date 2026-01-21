# Junln Framework


## 简介

Junln Framework 基于“约定优于配置”理念，进一步精简常规配置，提供完整的配置解决方案，帮助开发人员更快速地将常用第三方库或工具集成到 Spring Boot Web 应用程序中。

Junln Framework 基础框架 Fork https://github.com/continew-org/continew-starter 在此基础上修改成贴合企业设计需求

## Fork项目源码

| 开源平台    | 源码地址                                             |
|:--------|:-------------------------------------------------|
| Gitee   | https://gitee.com/continew/continew-starter      |
| GitHub  | https://github.com/continew-org/continew-starter |

## 像数1，2，3一样容易

1.在项目 pom.xml 中锁定版本（**以下两种方式任选其一**）

方式一：如您使用的是 Spring Boot Parent 的方式，则替换 Spring Boot Parent 为 Junln Framework。

```xml
<parent>
    <groupId>cn.junln.framework</groupId>
    <artifactId>junln</artifactId>
    <version>{latest-version}</version>
</parent>
```

方式二：如您使用的是引入 Spring Boot Dependencies 的方式，则替换 Spring Boot Dependencies 为 Junln Framework Dependencies

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- Junln Framework Dependencies -->
        <dependency>
            <groupId>cn.junln.framework</groupId>
            <artifactId>junln-dependencies</artifactId>
            <version>{latest-version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2.在项目 pom.xml 中引入所需模块依赖

```xml
<dependencies>
    <!-- Web 模块 -->
    <dependency>
        <groupId>cn.junln.framework</groupId>
        <artifactId>junln-web</artifactId>
    </dependency>
</dependencies>
```

3.在 application.yml 中根据引入模块，添加所需配置

示例：跨域配置

```yaml
--- ### 跨域配置
junln.web:
  cors:
    enabled: true
    # 配置允许跨域的域名
    allowed-origins: '*'
    # 配置允许跨域的请求方式
    allowed-methods: '*'
    # 配置允许跨域的请求头
    allowed-headers: '*'
    # 配置允许跨域的响应头
    exposed-headers: '*'
```

## 模块结构

```
continew-starter
├─ junln-core（核心模块：包含线程池等自动配置）
├─ junln-json（JSON 模块）
│  └─ junln-json-jackson
├─ junln-api-doc（接口文档模块：Spring Doc + NextDoc4j）
├─ junln-validation（校验模块：Hibernate Validator）
├─ junln-web（Web 开发模块：包含跨域、全局异常+响应、链路追踪等自动配置）
├─ junln-cache（缓存模块）
│  ├─ junln-cache-redisson（Redisson）
│  ├─ junln-cache-jetcache（JetCache 多级缓存）
│  └─ junln-cache-springcache（Spring 缓存）
├─ junln-auth（认证模块）
│  ├─ junln-auth-satoken（国产轻量认证鉴权）
│  └─ junln-auth-justauth（第三方登录）
├─ junln-data（数据访问模块）
│  ├─ junln-data-core（核心模块）
│  ├─ junln-data-mp（MyBatis Plus）
│  └─ junln-data-mf（MyBatis Flex）
├─ junln-encrypt（加密模块）
│  ├─ junln-encrypt-core（核心模块）
│  ├─ junln-encrypt-field（字段加密）
│  └─ junln-encrypt-api（API 加密）
│  └─ junln-encrypt-password-encoder（密码编码器）
├─ junln-security（安全模块）
│  ├─ junln-security-mask（脱敏：JSON 数据脱敏）
│  ├─ junln-security-xss（XSS 过滤）
│  └─ junln-security-sensitivewords（敏感词）
├─ junln-ratelimiter（限流模块）
├─ junln-idempotent（幂等模块）
├─ junln-trace（链路追踪模块）
├─ junln-captcha（验证码模块）
│  ├─ junln-captcha-graphic（静态验证码）
│  └─ junln-captcha-behavior（动态验证码）
├─ junln-messaging（消息模块）
│  ├─ junln-messaging-mail（邮件）
│  └─ junln-messaging-websocket（WebSocket）
├─ junln-log（日志模块）
│  ├─ junln-log-core（核心模块）
│  ├─ junln-log-aop（基于 AOP 实现）
│  └─ junln-log-interceptor（基于拦截器实现（Spring Boot Actuator HttpTrace 增强版））
├─ junln-excel（Excel 文件处理模块）
│  ├─ junln-excel-core（核心模块）
│  ├─ junln-excel-fastexcel（FastExcel）
│  └─ junln-excel-poi（POI）
├─ junln-storage（存储模块）
│  └─ junln-storage-local（本地存储）
├─ junln-license（License 模块）
│  ├─ junln-license-core（核心模块）
│  ├─ junln-license-generator（License 生成器）
│  └─ junln-license-verifier（License 校验器）
└─ junln-extension（扩展模块）
   ├─ junln-extension-datapermission（数据权限模块）
   │  ├─ junln-extension-datapermission-core（核心模块）
   │  └─ junln-extension-datapermission-mp（MyBatis Plus）
   ├─ junln-extension-tenant（租户模块）
   │  ├─ junln-extension-tenant-core（核心模块）
   │  └─ junln-extension-tenant-mp（MyBatis Plus）
   └─ junln-extension-crud（CRUD 模块）
      ├─ junln-extension-crud-core（核心模块）
      ├─ junln-extension-crud-mp（MyBatis Plus）
      └─ junln-extension-crud-mf（MyBatis Flex）
```

### 开发流程步骤

若您希望提交新功能或优化现有代码，请遵循以下步骤：

1. 在开源平台上将项目 fork 到您的个人仓库
2. 将 fork 的项目克隆到本地开发环境
3. 基于当前维护的分支（如 dev）创建新分支（如 feat/newFeature），请勿直接修改源分支（源分支仅做同步 ContiNew 最新代码用）
4. 在新分支上进行代码修改，完成后提交并 push 到您的远程仓库
5. 在开源平台上创建 pull request (PR)，选择正确的源分支和目标分支，按模板填写说明信息（参考 [已合并的 PR](https://github.com/continew-org/continew-starter/pulls?q=is%3Apr+is%3Amerged) 可提高合并率）
6. 提交 PR 后，系统会提示签署 CLA（贡献者协议）。请确保 commit 使用的邮箱与平台绑定邮箱一致（如果不一致，可以在本地通过 `git reset --soft HEAD~1` 回退，然后使用正确邮箱重新提交，最后 `git push -f` 即可，不需要重新创建 PR），然后使用该邮箱签署即可
7. 耐心等待维护者审核并合并您的 PR（建议通过交流群进行快捷沟通）
8. PR 合并后，下次贡献前请先同步最新代码，再重复步骤 3 开始

### 鸣谢

continew-starter
