package com.example.user.sportify.ui.mygames.base;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

class BaseMyGamesPresenter extends MvpBasePresenter<BaseMyGamesView> {
	
	void onViewCreated() {
		ifViewAttached(BaseMyGamesView::initToolbar);
		ifViewAttached(BaseMyGamesView::initViewPager);
	}
}
