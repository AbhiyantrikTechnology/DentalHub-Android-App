package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.entities.User
import com.abhiyantrik.dentalhub.entities.User_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception
import com.abhiyantrik.dentalhub.models.User as UserModel

class DownloadUsersWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var usersBox: Box<User>

    override fun doWork(): Result {
        return try {
            usersBox = ObjectBox.boxStore.boxFor(User::class.java)
            downloadUsers()
            return Result.success()
        }catch (e: Exception){
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun downloadUsers() {

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.listUsers("JWT $token")

        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val allUsers = response.body() as List<UserModel>
                    Log.d("DownloadUserWorkers", response.body().toString())
                    for(user in allUsers){
                        Log.d("DownloadUserWorkers", user.first_name + " " + user.middle_name + " " + user.last_name)
                        val existingUserCount = usersBox.query().equal(
                            User_.remote_id,
                            user.id
                        ).build().count()
                        if(existingUserCount<1){
                            val userEntity = User()
                            Log.d("DownloadUserWorkers", user.first_name + " " + user.middle_name + " " + user.last_name)
                            userEntity.remote_id = user.id
                            userEntity.first_name = user.first_name
                            if (user.middle_name != null) {
                                userEntity.middle_name = user.middle_name
                            }
                            userEntity.last_name = user.last_name
                            usersBox.put(userEntity)
                        }else{
                            Log.d("DUser","User already download")
                        }
                    }
                }
            }
        }
    }
}
