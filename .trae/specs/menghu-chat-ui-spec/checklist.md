# 梦狐社交 App - 验证清单（Checklist）

> 交付前须逐项打勾。每条对应 `spec.md` 的 AC 或 NFR，与 `tasks.md` 的 Task 编号联动。

---

## 工程骨架 & 基础设施（Phase 0）
- [ ] **C-01**：`app/build.gradle.kts` 使用 Kotlin DSL，所有依赖版本通过 `libs.versions.toml` 管理
- [ ] **C-02**：AGP 8.7、Gradle 8.11、Kotlin 2.0、minSdk 26、compileSdk 35、targetSdk 35
- [ ] **C-03**：Compose 开启 `metricsDestination` 与 `reportsDestination`
- [ ] **C-04**：`MenghuChatApp.kt` 为 `@HiltAndroidApp`，初始化 Timber、Coil
- [ ] **C-05**：`di/AppModule.kt` 通过 `@Provides` 提供 Room DAO、OkHttp/Retrofit（含 MockInterceptor）、DataStore、WorkManager 工厂
- [ ] **C-06**：`core/ui/theme` 包含 Color / Type / Shape / Theme，主色橙色、辅色紫色、统一圆角
- [ ] **C-07**：Room 数据库定义：至少 User、Conversation、Message、Post、Comment、Notification、Story、Friend 实体与 DAO
- [ ] **C-08**：DataStore 封装 UserPreferences（token、userId、theme）
- [ ] **C-09**：`core/utils` 提供时间格式化（Just now / 2 mins / 1 day ago）
- [ ] **C-10**：模型分层：UiModel / Domain / Entity / Dto + 映射扩展函数
- [ ] **C-11**：所有类、函数含中文注释（代码评审 100% 覆盖）

## 登录 & 注册（Phase 1 / AC-1）
- [ ] **C-12**：LoginScreen 视觉对齐 Login.png（渐变头部、白色圆角卡片、输入框、显隐密码、橙色登录按钮、三方按钮、底部链接）
- [ ] **C-13**：SignUpScreen 视觉对齐 Sign up.png（姓名/邮箱/密码、Register 按钮、三方登录、Log in 链接）
- [ ] **C-14**：表单验证：邮箱非空且格式合法、密码长度 ≥ 6；不通过时显示错误提示，不调用接口
- [ ] **C-15**：登录/注册成功后写入 DataStore 并路由到首页
- [ ] **C-16**：ViewModel 单元测试覆盖输入验证与成功/失败状态流

## 全局导航与组件（Phase 2 / AC-2）
- [ ] **C-17**：`navigation/Destinations.kt` 与 `MenghuNavGraph.kt` 定义了所有路由（login/register/home 嵌套 feed/messages/friends/profile 等）
- [ ] **C-18**：HomeScaffold 提供通用 TopBar（菜单/橙色居中标题/通知铃铛）+ 4 Tab BottomNav（Feed/Messages/Friends/Profile，选中态橙色）
- [ ] **C-19**：`core/ui/component` 抽取 PrimaryButton、SecondaryButton、CircleAvatar、OutlineTextField、SearchBar、SectionHeader 等公共组件

## 动态流 & 故事（Phase 3 / AC-3 / AC-4）
- [ ] **C-20**：FeedScreen 布局对齐 Feeds.png（顶部故事横排、动态卡片、评论/点赞计数）
- [ ] **C-21**：动态卡片支持多种图片组合：1 大图 + 右侧 2 小图；2 张横排等
- [ ] **C-22**：`LazyColumn` 使用 `items(posts, key={it.id})` 稳定 key
- [ ] **C-23**：点击故事头像进入 Story 全屏页，视觉对齐 Story.png（顶部用户信息、底部评论栏）
- [ ] **C-24**：点击动态评论图标/数量可进入评论页

## 发布（Phase 4 / AC-5）
- [ ] **C-25**：New Post 页 1 对齐 New Post.png（Post/Story/Live Tab、大图预览、滤镜缩略图行、Gallery 网格、浮动编辑/相机按钮）
- [ ] **C-26**：New Post 页 2 对齐 New Post 2.png（图片预览、"What you want to say?"、选项行、Share to 开关、橙色 Post Now）
- [ ] **C-27**：Post Now 后 Room 写入新动态且 Feed 列表首项显示"Just now"

## 评论（Phase 5 / AC-6）
- [ ] **C-28**：评论页对齐 coments.png（顶部标题、评论项、底部输入栏+紫色发送）
- [ ] **C-29**：评论项显示头像/昵称/时间/内容/评论图标+子计数/点赞图标+子计数（点赞红色拇指）
- [ ] **C-30**：发送评论后列表即时更新

## 即时消息（Phase 6 / AC-7）
- [ ] **C-31**：Messages 会话列表对齐 Messages.png（紫色外圈头像、昵称、预览、相对时间）
- [ ] **C-32**：Chat 单聊页对齐 Chat.png（顶部 Online 绿点、电话/视频、右侧橙色气泡/左侧白色气泡、"正在输入..."三点气泡、底部输入+紫色发送+附件图标）
- [ ] **C-33**：附件弹窗对齐 Attachement file.png（Select Option 九宫格）
- [ ] **C-34**：点击视频图标可进入 Video call 页

## 好友与附近的人（Phase 7 / AC-8）
- [ ] **C-35**：Friends 两列网格对齐 Friends.png（头像卡片、姓名、国家、右侧 A–K 字母索引）
- [ ] **C-36**：Nearby radar 对齐 nearby.png（渐变橙背景、中央箭头、头像环、"Searching people nearby..."）
- [ ] **C-37**：Nearby list 对齐 nearby - friends.png（两列网格、姓名、距离）
- [ ] **C-38**：Send request 页对齐 send request.png（大图、姓名、距离、橙色"Send friend request"）
- [ ] **C-39**：Follower Profile 对齐 Follower Profile.png（头像+职业+签名+统计+Follow/Chat+Posts/Collection Tab+动态列表）

## 个人主页（Phase 8 / AC-9）
- [ ] **C-40**：Profile Posts Tab 对齐 Profile Posts.png
- [ ] **C-41**：Profile Collection Tab 切换后展示收藏内容（Mock）
- [ ] **C-42**：动态列表与 Feed 卡片组件复用

## 通知中心（Phase 9 / AC-10）
- [ ] **C-43**：通知列表对齐 Notification.png（Today/Yesterday/This week 分组；头像+昵称+类型+时间；未读红点；可清除）
- [ ] **C-44**：顶部通知铃铛在存在未读通知时显示红点
- [ ] **C-45**：WorkManager `NotificationSyncWorker` 可周期生成 Mock 通知（测试可通过 `WorkManagerTestInitHelper`）

## 设置与登出（Phase 10 / AC-11）
- [ ] **C-46**：Settings 页对齐 Settings.png（用户信息、Account/Chat/New Group/Security/Notification/Help/Log out 选项）
- [ ] **C-47**：Log out 后清除 DataStore token 并返回 Login
- [ ] **C-48**：各子项点击进入对应占位页（样式统一）

## 视频通话（Phase 11 / AC-12）
- [ ] **C-49**：Video call 页对齐 Video call.png（双视频窗、左下紫色静音图标+时长、右下角 PIP、底部操作栏）
- [ ] **C-50**：红色挂断按钮返回聊天页

## 构建 & 测试（Phase 12 / AC-13）
- [ ] **C-51**：`./gradlew clean assembleDebug` 成功（无红报错、无版本冲突）
- [ ] **C-52**：`./gradlew testDebugUnitTest` 全部通过
- [ ] **C-53**：LeakCanary 仅 debugImplementation，release 不包含
- [ ] **C-54**：所有核心 ViewModel / DAO 有对应单元测试（StateFlow 状态机、DAO CRUD）
- [ ] **C-55**：首次启动应用后 Room 已插入默认 Mock 数据（用户/动态/消息/通知/好友/附近的人）

## 代码规范 & 安全（NFR-3 / NFR-5）
- [ ] **C-56**：无 `!!` 滥用；空安全遵循 Kotlin 规范
- [ ] **C-57**：字符串/尺寸/颜色抽取至 `res/values`，未硬编码
- [ ] **C-58**：Compose 内不直接写异步逻辑，事件回调至 ViewModel
- [ ] **C-59**：Composable 使用 `@Immutable`/`@Stable` 优化核心数据类（如项目规则要求）
- [ ] **C-60**：未引入禁止的第三方库（Ktor/Glide/Koin/MockK/等均未出现）
