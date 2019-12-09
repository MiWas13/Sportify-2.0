package com.example.user.sportify.network.session;

import android.content.Context;

import com.example.user.sportify.annotations.SessionScope;

import dagger.Module;
import dagger.Provides;

//Реализован Singleton с помощью @SessionScope
@Module(includes = ContextModule.class)
public class SessionModule {

    @SessionScope
    @Provides
    public AuthSessionManager authSessionManager(SharedPreferencesSessionStorage sharedPreferencesSessionStorage, SessionData sessionData) {
        return new AuthSessionManager(sharedPreferencesSessionStorage, sessionData);
    }

    @SessionScope
    @Provides
    public SharedPreferencesSessionStorage sharedPreferencesSessionStorage(Context context) {
        return new SharedPreferencesSessionStorage(context);
    }

    @SessionScope
    @Provides
    public SessionData sessionData() {
        return new SessionData();
    }


}
