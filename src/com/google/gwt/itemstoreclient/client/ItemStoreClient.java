package com.google.gwt.itemstoreclient.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.XMLParser;
import java.util.Date;

public class ItemStoreClient implements EntryPoint {

	private static final int REFRESH_INTERVAL = 30000; // ms
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable bucketsFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newBucketTextBox = new TextBox();
	private Button addBucketButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private String USER_ID = "root";
	private ItemStoreClientServiceAsync itemstoreSvc = GWT.create(ItemStoreClientService.class);


	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		// Create table for bucket data.
		bucketsFlexTable.setText(0, 0, "Bucket");
		bucketsFlexTable.setText(0, 1, "Items");
		bucketsFlexTable.setText(0, 2, "Last Modified");
		bucketsFlexTable.setText(0, 3, "URI");
		bucketsFlexTable.setText(0, 4, "UserId");
		bucketsFlexTable.setText(0, 5, "Remove");



		// Add styles to elements in the bucket list table.
		bucketsFlexTable.setCellPadding(6);
		bucketsFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		bucketsFlexTable.addStyleName("watchList");
		bucketsFlexTable.getCellFormatter().addStyleName(0, 1, "watchListRemoveColumn");
		bucketsFlexTable.getCellFormatter().addStyleName(0, 5, "watchListRemoveColumn");

		// Assemble Add Stock panel.
		addPanel.add(newBucketTextBox);
		addPanel.add(addBucketButton);
		addPanel.addStyleName("addPanel");

		// Assemble Main panel.
		mainPanel.add(bucketsFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("bucketList").add(mainPanel);

		// Move cursor focus to the input box.
		newBucketTextBox.setFocus(true);

		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshBucketList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

		// Listen for mouse events on the Add button.
		addBucketButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addBucket();
			}
		});

		// Listen for keyboard events in the input box.
		newBucketTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					addBucket();
				}
			}
		});

		getUserId();

	}

	
	private void getUserId(){
		final DialogBox dialogBox = new DialogBox();
		VerticalPanel userIdPanel = new VerticalPanel();
		dialogBox.setWidget(userIdPanel);

		Label userIdLabel = new Label("User Id");
		final TextBox userIdTextBox = new TextBox();

		userIdTextBox.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER){
					USER_ID = userIdTextBox.getText().trim();

					dialogBox.hide();

					// Load the tweetsFlexTable
					refreshBucketList();
				}

			}
		});

		// Add a close button at the bottom of the dialog
		Button sumbmitButton = new Button("Submit", new ClickHandler() {
			public void onClick(ClickEvent event) {

				USER_ID = userIdTextBox.getText().trim();

				dialogBox.hide();

				// Load the tweetsFlexTable
				refreshBucketList();
			}
		});

		userIdPanel.add(userIdLabel);
		userIdPanel.add(userIdTextBox);
		userIdPanel.add(sumbmitButton);

		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.center();
		dialogBox.show();
		

	}
	
	/**
	 * Add stock to FlexTable. Executed when the user clicks the addBucketButton or
	 * presses enter in the newBucketTextBox.
	 */
	private void addBucket() {
		final String bucketnm = newBucketTextBox.getText().trim();
		newBucketTextBox.setFocus(true);

		// Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
		if (!bucketnm.matches("^[0-9a-zA-Z\\.]{1,32}$")) {
			Window.alert("'" + bucketnm + "' is not a valid bucket name.");
			newBucketTextBox.selectAll();
			return;
		}

		newBucketTextBox.setText("");

		// Add the bucket.
		createBucket(bucketnm, USER_ID);
	}

	/**
	 * Refresh Bucket List.
	 */
	private void refreshBucketList() {
		// Initialize the service proxy.
		if (itemstoreSvc == null) {
			itemstoreSvc = GWT.create(ItemStoreClientService.class);
		}

		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
			}

			public void onSuccess(String result) {
				parseBucketXML(result);
			}
		};

		// Make the call to the stock price service.
		itemstoreSvc.getBuckets(USER_ID, callback);
	}

	private void createBucket(final String bucket, String user) {
		// Initialize the service proxy.
		if (itemstoreSvc == null) {
			itemstoreSvc = GWT.create(ItemStoreClientService.class);
		}

		// Set up the callback object.
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
				Window.alert("Bucket '" + bucket + "' is not created");
				newBucketTextBox.setText(bucket);
				newBucketTextBox.selectAll();
				return;
			}

			public void onSuccess(Boolean result) {
				if(result){
					refreshBucketList();
					Window.alert("Bucket '" + bucket + "' is created.");
					return;
				} else {
					Window.alert("Bucket '" + bucket + "' is not created");
					newBucketTextBox.setText(bucket);
					newBucketTextBox.selectAll();
					return;
				}
			}
		};

		// Make the call to the stock price service.
		itemstoreSvc.postBucket(bucket, user, callback);
	}

	private void removeBucket(final String bucket, String user) {
		// Initialize the service proxy.
		if (itemstoreSvc == null) {
			itemstoreSvc = GWT.create(ItemStoreClientService.class);
		}

		// Set up the callback object.
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
				Window.alert("Bucket '" + bucket + "' couldn't be deleted");
				newBucketTextBox.setText(bucket);
				newBucketTextBox.selectAll();
				return;
			}

			public void onSuccess(Boolean result) {
				if(result){
					refreshBucketList();
					Window.alert("Bucket '" + bucket + "' is deleted.");
					return;
				} else {
					Window.alert("Bucket '" + bucket + "' couldn't be deleted");
					newBucketTextBox.setText(bucket);
					newBucketTextBox.selectAll();
					return;
				}
			}
		};

		// Make the call to the stock price service.
		itemstoreSvc.deleteBucket(bucket, user, callback);
	}



	/**
	 * Refresh Bucket List.
	 */
	private void refreshItemList(String bucket, String user) {
		// Initialize the service proxy.
		if (itemstoreSvc == null) {
			itemstoreSvc = GWT.create(ItemStoreClientService.class);
		}

		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
			}

			public void onSuccess(String result) {
				parseItemXML(result);
			}
		};

		// Make the call to the stock price service.
		itemstoreSvc.getItems(bucket, user, callback);
	}

	private void parseBucketXML(String messageXml) {
		Bucket[] bucketArr = null;
		try {
			// parse the XML document into a DOM
			Document messageDom = XMLParser.parse(messageXml);

			// find the sender's display name in an attribute of the <from> tag
			NodeList bucketNodes = messageDom.getElementsByTagName("buckets").item(0).getChildNodes();

			int n = bucketNodes.getLength();

			bucketArr = new Bucket[n];

			for(int i=0; i<n; i++){
				NodeList b = bucketNodes.item(i).getChildNodes();
				String bucketnm = b.item(0).getFirstChild().getNodeValue();
				String item_nos = b.item(1).getFirstChild().getNodeValue();
				String lastModified = b.item(2).getFirstChild().getNodeValue();
				String uri = b.item(4).getFirstChild().getNodeValue();
				String userid = b.item(5).getFirstChild().getNodeValue();
				bucketArr[i] = new Bucket(bucketnm, uri, item_nos, userid, lastModified);
			}
		} catch (DOMException e) {
			Window.alert("Could not parse XML document.");
		}

		updateBucketTable(bucketArr);
	}

	private void parseItemXML(String messageXml) {
		Item[] itemArr = null;
		try {
			// parse the XML document into a DOM
			Document messageDom = XMLParser.parse(messageXml);

			// find the sender's display name in an attribute of the <from> tag
			NodeList itemNodes = messageDom.getElementsByTagName("items").item(0).getChildNodes();

			int n = itemNodes.getLength();

			itemArr = new Item[n];

			for(int i=0; i<n; i++){
				NodeList iList = itemNodes.item(i).getChildNodes();
				String itemnm = iList.item(0).getFirstChild().getNodeValue();
				String lastModified = iList.item(1).getFirstChild().getNodeValue();
				String type = iList.item(2).getFirstChild().getNodeValue();
				String uri = iList.item(3).getFirstChild().getNodeValue();
				itemArr[i] = new Item(itemnm, type, uri, lastModified);
			}
		} catch (DOMException e) {
			Window.alert("Could not parse XML document.");
		}

		createDialogBox(itemArr);
	}


	private void createDialogBox(Item[] items){
		final DialogBox dialogBox = new DialogBox(true);
		VerticalPanel itemsPanel = new VerticalPanel();
		dialogBox.setWidget(itemsPanel);
		FlexTable itemsFlexTable = new FlexTable();
		// Create table for bucket data.
		itemsFlexTable.setText(0, 0, "Item");
		itemsFlexTable.setText(0, 1, "Last Modified");
		itemsFlexTable.setText(0, 2, "Type");
		itemsFlexTable.setText(0, 3, "URI");

		// Add styles to elements in the bucket list table.
		itemsFlexTable.setCellPadding(6);
		itemsFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		itemsFlexTable.addStyleName("watchList");

		itemsPanel.add(itemsFlexTable);

		updateItemTable(itemsFlexTable, items);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		itemsPanel.add(closeButton);

		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.center();
		dialogBox.show();
	}

	/**
	 * Update the Price and Change fields all the rows in the stock table.
	 *
	 * @param prices Stock data for all rows.
	 */
	private void updateBucketTable(Bucket[] bucketArr) {
		
		bucketsFlexTable.removeAllRows();
		bucketsFlexTable.setText(0, 0, "Bucket");
		bucketsFlexTable.setText(0, 1, "Items");
		bucketsFlexTable.setText(0, 2, "Last Modified");
		bucketsFlexTable.setText(0, 3, "URI");
		bucketsFlexTable.setText(0, 4, "UserId");
		bucketsFlexTable.setText(0, 5, "Remove");
		
		// Add styles to elements in the bucket list table.
		bucketsFlexTable.setCellPadding(6);
		bucketsFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		bucketsFlexTable.addStyleName("watchList");
		bucketsFlexTable.getCellFormatter().addStyleName(0, 1, "watchListRemoveColumn");
		bucketsFlexTable.getCellFormatter().addStyleName(0, 5, "watchListRemoveColumn");

		for (int i = 0; i < bucketArr.length; i++) {
			//updateTable(bucketArr[i]);

			final Bucket bucket = bucketArr[i];

			bucketsFlexTable.setText(i+1, 0, bucket.getBucketnm());

			bucketsFlexTable.getCellFormatter().addStyleName(i+1, 1, "watchListRemoveColumn");
			bucketsFlexTable.setText(i+1, 1, bucket.getItem_nos());
			// Add a button to view items in this bucket.
			Button viewItemsButton = new Button("View");
			viewItemsButton.addStyleDependentName("remove");
			viewItemsButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					refreshItemList(bucket.getBucketnm(), bucket.getUserid());
				}
			});
			bucketsFlexTable.setWidget(i+1, 1, viewItemsButton);


			bucketsFlexTable.setText(i+1, 2, bucket.getLastModified());

			bucketsFlexTable.setWidget(i+1, 3, new Anchor(bucket.getUri(),bucket.getUri(), "_blank"));

			bucketsFlexTable.setText(i+1, 4, bucket.getUserid());

			bucketsFlexTable.getCellFormatter().addStyleName(i+1, 5, "watchListRemoveColumn");

			// Add a button to remove this bucket.
			Button removeBucketButton = new Button("x");
			removeBucketButton.addStyleDependentName("remove");
			removeBucketButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					// remove the bucket.
					removeBucket(bucket.getBucketnm(), USER_ID);

					// Get the stock price.
					refreshBucketList();

				}
			});
			bucketsFlexTable.setWidget(i+1, 5, removeBucketButton);

		}

		// Display timestamp showing last refresh.
		lastUpdatedLabel.setText("Last update : "
				+ DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date()));

	}

	private void updateItemTable(FlexTable itemsFlexTable, Item[] itemArr) {
		for (int i = 0; i < itemArr.length; i++) {

			final Item item = itemArr[i];

			itemsFlexTable.setText(i+1, 0, item.getItemnm());

			itemsFlexTable.setText(i+1, 1, item.getLastModified());

			itemsFlexTable.setText(i+1, 2, item.getType());

			itemsFlexTable.setWidget(i+1, 3, new Anchor(item.getUri(), item.getUri(), "_blank"));

		}

		// Display timestamp showing last refresh.
		//lastUpdatedLabel.setText("Last update : "
		//		+ DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date()));

	}

	/**
	 * Update a single row in the stock table.
	 *
	 * @param price Stock data for a single row.
	 */
	//private void updateTable(Bucket bucket) {
	//itemsFlexTable.setText(row, 1, priceText);
	//}
}
