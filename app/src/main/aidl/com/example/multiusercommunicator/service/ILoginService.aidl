// ILoginService.aidl
package com.example.multiusercommunicator.service;

import com.example.multiusercommunicator.service.ILoginServiceCallback;

interface ILoginService {
    /**
     * Logs in to the service.
     * @return Whether the login was successful
     */
    boolean login(String username, String password);

    /**
     * Logs out of the service.
     * @return Whether the logout was successful
     */
    boolean logout();

    void addListenerOrRemoveIfNull(ILoginServiceCallback callback);
}