package ru.krivonosovdenis.fintechapp.parserfunctions

import org.json.JSONException
import org.json.JSONObject
import ru.krivonosovdenis.fintechapp.dataclasses.GroupData
import ru.krivonosovdenis.fintechapp.dataclasses.PostData

fun parsePostsResponse(data: String?): ArrayList<PostData> {
    val resultList = ArrayList<PostData>()
    if (data == null) {
        return resultList
    }
    return try {
        val dataJSON = JSONObject(data)
        val postsRawJSONArray = dataJSON.getJSONArray("posts")
        for (i in 0 until postsRawJSONArray.length()) {
            val postObject = postsRawJSONArray.getJSONObject(i)
            val postItem = parsePostObject(postObject)
            resultList.add(postItem)
        }
        resultList
    } catch (e: JSONException) {
        ArrayList()
    }
}

fun parsePostObject(postObject: JSONObject): PostData {
    val sourceId = postObject.getInt("source_id")
    val postId = postObject.getInt("post_id")
    //Апишка вк (ну как минимум ее файловая хардкод версия) отдает время в секундах
    //joda time корректно работает с милисекундами. Для корректной работы преобразуем
    //время в миллисекунды
    val date = postObject.getLong("date") * 1000
    val text = postObject.getString("text")
    val photo = try {
        postObject.getJSONArray("attachments").getJSONObject(0)?.getJSONObject("photo")
            ?.getJSONArray("sizes")?.getString(0)
    } catch (e: JSONException) {
        null
    }
    return PostData(sourceId, postId, date, text, photo)
}

fun parseGroupsResponse(data: String?): ArrayList<GroupData> {
    val resultList = ArrayList<GroupData>()
    if (data == null) {
        return resultList
    }
    return try {
        val dataJSON = JSONObject(data)
        val groupsRawJSONArray = dataJSON.getJSONArray("groups")
        for (i in 0 until groupsRawJSONArray.length()) {
            val groupObject = groupsRawJSONArray.getJSONObject(i)
            val groupItem = parseGroupObject(groupObject)
            resultList.add(groupItem)
        }
        resultList
    } catch (e: JSONException) {
        ArrayList()
    }
}

fun parseGroupObject(groupObject: JSONObject): GroupData {
    val groupId = groupObject.getInt("id")
    val groupName = groupObject.getString("name")
    val photo = groupObject.getString("photo100")
    return GroupData(groupId, groupName, photo)
}
