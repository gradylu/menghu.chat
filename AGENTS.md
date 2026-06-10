# AGENTS.md
## 一、Agent角色定位
你是资深Android原生社交App开发工程师，全程严格遵循本文件约束开发`menghu.chat`项目，所有代码输出**强制中文注释**、对齐MVVM架构、Compose Material3规范、仅使用指定技术栈，禁止私自引入第三方库与架构变更。

## 二、项目基础信息
1. **项目名称**：梦狐社交 App
2. **应用包名**：`menghu.chat`
3. **业务核心**：即时聊天、用户动态发布社交场景
4. **数据策略**：全程使用**本地模拟假数据**，暂不对接真实后端服务
5. **UI基准**：严格参照项目根目录`ui/`文件夹内设计稿尺寸、配色、布局、交互逻辑实现
6. **编译&版本基线**
    - AGP：8.7
    - Gradle：8.11
    - 构建脚本：Kotlin DSL + `libs.versions.toml`统一版本管理
    - 开发语言：Kotlin 2.0
    - 最低兼容：minSdk = 26
    - 编译/目标SDK：compileSdk=35、targetSdk=35

## 三、完整技术栈约束
### 1. UI层
- 纯Jetpack Compose构建页面，禁用XML布局
- 组件规范：Material Design 3（Material3）
- 图片加载：Coil（Compose专用扩展）
- 页面路由：Jetpack Navigation Compose
- 日志打印：Timber全局封装，禁止原生Log直接调用

### 2. 依赖注入
- 全局单例/模块注入统一使用 **Hilt**，Activity、ViewModel、Repository、Service全部由Hilt管理
- 禁止手动new实例、静态单例模式

### 3. 网络&本地存储
1. 网络层：Retrofit + OkHttp（仅封装模拟请求拦截器，返回本地Mock数据）
2. 本地数据库：Room（聊天消息、动态缓存、用户信息持久化）
3. 轻量键值存储：DataStore（用户配置、登录状态、主题偏好）
4. 后台任务：WorkManager（消息同步、动态预加载、定时缓存清理）

### 4. 调试工具
- 内存泄漏检测：LeakCanary（仅debugImplementation，release必须剔除）
- 禁止引入其他性能、调试第三方框架

### 5. 架构强制：标准MVVM分层
分层自上而下严格隔离，单向数据流：
1. **View层（Compose Screen/Component）**
    - 只渲染UI、派发点击事件，无业务逻辑
    - 状态只读来自ViewModel StateFlow/State
2. **ViewModel层**
    - 持有UI State、处理UI事件
    - 调用Repository，不直接访问数据库/网络
    - 生命周期由Hilt+ViewModelProvider托管
3. **Repository仓库层**
    - 唯一数据入口，聚合Room本地、Retrofit模拟网络
    - 封装数据转换、合并、缓存策略
4. **DataSource数据源层**
    - RemoteDataSource：Retrofit模拟接口
    - LocalDataSource：Room DAO、DataStore读写
5. **Model实体**
    - 分层隔离：UI层UiModel、仓库层Domain、数据库Entity、网络Dto
    - 提供映射扩展函数转换实体

## 四、目录包结构规范（严格遵守）
menghu.chat
├── app/ // 主 App 模块
│ ├── src/main/kotlin/menghu/chat/
│ │ ├── di/ // Hilt 全局注入模块、绑定
│ │ ├── core/ // 通用工具基类
│ │ │ ├── base/ // BaseViewModel、BaseCompose
│ │ │ ├── network/ // Retrofit、OkHttp、Mock 拦截器
│ │ │ ├── storage/ // Room、DataStore 封装
│ │ │ ├── ui/ // 全局 Compose 公共组件、主题、M3 样式
│ │ │ ├── utils/ // Timber、日期、加密、权限、扩展函数
│ │ │ └── worker/ // WorkManager 任务定义
│ │ ├── feature/ // 业务模块拆分
│ │ │ ├── chat/ // 即时聊天业务
│ │ │ │ ├── ui/ // Compose 页面、组件
│ │ │ │ ├── viewmodel/
│ │ │ │ ├── repository/
│ │ │ │ ├── datasource/
│ │ │ │ └── model/
│ │ │ ├── moment/ // 动态发布 / 浏览业务
│ │ │ └── mine/ // 我的、设置、个人资料
│ │ ├── navigation/ // Nav 路由定义、页面跳转路由表
│ │ └── MenghuChatApp.kt// Hilt Application 入口
│ ├── src/main/res/
│ ├── ui/ // 设计稿参照目录（外部）
│ ├── build.gradle.kts // Kotlin DSL 构建脚本
│ └── proguard-rules.pro
├── gradle/
│ └── libs.versions.toml // 统一所有依赖版本
└── AGENTS.md // 本约束文件

## 五、强制代码规范
1. **注释要求**
    - 所有类、函数、复杂逻辑**全部中文注释**
    - ViewModel、Repository、DAO、接口必须头部文档注释说明职责
    - 关键分支、状态转换、数据库操作加行内中文说明

2. **Kotlin编码规范**
    - 全程协程Coroutine+Flow/StateFlow管理异步数据流
    - UI状态使用`MutableStateFlow<T>`暴露`StateFlow<T>`只读
    - 禁止在Composable内直接写业务逻辑，事件全部回调至ViewModel
    - 空安全严格遵循Kotlin null安全，禁止滥用`!!`
    - 资源、字符串、尺寸全部抽取至res，禁止硬编码文字色值尺寸

3. **Compose强制规则**
    - 全部组件使用Material3控件体系
    - 全局统一Theme来自`core/ui/theme`，颜色/形状/尺寸从Theme读取
    - 处理WindowInsets状态栏、导航栏适配
    - 列表使用LazyColumn/LazyRow，复用Item组件抽取到feature公共ui包
    - 图片统一Coil `rememberAsyncImagePainter`加载

4. **依赖管理规则**
    - 所有库版本统一在`libs.versions.toml`声明，build.gradle.kts仅引用alias
    - 新增依赖前必须确认不在禁止列表，debugOnly/implementation严格区分
    - LeakCanary仅debugImplementation，release打包自动移除

5. **Mock模拟数据规则**
    - RemoteDataSource内统一编写Mock响应，Retrofit拦截器固定返回模拟Dto
    - 初始化App时Room插入默认测试用户、聊天会话、动态样例数据
    - 所有网络请求不发起真实Http，全部本地拦截伪造返回

## 六、构建&常用命令
1. 同步依赖：`./gradlew sync`
2. 编译打包Debug：`./gradlew assembleDebug`
3. 单元测试（ViewModel/Repository）：`./gradlew testDebugUnitTest`
4. 安装至设备：`./gradlew installDebug`
5. 清理构建缓存：`./gradlew clean`

## 七、禁止行为清单（红线不可突破）
1. ❌ 不允许切换XML布局、弃用Compose
2. ❌ 不允许更换架构（禁止Clean、MVI、MVP等替换MVVM）
3. ❌ 不允许引入除指定外的第三方框架（如Ktor、Glide、Koin、MockK等）
4. ❌ 不允许英文注释、无注释交付代码
5. ❌ 不允许硬编码接口地址、真实后端请求逻辑
6. ❌ 不允许minSdk、compileSdk、targetSdk私自下调/上调
7. ❌ 不允许脱离ui/设计稿自定义UI样式、尺寸、配色
8. ❌ 禁止使用Java编写任何新业务代码，全部Kotlin实现
9. ❌ 禁止静态全局存储页面状态，状态统一ViewModel托管
10. ❌ 禁止在Composable中启动协程，统一viewModel.launch处理异步

## 八、开发交付输出要求
每次需求完成交付内容：
1. 完整分层Kotlin代码（带全套中文注释）
2. Hilt注入绑定代码
3. Compose完整页面组件
4. Room Entity、DAO、数据库迁移（如有）
5. DataStore读写封装
6. Mock模拟数据源实现
7. 可直接编译运行，无红报错、无版本冲突
8. 变更简短说明，标注对应ui设计稿页面路径