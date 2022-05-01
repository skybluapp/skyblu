package com.skyblu.dependancyinjection

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.skyblu.data.Repository
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.authentication.FirebaseAuthentication
import com.skyblu.data.datastore.DataStoreRepository
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.firestore.FireStoreRead
import com.skyblu.data.firestore.FirestoreWrite
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.storage.FirebaseStorage
import com.skyblu.data.storage.StorageInterface
import com.skyblu.data.users.SavedSkydives
import com.skyblu.data.users.SavedSkydivesInterface
import com.skyblu.data.users.SavedUsers
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.jumptracker.service.SkybluAppService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTestString(): String {
        return "This is a test string"
    }

    @Singleton
    @Provides
    fun provideDatastore(@ApplicationContext appContext: Context): DataStore<androidx.datastore.preferences.core.Preferences> {
        return appContext.dataStore
    }

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext applicationContext: Context): Context {
        return applicationContext
    }

    @Singleton
    @Provides
    fun provideService(@ApplicationContext activityContext: Context): SkybluAppService {
        return SkybluAppService(context = activityContext)
    }

    @Singleton
    @Provides
    fun provideRepository(
        authentication: AuthenticationInterface,
        datastoreInterface: DatastoreInterface,
        readServerInterface: ReadServerInterface,
        savedUsersInterface: SavedUsersInterface,
        savedSkydives: SavedSkydivesInterface,
        storageInterface: StorageInterface,
        writeServerInterface: WriteServerInterface
    ): Repository {
        return Repository(
            authenticationInterface = authentication,
            datastoreInterface = datastoreInterface,
            readServerInterface = readServerInterface,
            savedSkydivesInterface = savedSkydives,
            savedUsersInterface = savedUsersInterface,
            storageInterface = storageInterface,
            writeServerInterface = writeServerInterface
        )
    }

//    @Singleton
//    @Provides
//    fun provideUserDao(database: AppDatabase): TrackingPointsDao {
//        return database.trackingPointsDao()
//    }

//    @Singleton
//    @Provides
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "DATABASE",
//        )
//            .fallbackToDestructiveMigration()
//            .build()
//    }

    @Singleton
    @Provides
    fun provideAuthentication(): FirebaseAuthentication {
        return FirebaseAuthentication()
    }

    @Singleton
    @Provides
    fun provideFirestoreWrite(): FirestoreWrite {
        return FirestoreWrite()
    }

    @Singleton
    @Provides
    fun provideFirestoreRead(): FireStoreRead {
        return FireStoreRead()
    }

    @Singleton
    @Provides
    fun provideStorage(): FirebaseStorage {
        return FirebaseStorage()
    }

    @Singleton
    @Provides
    fun provideSavedUsers(authentication: AuthenticationInterface, fireStore: FireStoreRead): SavedUsers {
        return SavedUsers(authentication, fireStore)
    }

    @Singleton
    @Provides
    fun provideSavedSkydive(): SavedSkydives {
        return SavedSkydives()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindings {

    @Singleton
    @Binds
    abstract fun bindingFunction(dataStoreRepository: DataStoreRepository): DatastoreInterface

    @Singleton
    @Binds
    abstract fun readServerInterface(fireStore: FireStoreRead): ReadServerInterface

    @Singleton
    @Binds
    abstract fun writeServerInterface(fireStore: FirestoreWrite): WriteServerInterface

    @Singleton
    @Binds
    abstract fun firebaseAuthentication(firebaseAuthentication: FirebaseAuthentication): AuthenticationInterface

    @Singleton
    @Binds
    abstract fun clientToServiceInterface(cTsInterface: SkybluAppService): ClientToService

    @Singleton
    @Binds
    abstract fun firebaseStorage(firebaseStorage: FirebaseStorage): StorageInterface

    @Singleton
    @Binds
    abstract fun savedUsers(savedUsers: SavedUsers): SavedUsersInterface

    @Singleton
    @Binds
    abstract fun savedSkydive(savedSkydives: SavedSkydives): SavedSkydivesInterface

}

@ActivityScoped
interface PermissionsInterface {

    fun requestPermission(vararg permissions: String)
    fun checkPermissions(vararg permissions: String): Boolean
}

@ActivityScoped
class PermissionsInterfaceImpl(private val activity: Activity) : PermissionsInterface {

    override fun requestPermission(vararg permissions: String) {
        EasyPermissions.requestPermissions(
            pub.devrel.easypermissions.PermissionRequest.Builder(
                activity,
                1,
                *permissions
            )
                .build(),
        )
    }

    override fun checkPermissions(vararg permissions: String): Boolean {
        return EasyPermissions.hasPermissions(
            activity,
            *permissions
        )
    }
}

