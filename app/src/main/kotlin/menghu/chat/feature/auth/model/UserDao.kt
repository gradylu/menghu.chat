package menghu.chat.feature.auth.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 用户表 DAO 接口
 * 定义 user 表的增删改查操作
 */
@Dao
interface UserDao {

    /** 插入用户（冲突时替换） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /** 批量插入用户 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    /** 更新用户信息 */
    @Update
    suspend fun updateUser(user: UserEntity)

    /** 根据 ID 查询用户 */
    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    /** 根据邮箱查询用户 */
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /** 查询所有用户（Flow 形式，可观察变化） */
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    /** 查询所有用户（一次性） */
    @Query("SELECT * FROM user")
    suspend fun getAllUsersList(): List<UserEntity>

    /** 删除指定用户 */
    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUser(id: Long)

    /** 清空用户表 */
    @Query("DELETE FROM user")
    suspend fun clearAll()
}
