package com.edu.hrbeu.helpsend.http;


import rx.Subscription;

public interface RequestImpl {
    void loadSuccess(Object object);

    void loadFailed();

    void addSubscription(Subscription subscription);
}
