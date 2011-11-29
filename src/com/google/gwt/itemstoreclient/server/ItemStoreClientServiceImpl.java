package com.google.gwt.itemstoreclient.server;

import com.google.gwt.itemstoreclient.client.ItemStoreClientService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.itemstore.types.ItemStoreException;
import com.itemstore.types.ItemStoreUtil;

public class ItemStoreClientServiceImpl extends RemoteServiceServlet implements ItemStoreClientService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getBuckets(String user) {
		String xml = "";
		try {
			xml = ItemStoreUtil.getBuckets(user);
		} catch (ItemStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}
	
	@Override
	public Boolean postBucket(String bucket, String user) {
		try {
			ItemStoreUtil.createBucket(bucket, user);
		} catch (ItemStoreException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	@Override
	public Boolean deleteBucket(String bucket, String user) {
		try {
			ItemStoreUtil.deleteBucket(bucket, user);
		} catch (ItemStoreException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	@Override
	public String getItems(String bucket, String user) {
		String xml = "";
		try {
			xml = ItemStoreUtil.getItems(bucket, user);
		} catch (ItemStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}
}