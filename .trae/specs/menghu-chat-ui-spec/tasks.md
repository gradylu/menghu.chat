# 梦狐社交 App - 实现计划（任务分解与优先级）

> 说明：本计划基于 `spec.md` 将全项目拆分为 **阶段（Phase）→ 任务（Task）** 两个层级。
> 每个 Task 对应一张或多张设计稿。所有 Task 采用 P0/P1/P2 划分，且给出前置依赖。
> 建议按 Phase 顺序交付，每个 Phase 完成后做一次小验收。

---

## Phase 0：工程骨架与通用主题（P0）—— 无设计稿，对应 AC-2 / AC-13

### [ ] Task 0.1：Gradle 工程与依赖配置
- **Priority**：P0
- **Depends On**：无
- **Description**：
  - 建立 `app/` 模块，Kotlin DSL `build.gradle.kts` + `libs.versions.toml`
  - 配置 AGP 8.7 / Gradle 8.11 / Kotlin 2.0 / minSdk 26 / compileSdk 35 / targetSdk 35
  - 启用 Compose，配置 `metricsDestination`、`reportsDestination`
  - 声明依赖：Compose BOM、Material3、Navigation-Compose、Hilt、Hilt-Compose、Room（runtime/compiler/ktx）、Retrofit/OkHttp、DataStore(Preferences)、WorkManager(hilt)、Coil、Timber、LeakCanary(debug)
- **AC 对应**：AC-13 / NFR-3 / NFR-4
- **测试要求**：
  - `programmatic`：`./gradlew assembleDebug` 成功；`./gradlew testDebugUnitTest` 通过（至少有一个空测试以验证管道）
- **Notes**：所有模块包结构严格遵守 AGENTS.md

### [ ] Task 0.2：全局主题、颜色、字体、尺寸、图标资源
- **Priority**：P0
- **Depends On**：Task 0.1
- **Description**：
  - `core/ui/theme`：`Color.kt`、`Type.kt`、`Shape.kt`、`Theme.kt`
  - 主色橙色 `#FF7A3F`、辅色紫色 `#7C4DFF`、白色卡片背景、文本灰阶
  - 抽取所有 `res/values` 资源（strings、dimens、colors）
  - 准备通用矢量图标（菜单、铃铛、相机、附件、点赞、评论等）
- **AC 对应**：AC-2 / NFR-6
- **测试要求**：
  - `human-judgment`：所有页面使用该主题后视觉与设计稿一致
- **Notes**：图标建议使用 `ImageVector` 或 `xml drawable`，避免外部资源

### [ ] Task 0.3：全局 MVVM 基础设施（Base + DI + 网络 Mock + 数据库 + 数据模型映射）
- **Priority**：P0
- **Depends On**：Task 0.1 / Task 0.2
- **Description**：
  - `MenghuChatApp.kt`：`@HiltAndroidApp`，初始化 Timber + Coil
  - `di/`：`AppModule.kt`（提供 OkHttp/MockInterceptor/Retrofit/Room DB/DAO/DataStore/SharedViewModelFactory 等）
  - `core/base/`：`BaseViewModel`（封装 `viewModelScope` + 状态流）、`UiState` 通用 sealed class
  - `core/network/`：`MockInterceptor`（根据 URL 返回本地 JSON/Mock DTO）、Retrofit service 接口骨架
  - `core/storage/`：`MenghuDatabase`（Room）、基础 Entity 设计（User、Conversation、Message、Post、Comment、Notification、Story、Friend）、`UserPreferences` DataStore 封装（token、userId、theme）
  - `core/utils/`：时间格式化工具（"Just now" / "2 mins" / "1 day ago" 等）、`Timber` 封装
  - `core/worker/`：`NotificationSyncWorker` 示例
  - 模型分层：`UiModel`、`Domain`、`Entity`、`Dto` + 扩展函数互转
- **AC 对应**：AC-1 / AC-3 / AC-7 / AC-10 / AC-13
- **测试要求**：
  - `programmatic`：DAO 测试（insert/query）；ViewModel 流测试（StateFlow 状态机）
  - `human-judgment`：代码分层清晰、中文注释完整

---

## Phase 1：账号体系（P0）—— Login.png / Sign up.png

### [ ] Task 1.1：登录/注册数据层（User 模型 + AuthRepository + Mock）
- **Priority**：P0
- **Depends On**：Task 0.3
- **Description**：
  - `feature/auth/` 包：`datasource`（remote + local）、`repository`（AuthRepository）、`model`（LoginRequest/Response、RegisterRequest/Response、User）
  - MockInterceptor 对 `/auth/login`、`/auth/register` 返回固定 DTO（含 mock token）
  - 成功写入 DataStore（token、userId、profile 概要）
- **AC 对应**：AC-1
- **测试要求**：
  - `programmatic`：ViewModel 测试（输入非法表单 → 错误态；合法 → 登录态）

### [ ] Task 1.2：登录页 Compose（LoginScreen + LoginViewModel + 导航）
- **Priority**：P0
- **Depends On**：Task 1.1
- **Description**：
  - 渐变橙色头部、白色圆角卡片
  - 邮箱输入、密码输入（眼睛显示/隐藏）、"Forgot password?"（Toast 占位）
  - 橙色"Log in"按钮；Google/Apple/Facebook 三个按钮（Mock 登录流程）
  - 底部"Don't have an account? Sign up" → 跳转注册
  - 登录状态检查：若已有 token，跳转首页
- **AC 对应**：AC-1
- **测试要求**：
  - `human-judgment`：像素级对齐 Login.png（颜色、间距、圆角）

### [ ] Task 1.3：注册页 Compose（SignUpScreen + SignUpViewModel）
- **Priority**：P0
- **Depends On**：Task 1.1
- **Description**：
  - 渐变橙色头部、白色圆角卡片
  - 姓名、邮箱、密码三项（密码含显隐切换）
  - 橙色"Register"按钮；三方登录占位按钮
  - 底部"Already have an account? Log in" → 跳转登录
- **AC 对应**：AC-1
- **测试要求**：
  - `human-judgment`：对齐 Sign up.png

---

## Phase 2：全局导航与首页骨架（P0）—— 设计稿中所有 TopBar + BottomNav

### [ ] Task 2.1：Navigation 路由表 + 首页容器（HomeScaffold）
- **Priority**：P0
- **Depends On**：Task 0.3 / Task 1.x
- **Description**：
  - `navigation/`：`MenghuNavGraph.kt`、`Destinations.kt`（sealed class 定义所有路由）
  - `HomeScaffold`：通用 TopBar（菜单/橙色标题/通知铃铛）+ 4 Tab BottomNav（Feed/Messages/Friends/Profile）
  - 路由：`login` / `register` / `home`（嵌套 `feed`/`messages`/`friends`/`profile`）/ `story`/`newPost`/`newPostText`/`comments`/`chat`/`nearby`/`nearbyList`/`sendRequest`/`followerProfile`/`notification`/`settings`/`videoCall`
- **AC 对应**：AC-2
- **测试要求**：
  - `programmatic`：导航测试（NavHostController 状态切换）
  - `human-judgment`：BottomNav 选中橙色、图标风格

### [ ] Task 2.2：通用 Compose 组件库（core/ui/component）
- **Priority**：P1
- **Depends On**：Task 0.2
- **Description**：
  - `PrimaryButton`（橙色实心圆角）、`SecondaryButton`（白底灰边）
  - `OutlineTextField`（圆角输入框，含 label、trailing icon）
  - `CircleAvatar`（带/不带描边；可带 "+"）
  - `SearchBar`（圆角搜索框）
  - `Divider`、`SectionHeader`（Today / Yesterday 分组标题）
- **AC 对应**：全局 NFR-6
- **测试要求**：
  - `human-judgment`：各组件在不同页面被复用后视觉一致

---

## Phase 3：动态流与故事（P1）—— Feeds.png / Story.png

### [ ] Task 3.1：动态（Post）数据层 + Mock 初始化数据
- **Priority**：P1
- **Depends On**：Task 0.3
- **Description**：
  - `feature/moment/`：`datasource`、`repository`（PostRepository）、`model`（Post / PostImage / Story）
  - Room：`PostEntity`、`StoryEntity`、`PostDao`、`StoryDao`
  - Mock 初始化：应用首次启动时写入 ≥ 5 条动态样例（包含多种图片组合：1大+2小、2横排等）、≥ 5 条故事
- **AC 对应**：AC-3 / AC-4
- **测试要求**：
  - `programmatic`：DAO insert + query 测试

### [ ] Task 3.2：Feeds 动态列表页 Compose（FeedScreen + FeedViewModel）
- **Priority**：P1
- **Depends On**：Task 3.1 / Task 2.1 / Task 2.2
- **Description**：
  - TopBar：菜单 + "Feeds" 橙标题 + 通知铃铛
  - 横向滚动故事行（LazyRow，首项为"+"头像）
  - `LazyColumn` 动态卡片：头像、昵称、时间、`...`、正文、多图组合、评论图标+计数、点赞图标+计数
  - 点赞动效（红色拇指）、点击评论图标进入评论页
  - 点击故事头像进入 Story 页
- **AC 对应**：AC-3
- **测试要求**：
  - `human-judgment`：对齐 Feeds.png 卡片布局与配色
  - `programmatic`：LazyColumn 使用 `items(posts, key={it.id})`

### [ ] Task 3.3：故事全屏页 Compose（StoryScreen + StoryViewModel）
- **Priority**：P1
- **Depends On**：Task 3.1 / Task 2.1
- **Description**：
  - 全屏大图背景（Coil 加载；全屏沉浸式，WindowInsets 适配）
  - 顶部：用户头像 + 昵称 + 时间（1h）
  - 底部：附件图标、输入框"be the first to comment"、表情图标、发送图标
  - 发送后本地 toast/Mock 反馈；点击空白区/返回关闭
- **AC 对应**：AC-4
- **测试要求**：
  - `human-judgment`：全屏观感与 Story.png 一致

---

## Phase 4：发布动态（P1）—— New Post.png / New Post 2.png

### [ ] Task 4.1：New Post 图片选择/滤镜页 Compose
- **Priority**：P1
- **Depends On**：Task 2.1 / Task 2.2
- **Description**：
  - 顶部 Tab：Post（橙色）/ Story（灰）/ Live（灰）
  - 中间大图预览；上方 `\` 关闭按钮
  - 横向滤镜缩略图行（Normal + 多滤镜），Filter/Edit 文字 Tab
  - 下方 Gallery 网格；右侧浮动铅笔、相机按钮（Mock 反馈）
- **AC 对应**：AC-5
- **测试要求**：
  - `human-judgment`：对齐 New Post.png

### [ ] Task 4.2：New Post 文字描述页 Compose + 发布逻辑
- **Priority**：P1
- **Depends On**：Task 4.1 / Task 3.1
- **Description**：
  - 顶部图片预览 + "+" 按钮
  - 多行输入 "What you want to say?"
  - 选项行：Tag people / Add location / Add music / Feeling or activity（点击 toast）
  - Share to：Facebook / Instagram / twitter（三个开关）
  - 橙色"Post Now" → 写入 Room 新动态（置顶 Just now）→ 返回 Feeds
- **AC 对应**：AC-5
- **测试要求**：
  - `human-judgment`：对齐 New Post 2.png
  - `programmatic`：发布后 Feed 列表新增 1 条（DAO 测试可验证）

---

## Phase 5：评论（P1）—— coments.png

### [ ] Task 5.1：评论数据层 + Compose 页
- **Priority**：P1
- **Depends On**：Task 3.1 / Task 2.1
- **Description**：
  - `CommentEntity` + `CommentDao`；发布动态时写入若干示例评论
  - 页面：顶部标题（头像 + "Comments 24k from xxx post"）+ 时间 + 关闭
  - 评论列表：头像、昵称、时间、内容、评论图标+子计数、点赞图标+子计数（红色拇指）
  - 底部：附件、输入框、紫色发送按钮
- **AC 对应**：AC-6
- **测试要求**：
  - `human-judgment`：对齐 coments.png
  - `programmatic`：发送后列表新增 1 条评论

---

## Phase 6：即时消息（P0）—— Messages.png / Chat.png / Attachement file.png

### [ ] Task 6.1：消息数据层（Conversation + Message + Repository）
- **Priority**：P0
- **Depends On**：Task 0.3
- **Description**：
  - `feature/chat/`：ConversationEntity、MessageEntity、ConversationDao、MessageDao、ChatRepository
  - Mock 初始化：≥ 6 个会话，每会话若干消息
- **AC 对应**：AC-7
- **测试要求**：
  - `programmatic`：DAO 会话列表/消息列表测试

### [ ] Task 6.2：会话列表 Compose（MessagesScreen）
- **Priority**：P0
- **Depends On**：Task 6.1 / Task 2.1
- **Description**：
  - TopBar + 搜索框
  - LazyColumn 会话项：圆形头像（紫色外圈描边）、昵称、最新消息、相对时间（Just now / 2 mins / 1 hours）
  - 点击进入 Chat
- **AC 对应**：AC-7
- **测试要求**：
  - `human-judgment`：对齐 Messages.png

### [ ] Task 6.3：单聊页 Compose（ChatScreen）
- **Priority**：P0
- **Depends On**：Task 6.2
- **Description**：
  - 顶部：返回箭头、头像、昵称、"Online" 绿点、电话、视频（进入 Video call）
  - 气泡：右侧橙色（自己）/ 左侧白色（对方）+ 时间
  - "正在输入..." 三点气泡（短暂动画）
  - 底部：附件图标（弹出 Attachement file 弹窗）、输入框、紫色发送
- **AC 对应**：AC-7
- **测试要求**：
  - `human-judgment`：对齐 Chat.png
  - `programmatic`：发送消息写入 Room 并出现在列表

### [ ] Task 6.4：附件选择弹窗 Compose（AttachmentBottomSheet）
- **Priority**：P1
- **Depends On**：Task 6.3
- **Description**：
  - "Select Option" 半屏底部弹窗，2 行 3 列九宫格：Camera/Galery/File + Location/Audio/Contact
  - 右上角关闭；点击任意图标 toast 反馈（Mock）
- **AC 对应**：AC-7
- **测试要求**：
  - `human-judgment`：对齐 Attachement file.png

---

## Phase 7：好友与附近的人（P1）—— Friends.png / nearby.png / nearby - friends.png / send request.png / Follower Profile.png

### [ ] Task 7.1：好友数据层（Friend + NearbyUser + Repository）
- **Priority**：P1
- **Depends On**：Task 0.3
- **Description**：
  - FriendEntity / NearbyUserEntity / DAO / Repository；Mock 初始化好友 10+ 条、附近用户 10+ 条
- **AC 对应**：AC-8

### [ ] Task 7.2：好友列表 FriendsScreen（两列网格 + 字母索引）
- **Priority**：P1
- **Depends On**：Task 7.1 / Task 2.1
- **Description**：
  - TopBar + 搜索框 + 右侧 A–K 字母索引条（滚动定位 Mock）
  - `LazyVerticalGrid(2)`：头像大图卡片 + 姓名 + 国家
- **AC 对应**：AC-8
- **测试要求**：
  - `human-judgment`：对齐 Friends.png

### [ ] Task 7.3：附近的人雷达 NearbyRadarScreen
- **Priority**：P2
- **Depends On**：Task 7.1 / Task 2.1
- **Description**：
  - 渐变橙色背景；中央蓝色箭头；周围用户头像环形布局（Canvas 或自定义 Layout）
  - 底部"Searching people nearby..."
  - 点击头像或空白 → 跳 nearby - friends 列表
- **AC 对应**：AC-8
- **测试要求**：
  - `human-judgment`：对齐 nearby.png

### [ ] Task 7.4：附近用户列表 NearbyListScreen + 发送好友请求 SendRequestScreen
- **Priority**：P1
- **Depends On**：Task 7.3
- **Description**：
  - NearbyList：两列网格，头像 + 姓名 + 距离（2km / 10km ...）
  - SendRequest：全屏大图背景 + 姓名 + 距离 + 橙色"Send friend request"按钮
- **AC 对应**：AC-8
- **测试要求**：
  - `human-judgment`：对齐 nearby - friends.png / send request.png

### [ ] Task 7.5：关注者个人主页 FollowerProfileScreen
- **Priority**：P1
- **Depends On**：Task 3.2（复用动态卡片组件）
- **Description**：
  - 头像、昵称、职业（Photographer @Cannon）、签名、统计行（Post / Followers / Friends）
  - 橙色 Follow + 次要 Chat 按钮
  - "Posts / Collection" Tab；下方动态列表（复用 Feeds 卡片组件）
- **AC 对应**：AC-8 / AC-9
- **测试要求**：
  - `human-judgment`：对齐 Follower Profile.png

---

## Phase 8：个人主页（P1）—— Profile Posts.png / Profile Collection.png

### [ ] Task 8.1：ProfileScreen（Posts Tab）
- **Priority**：P1
- **Depends On**：Task 2.1 / Task 3.2
- **Description**：
  - 顶部：头像、昵称、职业（Designer @HelloUI）、签名（Independent woman woman 😊）
  - 统计：Post 1,312 / Followers 2,1m / Friends 2,523
  - "Posts / Collection" Tab（橙色下划线选中）
  - 动态列表（复用卡片组件）
- **AC 对应**：AC-9
- **测试要求**：
  - `human-judgment`：对齐 Profile Posts.png

### [ ] Task 8.2：Profile Collection Tab 内容
- **Priority**：P2
- **Depends On**：Task 8.1
- **Description**：
  - 切换到 Collection 后显示收藏内容（Mock 列表/网格）
  - 样式与主 Tab 一致，仅数据差异
- **AC 对应**：AC-9
- **测试要求**：
  - `human-judgment`：对齐 Profile Collection.png

---

## Phase 9：通知中心（P1）—— Notification.png

### [ ] Task 9.1：通知数据层 + 页面
- **Priority**：P1
- **Depends On**：Task 0.3
- **Description**：
  - NotificationEntity + NotificationDao + Repository + WorkManager 轮询生成 Mock 未读
  - 页面：Today / Yesterday / This week 分组 SectionHeader
  - 每条：头像、昵称、类型文案（respond/like/follow）、相对时间、未读红点
  - 支持点击清除或右侧 × 清除
- **AC 对应**：AC-10
- **测试要求**：
  - `human-judgment`：对齐 Notification.png
  - `programmatic`：DAO 增删 + WorkManager Mock 测试

---

## Phase 10：设置（P1）—— Settings.png

### [ ] Task 10.1：设置页面 SettingsScreen + 子页占位 + 登出
- **Priority**：P1
- **Depends On**：Task 2.1 / Task 0.3
- **Description**：
  - 顶部当前用户信息行
  - 选项列表（Account / Chat / New Group / Security / Notification / Help + Log out）
  - 每项点击进入简单占位页；Log out 清除 DataStore 并返回 Login
- **AC 对应**：AC-11
- **测试要求**：
  - `human-judgment`：对齐 Settings.png
  - `programmatic`：登出后 DataStore token 为空

---

## Phase 11：视频通话（P2）—— Video call.png

### [ ] Task 11.1：视频通话页 VideoCallScreen（Mock）
- **Priority**：P2
- **Depends On**：Task 6.3（由聊天页点击视频进入）
- **Description**：
  - 全屏双视频窗：上方对方视频、下方本地视频（右下角 PIP 小窗）
  - 左下紫色圆形静音图标 + 时长（Mock 计时）
  - 底部操作栏：麦克风、扬声器、摄像头、聊天、红色挂断 → 返回聊天
- **AC 对应**：AC-12
- **测试要求**：
  - `human-judgment`：对齐 Video call.png

---

## Phase 12：测试与构建收尾（P0）

### [ ] Task 12.1：核心 ViewModel/DAO 单元测试 + 构建验证
- **Priority**：P0
- **Depends On**：所有 Task 完成后
- **Description**：
  - 为关键 ViewModel（Login、Feed、Chat、Notification、Settings）补充 StateFlow 测试
  - 为 Room DAO 补充 insert/query/delete 基本测试
  - 确认 `./gradlew assembleDebug` / `testDebugUnitTest` 通过
- **AC 对应**：AC-13
- **测试要求**：
  - `programmatic`：命令构建成功；测试全绿
- **Notes**：LeakCanary 在 debug 构建自动启用；忽略 release 相关
