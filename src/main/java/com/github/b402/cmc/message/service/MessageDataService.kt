package com.github.b402.cmc.message.service

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.message.Message
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MessageDataService : DataService<SubmitData>(
        "message_data",
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
        val array = JsonArray()
        for (index in indexs) {
            jobs += GlobalScope.launch(GlobalScope.coroutineContext) {
                val msg = Message.getMessage(uid, index).await()
                val obj = JsonObject()
                obj.addProperty("index", index)
                if (msg == null) {
                    obj.addProperty("status", ERROR)
                    obj.addProperty("reason", "数据库异常")
                } else {
                    obj.addProperty("status", SUCCESS)
                    obj.add("message", Configuration.parser.parse(msg.data.toJson()))
                    obj.addProperty("read", msg.read)
                }
                array.add(obj)
            }
        }
        jobs.forEach { it.join() }
        return returnData(SUCCESS) {
            json.add("datas", array)
        }
    }
}