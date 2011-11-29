package com.google.gwt.itemstoreclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ItemStoreClientServiceAsync {

    void getBuckets(String user, AsyncCallback<String> callback);
    void postBucket(String bucket, String user, AsyncCallback<Boolean> callback);
    void deleteBucket(String bucket, String user, AsyncCallback<Boolean> callback);
    void getItems(String bucket, String user, AsyncCallback<String> callback); 
}
