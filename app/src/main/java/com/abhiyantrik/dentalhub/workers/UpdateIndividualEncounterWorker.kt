package com.abhiyantrik.dentalhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class UpdateIndividualEncounterWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        return try {
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }

    }
}