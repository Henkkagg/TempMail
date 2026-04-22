package domain

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Email
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SubscriptionService(private val databaseRepository: DatabaseRepository) {

    private data class LatestIdAndOnNotification(
        var subscriptionId: String,
        var latestId: Int,
        val onNotification: suspend (Email) -> Unit
    )

    private val subscriptions = ConcurrentHashMap<String, LatestIdAndOnNotification>()

    init {
        //Note, runs FOREVER because the scope isn't tied to any lifecycle.
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val notifications = databaseRepository.getNotifications()
                notifications.forEach { notification ->
                    val json = Gson().fromJson(notification.parameter, JsonObject::class.java)
                    val address = json["recipient"].asString

                    if (subscriptions[address] == null) return@forEach

                    //If better performance is needed, optimize by fetching only the new emails from database
                    val emails = databaseRepository.getEmailsToAddress(address)
                    emails.forEach { email ->
                        if (email.id <= subscriptions[address]!!.latestId) return@forEach
                        subscriptions[address]!!.onNotification(email)
                        if (email.id > subscriptions[address]!!.latestId) {
                            subscriptions[address]!!.latestId = email.id
                        }
                    }
                }
            }
        }
    }

    suspend fun subscribe(address: String, onNotification: suspend (Email?) -> Unit): String {

        val subscriptionId = UUID.randomUUID().toString()

        //Subscribing to notifications first to avoid a race condition which would miss an email
        subscriptions[address] = LatestIdAndOnNotification(subscriptionId, 0, onNotification)
        val preExistingEmails = databaseRepository.getEmailsToAddress(address)
        preExistingEmails.forEach { email ->
            onNotification(email)
            if (email.id > subscriptions[address]!!.latestId) {
                subscriptions[address]!!.latestId = email.id
            }
        }

        //Empty notification lets client know now pre-existing mails -> load page fully and show intro message
        if (preExistingEmails.isEmpty()) onNotification(null)

        return subscriptionId
    }

    fun unsubscribe(address: String, subscriptionId: String) {

        if (subscriptions[address]!!.subscriptionId == subscriptionId) {
            subscriptions.remove(address)
        }
    }
}