package com.github.b402.cmc.message.service

import com.github.b402.cmc.core.Permission
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.message.Message
import com.google.gson.JsonArray

class UnreadMessageService : DataService<SubmitData>(
        "message_unread",
        Permission.VERIFIED,
        SubmitData::class.java
) {
    override suspend fun onRequest(data: SubmitData): ReturnData {
        val uid = data.token!!.uid
        val list = Message.getMessageIndex(uid, false).await()
                ?: return returnData(ERROR, "数据库异常")
        return returnData(SUCCESS) {
            val array = JsonArray()
            for (index in list) array.add(index)
            json.add("list", array)
        }
    }
}