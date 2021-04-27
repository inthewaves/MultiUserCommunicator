// ILoginServiceCallback.aidl
package com.example.multiusercommunicator.service;

interface ILoginServiceCallback {
    oneway void onLoginChanged(in List<String> newLoggedInUsers);
}