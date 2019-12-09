package com.example.user.sportify.network.session;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
	
	private Context context;
	
	public ContextModule(final Context context) {
		this.context = context;
	}
	
	@Provides
	public Context context() {
		return context.getApplicationContext();
	}
}
