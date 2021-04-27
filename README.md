# MultiUserCommunicator
An app that uses a service to manage the state for the app across multiple users.

This requires the `INTERACT_ACROSS_USERS (signature|system)` permission. (TODO: Add a Android.bp
file)

Some background:
[Building Multiuser-Aware Apps](https://source.android.com/devices/tech/admin/multiuser-apps) (the
[Services in multiple users or profiles](https://source.android.com/devices/tech/admin/multiuser-apps#work-profiles)
section might not be needed, since we can use the `singleUser` attribute in the manifest.
