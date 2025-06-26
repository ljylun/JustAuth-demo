# JustAuth Demo

JustAuth Demo 是一个第三方授权登录的示例项目，演示了如何使用 [JustAuth](https://github.com/justauth/JustAuth) 快速集成第三方平台的 OAuth 登录功能。该项目提供了完整的登录流程实现，包括授权、回调、获取用户信息、刷新令牌和撤销授权等功能。

## 特性

- 支持 40+ 个第三方平台的授权登录
- 统一的接口设计，使用简单
- 支持 Redis 缓存管理授权状态
- 完整的异常处理机制
- 可扩展的平台支持
- 美观的 Bootstrap 4 用户界面
- 完整的用户管理功能

## 支持的平台

目前已集成的第三方平台包括：

- Gitee
- 百度
- GitHub
- 微博
- 钉钉
- 支付宝
- QQ
- 微信（开放平台、企业微信、公众平台）
- Google
- Facebook
- 抖音
- 领英
- Microsoft
- 小米
- 今日头条
- 飞书
- 等 40+ 个平台

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.0+
- Redis

### 配置说明

1. 克隆项目：
```bash
git clone https://github.com/justauth/JustAuth-demo.git
```

2. Redis 配置：
在 `src/main/resources/application.properties` 中配置 Redis 连接信息：
```properties
# Redis 配置
spring.redis.database=0
spring.redis.host=localhost
spring.redis.port=6379
# spring.redis.password= # 如果有密码，取消注释并设置
```

3. 平台配置：
在 `RestAuthController.java` 中配置各平台的 `clientId` 和 `clientSecret`：
```java
AuthConfig config = AuthConfig.builder()
    .clientId("您的 clientId")
    .clientSecret("您的 clientSecret")
    .redirectUri("http://localhost:8443/oauth/callback/平台名称")
    .build();
```

4. 运行项目：
```bash
mvn spring-boot:run
```

5. 访问：
   - 首页：http://localhost:8443/
   - 用户列表：http://localhost:8443/users

### 自定义认证源

项目提供了自定义 GitLab 认证源的示例（`AuthMyGitlabRequest.java`），展示了如何扩展 JustAuth 以支持新的认证平台：

1. 继承 `AuthDefaultRequest` 类
2. 实现必要的方法：
```java
// 获取访问令牌
protected AuthToken getAccessToken(AuthCallback authCallback);

// 获取用户信息
protected AuthUser getUserInfo(AuthToken authToken);

// 获取授权链接
protected String authorize(String state);
```

3. 在 `RestAuthController` 中添加新的认证源：
```java
AuthRequest authRequest = new AuthMyGitlabRequest(AuthConfig.builder()
    .clientId("您的 clientId")
    .clientSecret("您的 clientSecret")
    .redirectUri("回调地址")
    .build());
```

### 部署说明

1. 打包：
```bash
mvn clean package
```

2. 运行：
```bash
java -jar target/justauth-demo.jar
```

3. 配置说明：
   - 确保 Redis 服务已启动
   - 确保配置的回调地址可以被外网访问
   - 建议使用 HTTPS 以保证安全性
   - 生产环境建议配置 Redis 密码

4. 注意事项：
   - 生产环境需要修改 `application.properties` 中的配置
   - 建议使用 Nginx 做反向代理
   - 建议配置 SSL 证书
   - 建议使用专门的 Redis 实例

### 使用流程

1. 在首页选择要登录的平台
2. 跳转到对应平台的授权页面
3. 用户授权后自动跳回系统
4. 在用户列表页可以看到登录的用户信息
5. 可以查看用户的详细信息、刷新令牌或撤销授权

### 界面预览

项目提供了两个主要页面：

1. **首页**：展示所有支持的第三方登录平台，用户可以点击任意平台图标进行登录
2. **用户页面**：展示所有已登录的用户信息，包括头像、用户名、来源平台等，并提供查看详情、刷新令牌和撤销授权的功能

### 数据存储

项目使用 Redis 存储两类数据：

1. **授权状态**：通过 `AuthStateRedisCache` 类实现，用于存储授权过程中的状态信息
2. **用户信息**：通过 `UserServiceImpl` 类实现，使用 Redis Hash 结构存储用户数据，key 为 "JUSTAUTH::USERS"

## 项目结构

```
src/main/java/me/zhyd/justauth/
├── cache                  # 缓存相关
│   └── AuthStateRedisCache.java  # Redis 缓存实现
├── config                 # 配置类
├── controller             # 控制器
│   ├── RestAuthController.java   # 认证主控制器
│   └── IndexController.java      # 页面控制器
├── custom                 # 自定义认证源
│   └── AuthMyGitlabRequest.java  # 自定义 GitLab 认证
├── model                  # 数据模型
│   └── AuthUser.java      # 用户模型
├── service                # 服务层
│   ├── UserService.java   # 用户服务接口
│   └── UserServiceImpl.java      # 用户服务实现
├── JustAuthPlatformInfo.java     # 平台信息定义
└── JustauthDemoApplication.java  # 应用程序入口
```

## 核心类说明

- `RestAuthController`: 处理授权请求和回调的主控制器，包含所有平台的配置和认证逻辑
- `JustAuthPlatformInfo`: 定义支持的平台信息，包括名称、图标、是否支持等
- `AuthStateRedisCache`: Redis 缓存配置，实现 `AuthStateCache` 接口，用于存储授权状态
- `UserServiceImpl`: 用户信息管理服务，使用 Redis 存储用户数据
- `AuthMyGitlabRequest`: 自定义 GitLab 认证源的示例，展示如何扩展 JustAuth

## 常见问题

### 1. 回调地址配置问题

**问题**: 授权后无法正确回调到系统
**解决方案**: 
- 确保回调地址与平台开发者中心配置的一致
- 确保回调地址可以被外网访问（可使用内网穿透工具如 ngrok）
- 检查回调地址格式是否正确（应为 `http://域名/oauth/callback/平台名称`）

### 2. Redis 连接问题

**问题**: 无法连接到 Redis
**解决方案**:
- 确保 Redis 服务已启动
- 检查 Redis 连接配置是否正确
- 如果设置了密码，确保密码配置正确

### 3. 授权失败问题

**问题**: 第三方平台授权失败
**解决方案**:
- 检查 `clientId` 和 `clientSecret` 是否正确
- 确认应用是否已通过平台审核
- 查看日志获取详细错误信息

## 高级用法

### 1. 自定义缓存实现

除了示例中的 Redis 缓存实现，你还可以自定义其他缓存实现：

```java
public class CustomCache implements AuthStateCache {
    // 实现接口方法
}
```

然后在创建 AuthRequest 时传入：

```java
AuthRequest authRequest = new AuthGiteeRequest(AuthConfig.builder()
    // 其他配置
    .build(), new CustomCache());
```

### 2. 自定义 HTTP 客户端

JustAuth 默认使用 HttpURLConnection 作为 HTTP 客户端，你可以自定义其他实现：

```java
AuthConfig config = AuthConfig.builder()
    // 其他配置
    .httpConfig(HttpConfig.builder()
        .timeout(15000)  // 超时时间
        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10080)))  // 代理配置
        .build())
    .build();
```

### 3. 事件监听

你可以通过实现 `AuthEventListener` 接口来监听授权事件：

```java
AuthEventListener listener = new AuthEventListener() {
    @Override
    public void onPreAuth(AuthRequest authRequest) {
        // 授权前的处理
    }
    
    @Override
    public void onPostAuth(AuthRequest authRequest, AuthResponse<?> response) {
        // 授权后的处理
    }
};
```

## 扩展支持

如果需要支持新的平台，可以：

1. 实现自定义的 `AuthRequest`
2. 在 `RestAuthController` 中添加新平台的配置
3. 在 `JustAuthPlatformInfo` 中添加平台信息

## 注意事项

1. 需要在各平台的开发者中心申请应用并获取 `clientId` 和 `clientSecret`
2. 回调地址需要配置为可访问的地址，如果是本地开发可以使用内网穿透工具
3. 部分国外平台（如 Google、Facebook）需要配置代理才能正常访问
4. 生产环境中应该使用 HTTPS 保证数据传输安全
5. 敏感信息（如 clientSecret）应该使用配置文件或环境变量管理，不要硬编码
6. 定期检查第三方平台的 API 变更，及时更新配置

## 相关资源

- [JustAuth 官方文档](https://justauth.wiki)
- [各平台授权申请地址](https://justauth.wiki/guide/oauth)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [OAuth 2.0 协议](https://oauth.net/2/)
- [Redis 官方文档](https://redis.io/documentation)
- [JustAuth 源码](https://github.com/justauth/JustAuth)
- [Bootstrap 4 文档](https://getbootstrap.com/docs/4.6/getting-started/introduction/)

## 贡献指南

欢迎提交 Issue 或 Pull Request 来帮助改进这个示例项目。

1. Fork 项目
2. 创建你的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交你的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建一个 Pull Request

## 许可证

[MIT License](LICENSE)
