package com.example.multiusercommunicator.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.UserHandle
import android.util.Log

private const val TAG = "LoginService"

class LoginService : Service() {
    private val loggedInUserToUserHandleMap = hashMapOf<String, UserHandle>()
    private val userToListenerMap = hashMapOf<UserHandle, ILoginServiceCallback>()

    private val binder = object : ILoginService.Stub() {
        private fun notifyListenersOfLoggedInUsersChange() {
            synchronized(loggedInUserToUserHandleMap) {
                val allLoggedInUsers: List<String> = loggedInUserToUserHandleMap.map { it.key }

                // beware of deadlock lol
                synchronized(userToListenerMap) {
                    userToListenerMap.forEach { (_, listener) ->
                        listener.onLoginChanged(allLoggedInUsers)
                    }
                }
            }
        }

        override fun login(username: String?, password: String?): Boolean {
            Log.d(TAG, "User ${Binder.getCallingUserHandle()} trying to login as $username")
            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                return false
            }

            val userHandle = Binder.getCallingUserHandle()
            synchronized(loggedInUserToUserHandleMap) {
                val loggedInUser = loggedInUserToUserHandleMap[username]
                if (loggedInUser != null) {
                    // already logged in as someone else
                    return false
                }
                loggedInUserToUserHandleMap[username] = userHandle
                notifyListenersOfLoggedInUsersChange()
                return true
            }
        }

        override fun logout(): Boolean {
            Log.d(TAG, "User ${Binder.getCallingUserHandle()} trying to logout")
            val userHandle = Binder.getCallingUserHandle()

            synchronized(loggedInUserToUserHandleMap) {
                val iterator = loggedInUserToUserHandleMap.iterator()
                while (iterator.hasNext()) {
                    val currentEntry = iterator.next()
                    if (currentEntry.value == userHandle) {
                        iterator.remove()
                        notifyListenersOfLoggedInUsersChange()
                        return true
                    }
                }
                return false
            }
        }

        override fun addListenerOrRemoveIfNull(callback: ILoginServiceCallback?) {
            val userHandle = Binder.getCallingUserHandle()

            synchronized(userToListenerMap) {
                if (callback == null) {
                    userToListenerMap.remove(userHandle)
                } else {
                    userToListenerMap[userHandle] = callback
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG, "onLowMemory()")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind(intent = $intent)")
        return super.onUnbind(intent)
    }
}