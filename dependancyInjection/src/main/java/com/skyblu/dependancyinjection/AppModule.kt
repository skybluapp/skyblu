package com.skyblu.dependancyinjection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.skyblu.data.Repository
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.authentication.FirebaseAuthentication
import com.skyblu.data.datastore.Datastore
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.firestore.FireStoreRead
import com.skyblu.data.firestore.FirestoreWrite
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.memory.SavedSkydive
import com.skyblu.data.memory.SavedSkydiveInterface
import com.skyblu.data.permissions.EasyPermissions
import com.skyblu.data.permissions.PermissionsInterface
import com.skyblu.data.storage.FirebaseStorage
import com.skyblu.data.storage.StorageInterface

import com.skyblu.data.memory.SavedUsers
import com.skyblu.data.memory.SavedUsersInterface
import com.skyblu.data.workManager.AndroidWorkManager
import com.skyblu.data.workManager.WorkManagerInterface
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.jumptracker.service.SkybluAppService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("")

/**
 * Dependancy Injection module for Android version of Skyblu
 * Provides instances of interfaces used throughout the app to provide data to viewmodels
 */
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
        savedSkydives: SavedSkydiveInterface,
        storageInterface: StorageInterface,
        writeServerInterface: WriteServerInterface,
        permissionsInterface: PermissionsInterface,
        workManagerInterface: WorkManagerInterface
    ): Repository {
        return Repository(
            authenticationInterface = authentication,
            datastoreInterface = datastoreInterface,
            readServerInterface = readServerInterface,
            savedSkydivesInterface = savedSkydives,
            savedUsersInterface = savedUsersInterface,
            storageInterface = storageInterface,
            writeServerInterface = writeServerInterface,
            permissionsInterface = permissionsInterface,
            workManager = workManagerInterface
        )
    }


    @Singleton
    @Provides
    fun provideAuthentication(): FirebaseAuthentication {
        return FirebaseAuthentication()
    }


    @Singleton
    @Provides
    fun provideFirestoreWrite(context : Context): FirestoreWrite {
        return FirestoreWrite(context)
    }

    @Singleton
    @Provides
    fun provideWorkManager(context : Context): AndroidWorkManager {
        return AndroidWorkManager(context)
    }

    @Singleton
    @Provides
    fun provideFirestoreRead(): FireStoreRead {
        return FireStoreRead()
    }

    @Singleton
    @Provides
    fun provideStorage(context: Context): FirebaseStorage {
        return FirebaseStorage(context)
    }

    @Singleton
    @Provides
    fun provideSavedUsers(authentication: AuthenticationInterface, fireStore: FireStoreRead): SavedUsers {
        return SavedUsers(authentication, fireStore)
    }

    @Singleton
    @Provides
    fun provideSavedSkydive(): SavedSkydive {
        return SavedSkydive()
    }

    @Singleton
    @Provides
    fun providePermissions(context: Context): EasyPermissions {
        return EasyPermissions(context)
    }
}

/**
 * Binds interfaces to specific implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindings {

    @Singleton
    @Binds
    abstract fun bindingFunction(dataStoreRepository: Datastore): DatastoreInterface

    @Singleton
    @Binds
    abstract fun readServerInterface(fireStore: FireStoreRead): ReadServerInterface

    @Singleton
    @Binds
    abstract fun writeServerInterface(fireStore: FirestoreWrite): WriteServerInterface

    @Singleton
    @Binds
    abstract fun workManagerInterface(workManager: AndroidWorkManager): WorkManagerInterface
//
//    @Singleton
//    @Binds
//    abstract fun localJumpInterface(localJumpText: LocalJumpText): LocalJumpTextInterface

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
    abstract fun savedSkydive(savedSkydives: SavedSkydive): SavedSkydiveInterface

    @Singleton
    @Binds
    abstract fun permissions(permissions: EasyPermissions): PermissionsInterface

}
