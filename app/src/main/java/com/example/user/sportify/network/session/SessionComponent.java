package com.example.user.sportify.network.session;

import com.example.user.sportify.annotations.SessionScope;

import dagger.Component;

@SessionScope
@Component(modules = { SessionModule.class })
public interface SessionComponent {
	
	AuthSessionManager getAuthSessionManager();
}
