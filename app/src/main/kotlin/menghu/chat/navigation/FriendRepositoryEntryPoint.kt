package menghu.chat.navigation

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import menghu.chat.feature.friend.repository.FriendRepository
import menghu.chat.feature.moment.repository.MomentRepository

/**
 * Hilt EntryPoint，用于在非 @AndroidEntryPoint 的 Composable 环境中获取单例对象
 * - 供 MenghuNavHost 手动获取 FriendRepository，便于 SendRequestScreen 使用
 * - MomentRepository 预留给 FollowerProfile 使用
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface FriendRepositoryEntryPoint {
    fun friendRepository(): FriendRepository
    fun momentRepository(): MomentRepository
}
