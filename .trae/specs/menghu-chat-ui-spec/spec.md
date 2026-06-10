# 梦狐社交 App (menghu.chat) - 产品需求文档（基于 UI 设计稿）

## 概述
- **摘要**：基于 ui/ 目录下 20 张设计稿，构建一套完整的 Android 原生社交应用，涵盖用户登录/注册、动态流（Feed）、即时聊天、好友关系、附近的人、个人主页、故事（Story）、通知中心、设置、视频通话等核心功能。严格对齐 MVVM 架构、Jetpack Compose Material3、本地 Mock 数据。
- **目的**：为"梦狐社交 App"提供一份可追溯、可验收、可分阶段交付的功能需求与技术基线，覆盖 20 个设计稿对应页面。
- **目标用户**：18–45 岁社交应用用户；Android 设备用户（最低 Android 8.0 / API 26）。

## 目标
- 交付一套视觉、交互与设计稿 100% 对齐的 UI 层（Compose）
- 打通完整业务闭环：登录 → 浏览动态 → 浏览故事 → 点赞/评论 → 聊天 → 添加好友/附近的人 → 个人主页 → 通知 → 视频通话 → 设置
- 建立可扩展的 MVVM + Hilt + Room + Retrofit(Mock) + DataStore + WorkManager 技术基线
- 所有数据来自本地 Mock，无需真实后端即可编译运行并演示全流程

## 非目标（范围之外）
- 不对接任何真实后端服务器（API 仅走 Mock 拦截器）
- 不实现真实第三方社交登录 SDK（Google/Apple/Facebook 按钮为 UI 占位，点击后以本地 Mock 模拟登录流程）
- 不实现真实 RTC 视频通话（Video Call 页面为 UI 演示，音视频状态由本地模拟）
- 不实现实时推送（通知由本地模拟 + WorkManager 轮询生成）
- 不实现文件上传下载真实流程（附件选择页为选项弹窗，行为由 Mock 模拟）
- 不进行多语言/国际化（文案与设计稿一致，英文即可）
- 不做后台常驻服务（后台任务仅通过 WorkManager 短作业实现）

## 背景与上下文
- `UI/` 与 `ui/` 目录下 20 张 PNG 设计稿，定义了统一的品牌视觉风格
- 视觉主色为**橙色 (#FF7A3F 近似)**，辅色为**紫色 (#7C4DFF 近似)**；文本灰阶；圆角卡片风格
- 应用底部导航为 **4 Tab**：Feed / Messages / Friends / Profile
- AGENTS.md 规定的技术栈、包结构、注释规范必须 100% 遵守
- 技术基线：Kotlin 2.0、AGP 8.7、Gradle 8.11、minSdk 26、compileSdk/targetSdk 35

## 功能需求（按模块组织，对应设计稿）

### 模块 1：账号体系（Login / Sign up）
- **FR-1.1 登录页**：渐变橙色头部、白色圆角卡片，包含邮箱输入、密码输入（带显示/隐藏切换）、"Forgot password?"、"Log in" 主按钮、Google/Apple/Facebook 三方登录占位按钮、底部"Don't have an account? Sign up"链接 → 跳转注册页
- **FR-1.2 注册页**：渐变橙色头部、白色圆角卡片，包含姓名、邮箱、密码（带显示/隐藏切换）、"Register" 主按钮、Google/Apple/Facebook 三方登录占位、底部"Already have an account? Log in"链接 → 跳转登录页
- **FR-1.3 认证状态持久化**：登录/注册成功后使用 DataStore 持久化 token 与用户信息；启动 App 时若存在 token 直接进入首页

### 模块 2：底部导航 + 顶部全局组件
- **FR-2.1 底部导航栏**：4 个 Tab（Feed / Messages / Friends / Profile），选中态为橙色，未选中态为灰色图标；跨 Tab 切换状态由 Navigation Compose 管理
- **FR-2.2 通用 TopBar**：大多数页面顶部包含左侧菜单按钮（三条横线）、居中橙色标题（如 Feeds / Messages / Friends / Notification / Settings / Profile 等）、右侧通知铃铛（带红点）
- **FR-2.3 全局主题**：Material3 主题，主色橙色、圆角卡片、统一字体与间距

### 模块 3：动态流与故事（Feeds / Story）
- **FR-3.1 动态列表页 Feeds**
  - 顶部一排圆形头像故事（带头像+昵称，首个头像有 "+" 加号表示"发布故事"）
  - 动态卡片：用户头像、用户名、发布时间（如 "2 minutes"）、更多按钮 `...`、文字内容、图片（支持 1张大图 + 右侧2张小图组合布局；以及 2张横排等组合布局）
  - 底部：评论图标 + 数量（如 "24k"）、点赞图标 + 数量（如 "50k"）
  - 支持下拉刷新、分页滚动；使用 LazyColumn
- **FR-3.2 故事全屏页 Story**
  - 全屏大图背景
  - 顶部：圆形用户头像 + 昵称 + 时间（如 "1h"）
  - 底部：附件图标、评论输入框"be the first to comment"、表情图标、发送图标
  - 支持滑动关闭或点击底部发送评论（Mock）

### 模块 4：发布动态（New Post / New Post 2）
- **FR-4.1 发布页 1（图片编辑）**
  - 顶部 Tab：Post / Story / Live（当前选中 Post，其余置灰）
  - 中间大图预览，上方关闭按钮 `\`
  - 下方一排横向缩略图滤镜（Normal + 多种滤镜），Filter / Edit 切换
  - 下方 Gallery 网格缩略图，右侧浮动铅笔编辑按钮与相机按钮
- **FR-4.2 发布页 2（文字描述）**
  - 顶部：图片预览 + "+" 添加按钮
  - 多行输入框 "What you want to say?"
  - 选项行：Tag people / Add location / Add music / Feeling or activity
  - "Share to"：Facebook / Instagram / twitter 三个开关
  - 底部橙色"Post Now" 主按钮 → 提交后回到 Feeds，新增动态置顶

### 模块 5：评论页（coments）
- **FR-5.1 评论详情页**
  - 顶部：头像 + "Comments 24k from ralrh edrward post" 标题 + 时间，右上角关闭按钮
  - 评论列表：评论者头像 + 昵称 + 时间 + 评论内容 + 评论下的"评论数 3k/点赞数 1,3k"图标；支持点赞动效（红色拇指）
  - 底部：附件图标、评论输入框"be the first to comment"、紫色发送按钮

### 模块 6：即时消息（Messages / Chat / Attachement file）
- **FR-6.1 会话列表 Messages**
  - 顶部搜索框
  - 列表项：用户圆形头像（外圈紫色描边）、昵称、最新消息预览、时间（Just now / 2 mins / 1 hours）
  - 使用 LazyColumn，可点击进入单聊页
- **FR-6.2 单聊页 Chat**
  - 顶部：返回箭头、用户头像、昵称、在线状态（"Online" 绿色小圆点）、电话图标、视频图标
  - 聊天气泡：自己的消息为橙色右侧气泡、对方消息为白色左侧气泡；均带时间（如 "2 minutes"）
  - "正在输入..." 气泡（三个点动画）
  - 底部：回形针附件图标、输入框"I will wait fo..."、紫色发送按钮
- **FR-6.3 附件选择弹窗 Attachement file**：底部弹出半屏 "Select Option"，九宫格图标：Camera / Galery / File / Location / Audio / Contact；右上角关闭按钮

### 模块 7：好友与附近的人（Friends / nearby / nearby - friends / send request / Follower Profile）
- **FR-7.1 好友列表 Friends**
  - 顶部搜索框 + 右侧 A–K 字母索引条
  - 两列网格卡片：头像大图 + 姓名 + 国家（如 United states / Spain / England）
- **FR-7.2 附近的人雷达 nearby**：渐变橙色背景，中央蓝色箭头图标，周围环形排列附近用户头像；底部"Searching people nearby..."
- **FR-7.3 附近用户列表 nearby - friends**：两列网格卡片，展示头像、姓名、距离（2km / 10km / 13km / 15km / 17km / 20km）；可点击进入用户详情
- **FR-7.4 发送好友请求 send request**：全屏用户大图背景、姓名、距离、底部橙色"Send friend request" 主按钮
- **FR-7.5 关注者个人主页 Follower Profile**：头像+昵称+职业标签（如 Photographer @Cannon）+ 个性签名、统计行（Post 24,000 / Followers 4,2m / Friends 3,000）、"Follow" 橙色主按钮 + "Chat" 次要按钮、"Posts / Collection" Tab、动态列表（同 Feeds 卡片样式）

### 模块 8：个人主页（Profile Posts / Profile Collection）
- **FR-8.1 个人主页（Posts Tab）Profile Posts**：头像、昵称、职业标签（Designer @HelloUI）、签名（Independent woman woman 😊）、统计（Post 1,312 / Followers 2,1m / Friends 2,523）、"Posts / Collection" Tab、动态列表卡片（同 Feeds 卡片样式）
- **FR-8.2 个人主页（收藏 Tab）Profile Collection**：同布局但 Tab 切换到 "Collection"，展示收藏内容列表（Mock 数据）

### 模块 9：通知中心（Notification）
- **FR-9.1 通知列表**：分组 "Today / Yesterday / This week"；每条通知含头像、昵称、类型文案（respond to your story / Like your story / Started follow you）、相对时间（1 min ago / 20 min ago / 1 day ago / 1 week ago）；未读红点（如 Natalia 的粉色红点）；单项可右滑或点 × 清除

### 模块 10：设置（Settings）
- **FR-10.1 设置列表**：顶部展示当前用户（头像、昵称、邮箱），选项行含图标+标题+副标题+箭头：Account（Privacy, Change Number）、Chat（Theme, walpaper, chat history）、New Group（Create Group from Contacts）、Security（Change Password）、Notification（Message, group, ringtone）、Help（Help center, contact us, privacy policy）
- **FR-10.2 登出**：设置页底部提供退出登录入口（可选：由 Security 子页或单独按钮），清除 DataStore 的 token 并返回 Login

### 模块 11：视频通话（Video call）
- **FR-11.1 视频通话页**：上下双视频窗（对方在上、本地在下）、左下显示通话时长+静音紫色圆形图标、右下角本地视频小窗（PIP）、底部操作栏：麦克风、扬声器、摄像头、聊天、挂断（红色 ×）；所有状态本地 Mock

## 非功能需求
- **NFR-1 性能**：Feed/聊天/好友列表滚动 60fps；首次冷启动 ≤ 2.5s（基准中端机型）；Compose 启用 metricsDestination 和 reportsDestination
- **NFR-2 离线可用**：所有核心页面（Feed/消息/好友/个人主页/通知/设置）具备本地 Room 缓存，断网亦可浏览
- **NFR-3 代码规范**：100% 中文注释、MVVM 分层清晰、Hilt 注入、协程 Flow/StateFlow
- **NFR-4 可构建性**：`./gradlew assembleDebug` 无红报错、无版本冲突；`./gradlew testDebugUnitTest` 通过
- **NFR-5 安全**：不使用 `!!` 滥用；登录状态走 DataStore；敏感字符串不硬编码（Mock 场景下 token 字段仍按规范处理）
- **NFR-6 UI 规范**：严格按 ui/ 设计稿配色、圆角、字体、间距、图标风格；Material3 控件

## 约束
- **技术**：Kotlin 2.0 + Jetpack Compose + Material3 + Hilt + Room + Retrofit(OkHttp Mock) + DataStore + WorkManager + Coil + Timber；禁止引入额外第三方库
- **业务**：所有 API 返回本地 Mock DTO；图片可从网络占位图或 res drawable 提供
- **兼容**：minSdk=26，compileSdk=targetSdk=35；不可随意调整
- **架构**：强制 MVVM，禁止 MVI/Clean/Koin/Glide/Ktor 等替换方案

## 假设
- 应用首次启动时，由 Hilt 注入的初始化任务向 Room 写入默认 Mock 用户、会话、动态、通知数据
- 用户首次进入首页前完成 Mock 数据初始化并显示加载态
- 附近的人距离、动态点赞数、通知时间等数据由 Mock 随机/固定样例生成
- 视频通话、文件附件等真实能力以 Mock UI + 点击反馈演示

## 验收标准（对应设计稿编号）

### AC-1：登录与注册流程
- **Given** 已安装应用首次打开
- **When** 用户输入邮箱+密码点击 Log in（或 Register）
- **Then** 验证表单（非空/邮箱格式）→ Mock 返回成功 → DataStore 写入登录态 → 跳转首页 Feeds
- **Verification**：programmatic（ViewModel 单元测试 + 编译通过） + human-judgment（视觉对齐 Login.png / Sign up.png）

### AC-2：底部导航与全局主题
- **Given** 用户处于登录状态
- **When** 点击底部 Tab
- **Then** 正确切换到 Feed / Messages / Friends / Profile 四个页面，Tab 选中态颜色与设计稿一致
- **Verification**：human-judgment + programmatic（Navigation 路由测试）

### AC-3：动态流 Feeds
- **Given** 进入 Feeds Tab
- **When** 向下滚动
- **Then** 故事横排、动态卡片按设计稿 Feeds.png 显示；图片布局组合多样；评论/点赞计数展示正确
- **Verification**：human-judgment + programmatic（LazyColumn items key 不为空）

### AC-4：故事 Story
- **Given** 点击 Feeds 顶部故事头像
- **When** 进入 Story 全屏页
- **Then** 全屏大图 + 顶部用户信息 + 底部评论栏；可评论（Mock 提交反馈）
- **Verification**：human-judgment

### AC-5：发布 New Post（两步）
- **Given** 点击发布入口（Feeds 顶部"+"或导航中央按钮，按设计稿规范）
- **When** 进入 New Post 选择图片 → 编辑滤镜 → 到 New Post 2 填写文案 → 点 Post Now
- **Then** 新动态出现在 Feeds 顶部，时间为 "Just now"
- **Verification**：human-judgment + programmatic（Room 写入 + 列表刷新测试）

### AC-6：评论页
- **Given** 在 Feeds 点击评论图标或评论数
- **When** 进入评论页
- **Then** 布局与 coments.png 一致；可输入并发送评论（Mock 插入并显示）
- **Verification**：human-judgment + programmatic

### AC-7：消息列表与单聊
- **Given** 进入 Messages Tab
- **When** 点击某一会话
- **Then** 进入聊天页；气泡样式与 Chat.png 一致；可发送文字/附件（附件弹窗与 Attachement file.png 一致）
- **Verification**：human-judgment + programmatic

### AC-8：好友与附近的人
- **Given** 进入 Friends Tab 或附近的人入口
- **When** 浏览列表/雷达/详情
- **Then** Friends.png / nearby.png / nearby - friends.png / send request.png / Follower Profile.png 样式完全对齐
- **Verification**：human-judgment

### AC-9：个人主页 Posts / Collection
- **Given** 进入 Profile Tab 或 Follower Profile
- **When** 在 Posts 与 Collection 间切换
- **Then** 布局与 Profile Posts.png / Profile Collection.png 一致；统计数字正确显示
- **Verification**：human-judgment

### AC-10：通知中心
- **Given** 进入 Notification 页面
- **When** 查看通知列表
- **Then** Today/Yesterday/This week 分组正确，未读红点显示正确，可清除
- **Verification**：human-judgment + programmatic（Room + WorkManager Mock）

### AC-11：设置与登出
- **Given** 进入 Settings 页面
- **When** 点击 Account / Chat / New Group / Security / Notification / Help 或退出登录
- **Then** 页面样式与 Settings.png 一致，点击后进入对应子页（子页可暂用简单占位，样式保持一致）；退出登录返回 Login
- **Verification**：human-judgment + programmatic

### AC-12：视频通话
- **Given** 在聊天页点击视频图标
- **When** 进入 Video call 页面
- **Then** 布局与 Video call.png 一致；按钮有点击反馈（状态本地 Mock）
- **Verification**：human-judgment

### AC-13：编译与测试
- **Given** 代码提交完成
- **When** 运行 `./gradlew assembleDebug` 与 `./gradlew testDebugUnitTest`
- **Then** 编译成功，单元测试通过
- **Verification**：programmatic

## 未解决问题
- [ ] 登出入口在设计稿 Settings.png 中未明确给出，需产品确认：放在 Security 子项还是独立按钮（默认方案：Settings 列表最后添加 "Log out" 选项）
- [ ] 附近的人入口（nearby.png / nearby - friends.png）从哪个页面进入？（默认方案：Friends Tab 顶部加"附近的人"入口按钮或 Friends TopBar 菜单内）
- [ ] New Post 发布入口在设计稿底部导航中无独立 Tab，默认方案：Feeds TopBar 或悬浮 "+" 按钮
- [ ] 视频通话（Video call.png）的结束流程是否需要返回聊天页？（默认：点击红色挂断 → 返回 Chat）
- [ ] 通知铃铛红点规则：所有未读 ≥1 时显示红点（默认方案）
