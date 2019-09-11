package com.github.b402.cmc.message.service

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.ReturnData
import com.github.b402.cmc.core.service.data.SUCCESS
import com.github.b402.cmc.core.service.data.SubmitData
import com.github.b402.cmc.core.service.data.returnData
import com.github.b402.cmc.message.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReadMessageService : DataService<SubmitData>(
        "message_read",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val uid = data.token!!.uid
        val d = data.json.get("index")
        val indexs = mutableListOf<Int>()
        if (d is Number) {
            indexs += d.toInt()
        } else if (d is List<*>) {
            for (any in d) {
                indexs += (any as? Number)?.toInt() ?: continue
            }
        }
        val jobs = mutableListOf<Job>()
        for (index in indexs) {
            jobs += GlobalScope.launch(GlobalScope.coroutineContext) {
                Message.setRead(index, uid, true).await()
            }
        }
        jobs.forEach { it.join() }
        return returnData(SUCCESS)
    }
}