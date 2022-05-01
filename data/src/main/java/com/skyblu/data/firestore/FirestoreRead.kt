package com.skyblu.data.firestore

import com.google.firebase.firestore.*
import com.skyblu.configuration.DATAPOINTS_COLLECTION
import com.skyblu.configuration.JUMPS_COLLECTION
import com.skyblu.configuration.TIMEOUT_MILLIS
import com.skyblu.configuration.USERS_COLLECTION
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.UserParameterNames
import kotlinx.coroutines.delay
import timber.log.Timber

class FireStoreRead : ReadServerInterface {

    val firestore = FirebaseFirestore.getInstance()
    private val jumpCollection = firestore.collection(JUMPS_COLLECTION)
    val usersCollection = firestore.collection(USERS_COLLECTION)

    /**
     * Gets a list of jumps from server limited to pagesize
     * @param pageSize How many jumps to return at a time
     * @param page The jump to start from in the list
     * @param fromUsers A list of users to return jumps from. If no will return jumps from all users
     */
    override suspend fun getJumps(
        page: DocumentSnapshot?,
        pageSize: Int,
        fromUsers: List<String>?
    ): Result<QuerySnapshot> {
        var result: Result<QuerySnapshot>? = null
        val startTime = System.currentTimeMillis()



        val firestoreReference = if (page == null) {
            if (fromUsers.isNullOrEmpty()) {
                firestore.collection(JUMPS_COLLECTION).whereEqualTo(JumpParams.JUMP_ID, "123")
            } else {
                firestore.collection(JUMPS_COLLECTION)
                    .whereIn(
                        JumpParams.USER_ID,
                        fromUsers
                    )
                    .orderBy(
                        JumpParams.DATE,
                        Query.Direction.DESCENDING
                    )
            }
        } else {
            if (fromUsers == null) {
                firestore.collection(JUMPS_COLLECTION)
                    .orderBy(
                        JumpParams.DATE,
                        Query.Direction.DESCENDING
                    )
                    .startAfter(page)
            } else {
                firestore.collection(JUMPS_COLLECTION)
                    .whereIn(
                        JumpParams.USER_ID,
                        fromUsers
                    )
                    .orderBy(
                        JumpParams.DATE,
                        Query.Direction.DESCENDING
                    )
                    .startAfter(page)
            }
        }


            firestoreReference
                .limit(pageSize.toLong())
                .get()
                .addOnSuccessListener { jumpDocuments ->
                    result = Result.success(jumpDocuments)
                }
                .addOnFailureListener {
                    result = Result.failure(it)
                }


        while (result == null) {
            if (timeout(startTime)) {
                return Result.failure(Exception())
            }
            delay(1000)
        }

        return result as Result<QuerySnapshot>
    }

    /**
     * Gets a list of users from server limited to pagesize
     * @param pageSize How many jumps to return at a time
     * @param page The jump to start from in the list
     * @param search A list of users to return jumps from. If no will return jumps from all users
     */
    override suspend fun getUsers(
        page: DocumentSnapshot?,
        pageSize: Int,
        search: String
    ): Result<QuerySnapshot> {
        var result: Result<QuerySnapshot>? = null
        val startTime = System.currentTimeMillis()

        val firestoreReference = if (page == null) {
            firestore.collection(USERS_COLLECTION)
                .whereEqualTo(
                    UserParameterNames.USERNAME,
                    search
                )
        } else {
            usersCollection
                .whereEqualTo(
                    UserParameterNames.USERNAME,
                    search
                )
                .startAfter(page)
        }

        firestoreReference
            .limit(pageSize.toLong())
            .get()
            .addOnSuccessListener { userDocuments ->
                result = Result.success(userDocuments)
            }
            .addOnFailureListener {
                result = Result.failure(it)
            }

        while (result == null) {
            if (timeout(startTime)) {
                return Result.failure(Exception())
            }
            delay(1000)
        }

        return result as Result<QuerySnapshot>
    }

    /**
     * Returns a single jump from firestore
     * @param id The jumpID of the jump to return
     */
    override suspend fun getJump(id: String): Result<DocumentSnapshot?> {

        val firestore = FirebaseFirestore.getInstance()
        var result: Result<DocumentSnapshot?>? = null
        val startTime = System.currentTimeMillis()

        firestore.collection(JUMPS_COLLECTION).document(id).get()
            .addOnSuccessListener { document ->
                result = Result.success(document)
                if (document.exists()) {
                    result = Result.success(document)
                } else {
                    result = Result.success(null)
                }
            }
            .addOnFailureListener {
                result = Result.failure(java.lang.Exception())
            }

        while (result == null) {
            if (timeout(startTime)) {
                return Result.failure(Exception())
            }
            delay(100)
        }
        return result as Result<DocumentSnapshot>
    }

    /**
     * Returns all datapoints from a jump
     * @param jumpID ID of the jump to return the datapoints of
     */
    override suspend fun getDatapoints(
        jumpID: String
    ): Result<QuerySnapshot> {

        var result: Result<QuerySnapshot>? = null
        val startTime = System.currentTimeMillis()
        val documentReference =
            firestore.collection(JUMPS_COLLECTION).document(jumpID)
                .collection(DATAPOINTS_COLLECTION).get()
        documentReference
            .addOnSuccessListener { datapointDocuments ->
                result = Result.success(datapointDocuments)
            }
            .addOnFailureListener {
                result = Result.failure(it)
            }

        while (result == null) {
            if (timeout(startTime)) {
                return Result.failure(Exception())
            }
            delay(1000)
        }
        return result as Result<QuerySnapshot>
    }

    /**
     * @param userID The id of the user to return
     */
    override suspend fun getUser(userID: String): Result<DocumentSnapshot?> {
        val firestore = FirebaseFirestore.getInstance()
        var result: Result<DocumentSnapshot?>? = null
        val startTime = System.currentTimeMillis()


        firestore.collection(USERS_COLLECTION).document(userID).get()
            .addOnSuccessListener { document ->
                result = Result.success(document)
                if (document.exists()) {
                    result = Result.success(document)
                } else {
                    result = Result.success(null)
                }
            }
            .addOnFailureListener {
                result = Result.failure(java.lang.Exception())
            }

        while (result == null) {
            if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
                return Result.failure(Exception())
            }
            delay(100)
        }

        return result as Result<DocumentSnapshot>
    }

}
interface ReadServerInterface {

    suspend fun getJumps(
        page: DocumentSnapshot?,
        pageSize: Int,
        fromUsers: List<String>? = null
    ): Result<QuerySnapshot>
    suspend fun getUsers(
        page: DocumentSnapshot?,
        pageSize: Int,
        search : String
    ): Result<QuerySnapshot>

    suspend fun getJump(jumpID: String): Result<DocumentSnapshot?>
    suspend fun getDatapoints(jumpID: String): Result<QuerySnapshot>
    suspend fun getUser(id: String): Result<DocumentSnapshot?>

}



fun timeout(startTime: Long): Boolean {
    return System.currentTimeMillis() - TIMEOUT_MILLIS > startTime
}
