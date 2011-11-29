package com.google.gwt.itemstoreclient.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("itemstoreService")
public interface ItemStoreClientService extends RemoteService {

	String getBuckets(String user);
	Boolean postBucket(String bucket, String user);
	Boolean deleteBucket(String bucket, String user);
	String getItems(String bucket, String user) ;
}
