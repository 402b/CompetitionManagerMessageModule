package com.github.b402.cmc.message

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.sql.SQLManager

data class Message(
        val index: Int,
        val uid: Int,
        val data: Configuration,
        val read: Boolean
) {

    suspend fun sync() = SQLManager.async {
        val ps = this.prepareStatement("UPDATE Message SET Data = ?, Read = ? WHERE Index = ?")
        ps.setString(1, data.toJson())
        ps.setBoolean(2, read)
        ps.setInt(3, index)
        ps.executeUpdate()
    }

    companion object {

        suspend fun addMessage(uid: Int, data: Configuration) = SQLManager.async {
            val ps = this.prepareStatement("INSERT INTO Message (UID,Data) VALUES (?,?)")
            ps.setInt(1, uid)
            ps.setString(2, data.toJson())
            ps.executeUpdate()
        }

        suspend fun getMessage(uid: Int, read: Boolean = false) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT * FROM Message WHERE UID = ? AND Read = ?")
            ps.setInt(1, uid)
            ps.setBoolean(2, read)
            val rs = ps.executeQuery()
            val list = mutableListOf<Message>()
            while (rs.next()) {
                val index = rs.getInt(1)
                val json = rs.getString(3)
                list += Message(index, uid, Configuration(json), read)
            }
            list
        }

        suspend fun getMessageIndex(uid: Int, read: Boolean = false) = SQLManager.asyncDeferred {
            val ps = this.prepareStatement("SELECT Index FROM Message WHERE UID = ? AND Read = ?")
            ps.setInt(1, uid)
            ps.setBoolean(2, read)
            val rs = ps.executeQuery()
            val list = mutableListOf<Int>()
            while (rs.next()) {
                list += rs.getInt(1)
            }
            list
        }

        @JvmStatic
        fun checkTable() {
            SQLManager.operateConnection {
                val stn = this.createStatement()
                stn.execute("""
                    CREATE TABLE IF NOT EXISTS Message(
                        Index INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                        UID INT NOT NULL,
                        Data JSON NOT NULL,
                        Read BOOLEAN DEFAULT FALSE,
                        FOREIGN KEY (UID) REFERENCES User(UID)
                    ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4
                """.trimIndent())
                stn.close()
            }
        }
    }
}