package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DepositRequestEntity::class,
        NotificationEntity::class,
        WithdrawalRequestEntity::class,
        UserEntity::class,
        QuickOptionEntity::class,
        AdminSettingEntity::class,
        BannerEntity::class,
        PaymentMethodEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun depositDao(): DepositDao
    abstract fun notificationDao(): NotificationDao
    abstract fun withdrawalDao(): WithdrawalDao
    abstract fun userDao(): UserDao
    abstract fun quickOptionDao(): QuickOptionDao
    abstract fun adminSettingDao(): AdminSettingDao
    abstract fun bannerDao(): BannerDao
    abstract fun paymentMethodDao(): PaymentMethodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autobet_wallet_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { dbInstance ->
                                    val qDao = dbInstance.quickOptionDao()
                                    qDao.insertQuickOption(QuickOptionEntity(name = "BetProExch", url = "https://betproexch.com", isEnabled = true, displayOrder = 1))
                                    qDao.insertQuickOption(QuickOptionEntity(name = "BPExch", url = "https://bpexch.com", isEnabled = true, displayOrder = 2))
                                    qDao.insertQuickOption(QuickOptionEntity(name = "BPExch Live", url = "https://bpexchlive.com", isEnabled = true, displayOrder = 3))

                                    val pDao = dbInstance.paymentMethodDao()
                                    pDao.insertPaymentMethod(PaymentMethodEntity(name = "Easypaisa", accountNumber = "03001234567", accountTitle = "AutoBet Official", instructions = "Send funds via Easypaisa and upload screenshot", isEnabled = true, displayOrder = 1))
                                    pDao.insertPaymentMethod(PaymentMethodEntity(name = "JazzCash", accountNumber = "03007654321", accountTitle = "AutoBet Official", instructions = "Send funds via JazzCash and upload screenshot", isEnabled = true, displayOrder = 2))
                                    pDao.insertPaymentMethod(PaymentMethodEntity(name = "NayaPay", accountNumber = "03009988776", accountTitle = "AutoBet Official", instructions = "Transfer via NayaPay", isEnabled = true, displayOrder = 3))
                                    pDao.insertPaymentMethod(PaymentMethodEntity(name = "Bank Transfer", accountNumber = "PK03MEZN0001234567890", accountTitle = "AutoBet Official", instructions = "Bank Alfalah / Meezan Bank transfer", isEnabled = true, displayOrder = 4))
                                    pDao.insertPaymentMethod(PaymentMethodEntity(name = "SadaPay", accountNumber = "03001122334", accountTitle = "AutoBet Official", instructions = "Transfer via SadaPay", isEnabled = true, displayOrder = 5))

                                    val bDao = dbInstance.bannerDao()
                                    bDao.insertBanner(BannerEntity(title = "Download AutoBet Pro APK", imageUrl = "", linkUrl = "https://autobetpro.com/apk", isEnabled = true, displayOrder = 1))

                                    val setDao = dbInstance.adminSettingDao()
                                    if (setDao.getSettingSync("admin_username") == null) {
                                        setDao.setSettingSync(AdminSettingEntity("admin_username", "Admin"))
                                        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest("Admin123".toByteArray())
                                        val hash = bytes.joinToString("") { "%02x".format(it) }
                                        setDao.setSettingSync(AdminSettingEntity("admin_password_hash", hash))
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                try {
                    val setDao = instance.adminSettingDao()
                    if (setDao.getSettingSync("admin_username") == null) {
                        setDao.setSettingSync(AdminSettingEntity("admin_username", "Admin"))
                        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest("Admin123".toByteArray())
                        val hash = bytes.joinToString("") { "%02x".format(it) }
                        setDao.setSettingSync(AdminSettingEntity("admin_password_hash", hash))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                instance
            }
        }
    }
}
