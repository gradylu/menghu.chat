package menghu.chat.feature.auth.model

/**
 * 模型映射工具：提供 Entity/Domain/Dto 之间的互转方法
 * 保持各层模型解耦，不相互直接依赖
 */

/** DTO -> 数据库实体 */
fun UserDto.toEntity(): UserEntity = UserEntity(
    id = this.id,
    name = this.name,
    email = this.email,
    avatar = this.avatar,
    bio = this.bio,
    occupation = this.occupation,
    postCount = this.postCount,
    followerCount = this.followerCount,
    friendCount = this.friendCount,
    token = this.token
)

/** 数据库实体 -> 领域模型 */
fun UserEntity.toDomain(): UserDomain = UserDomain(
    id = this.id,
    name = this.name,
    email = this.email,
    avatar = this.avatar,
    bio = this.bio,
    occupation = this.occupation,
    postCount = this.postCount,
    followerCount = this.followerCount,
    friendCount = this.friendCount,
    token = this.token
)

/** DTO -> 领域模型 */
fun UserDto.toDomain(): UserDomain = UserDomain(
    id = this.id,
    name = this.name,
    email = this.email,
    avatar = this.avatar,
    bio = this.bio,
    occupation = this.occupation,
    postCount = this.postCount,
    followerCount = this.followerCount,
    friendCount = this.friendCount,
    token = this.token
)

/** 领域模型 -> 数据库实体 */
fun UserDomain.toEntity(): UserEntity = UserEntity(
    id = this.id,
    name = this.name,
    email = this.email,
    avatar = this.avatar,
    bio = this.bio,
    occupation = this.occupation,
    postCount = this.postCount,
    followerCount = this.followerCount,
    friendCount = this.friendCount,
    token = this.token
)
