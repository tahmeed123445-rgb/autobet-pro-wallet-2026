package com.example.data

import kotlinx.coroutines.flow.Flow

class WalletRepository(
    private val depositDao: DepositDao,
    private val notificationDao: NotificationDao,
    private val withdrawalDao: WithdrawalDao,
    private val userDao: UserDao,
    private val quickOptionDao: QuickOptionDao,
    private val adminSettingDao: AdminSettingDao,
    private val bannerDao: BannerDao,
    private val paymentMethodDao: PaymentMethodDao
) {
    val allRequests: Flow<List<DepositRequestEntity>> = depositDao.getAllRequests()
    val allWithdrawals: Flow<List<WithdrawalRequestEntity>> = withdrawalDao.getAllWithdrawals()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allQuickOptions: Flow<List<QuickOptionEntity>> = quickOptionDao.getAllQuickOptions()
    val allSettings: Flow<List<AdminSettingEntity>> = adminSettingDao.getAllSettings()
    val allBanners: Flow<List<BannerEntity>> = bannerDao.getAllBanners()
    val allPaymentMethods: Flow<List<PaymentMethodEntity>> = paymentMethodDao.getAllPaymentMethods()

    fun getRequestsForUser(userId: String): Flow<List<DepositRequestEntity>> =
        depositDao.getRequestsForUser(userId)

    fun getWithdrawalsForUser(userId: String): Flow<List<WithdrawalRequestEntity>> =
        withdrawalDao.getWithdrawalsForUser(userId)

    suspend fun registerUser(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUserApproval(userRowId: Long, status: String, bpLink: String, bpUsername: String, bpPassword: String, userId: String) {
        userDao.updateApproval(userRowId, status, bpLink, bpUsername, bpPassword)
        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "BetPro Account Approved!",
                message = "Your BetPro account has been approved by admin. Link: $bpLink, Username: $bpUsername",
                type = "ACCOUNT_APPROVED"
            )
        )
    }

    suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)
    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)

    suspend fun insertQuickOption(option: QuickOptionEntity) {
        quickOptionDao.insertQuickOption(option)
    }

    suspend fun updateQuickOption(id: Long, name: String, url: String, isEnabled: Boolean, displayOrder: Int) {
        quickOptionDao.updateQuickOption(id, name, url, isEnabled, displayOrder)
    }

    suspend fun deleteQuickOption(id: Long) {
        quickOptionDao.deleteQuickOption(id)
    }

    suspend fun setSetting(key: String, value: String) {
        adminSettingDao.setSetting(AdminSettingEntity(key, value))
    }

    suspend fun getSetting(key: String): String? {
        return adminSettingDao.getSetting(key)
    }

    suspend fun insertBanner(banner: BannerEntity) {
        bannerDao.insertBanner(banner)
    }

    suspend fun updateBanner(id: Long, title: String, imageUrl: String, linkUrl: String, isEnabled: Boolean, displayOrder: Int) {
        bannerDao.updateBanner(id, title, imageUrl, linkUrl, isEnabled, displayOrder)
    }

    suspend fun deleteBanner(id: Long) {
        bannerDao.deleteBanner(id)
    }

    suspend fun insertPaymentMethod(method: PaymentMethodEntity) {
        paymentMethodDao.insertPaymentMethod(method)
    }

    suspend fun updatePaymentMethod(id: Long, name: String, accountNumber: String, accountTitle: String, instructions: String, isEnabled: Boolean, displayOrder: Int) {
        paymentMethodDao.updatePaymentMethod(id, name, accountNumber, accountTitle, instructions, isEnabled, displayOrder)
    }

    suspend fun deletePaymentMethod(id: Long) {
        paymentMethodDao.deletePaymentMethod(id)
    }

    suspend fun insertDepositRequest(request: DepositRequestEntity): Long {
        val id = depositDao.insertRequest(request)
        notificationDao.insertNotification(
            NotificationEntity(
                userId = request.userId,
                title = "AutoBet Pro Deposit",
                message = "Your deposit of PKR ${request.amount.toInt()} is pending review.",
                type = "DEPOSIT_PENDING"
            )
        )
        return id
    }

    suspend fun updateDepositStatus(id: Long, status: String, rejectionReason: String?, amount: Double, userId: String) {
        depositDao.updateStatus(id, status, rejectionReason)
        val (title, message) = if (status == "APPROVED") {
            Pair("Deposit Approved", "Your deposit of PKR ${amount.toInt()} has been approved.")
        } else {
            Pair("Deposit Rejected", "Your deposit of PKR ${amount.toInt()} was rejected.")
        }
        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = title,
                message = message,
                type = if (status == "APPROVED") "DEPOSIT_APPROVED" else "DEPOSIT_REJECTED"
            )
        )
    }

    suspend fun insertWithdrawalRequest(request: WithdrawalRequestEntity): Long {
        val id = withdrawalDao.insertWithdrawal(request)
        notificationDao.insertNotification(
            NotificationEntity(
                userId = request.userId,
                title = "AutoBet Pro Withdrawal",
                message = "Your withdrawal of PKR ${request.amount.toInt()} is pending review.",
                type = "WITHDRAWAL_PENDING"
            )
        )
        return id
    }

    suspend fun updateWithdrawalStatus(id: Long, status: String, rejectionReason: String?, amount: Double, userId: String) {
        withdrawalDao.updateStatus(id, status, rejectionReason)
        val (title, message) = if (status == "APPROVED") {
            Pair("Withdrawal Approved", "Your withdrawal of PKR ${amount.toInt()} has been approved.")
        } else {
            Pair("Withdrawal Rejected", "Your withdrawal of PKR ${amount.toInt()} was rejected.")
        }
        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = title,
                message = message,
                type = if (status == "APPROVED") "WITHDRAWAL_APPROVED" else "WITHDRAWAL_REJECTED"
            )
        )
    }

    fun getNotifications(userId: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    suspend fun markNotificationAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }
}
