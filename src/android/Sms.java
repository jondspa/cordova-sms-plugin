package com.cordova.plugins.sms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import java.util.ArrayList;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Sms extends CordovaPlugin {

	public final String ACTION_SEND_SMS = "send";

	public final String ACTION_HAS_PERMISSION = "has_permission";

	private static final String INTENT_FILTER_SMS_SENT = "SMS_SENT";

	private static final int SEND_SMS_REQ_CODE = 0;

	private CallbackContext callbackContext;

	private JSONArray args;

        BroadcastReceiver receiver;

     @Override
        public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

                if (action.equals(ACTION_SEND_SMS)) {
                        try {
                                String phoneNumber = args.getJSONArray(0).join(";").replace("\"", "");
                                String message = args.getString(1);
                                String imageFile = args.getString(2);
                                String method = args.getString(3);


                                if (!checkSupport()) {
                                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS not supported on this platform"));
                                        return true;
                                }

                                if ("".equals(imageFile)) {
                                        invokeSMSIntentNoImage(phoneNumber, message);
                                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                                } else if (method.equalsIgnoreCase("INTENT")) {
                                        invokeSMSIntent(phoneNumber, message, imageFile);
                                        // always passes success back to the app
                                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                                } else {
                                        // by creating this broadcast receiver we can check whether or not the SMS was sent
                                        if (receiver == null) {
                                                this.receiver = new BroadcastReceiver() {
                                                        @Override
                                                        public void onReceive(Context context, Intent intent) {
                                                                PluginResult pluginResult;

                                                                switch (getResultCode()) {
                                                                case SmsManager.STATUS_ON_ICC_SENT:
                                                                        pluginResult = new PluginResult(PluginResult.Status.OK);
                                                                        pluginResult.setKeepCallback(true);
                                                                        callbackContext.sendPluginResult(pluginResult);
                                                                        break;
                                                                case Activity.RESULT_OK:
                                                                        pluginResult = new PluginResult(PluginResult.Status.OK);
                                                                        pluginResult.setKeepCallback(true);
                                                                        callbackContext.sendPluginResult(pluginResult);
                                                                        break;
                                                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                                                        pluginResult = new PluginResult(PluginResult.Status.ERROR);
                                                                        pluginResult.setKeepCallback(true);
                                                                        callbackContext.sendPluginResult(pluginResult);
                                                                        break;
                                                                default:
                                                                        pluginResult = new PluginResult(PluginResult.Status.ERROR);
                                                                        pluginResult.setKeepCallback(true);
                                                                        callbackContext.sendPluginResult(pluginResult);
                                                                        break;
                                                                }
                                                        }
                                                };
                                                final IntentFilter intentFilter = new IntentFilter();
                                                intentFilter.addAction(INTENT_FILTER_SMS_SENT);
                                                cordova.getActivity().registerReceiver(this.receiver, intentFilter);
                                        }
                                        send(phoneNumber, message);
                                }
                                return true;
                        } catch (JSONException ex) {
                                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
                        }
                }
                return false;
        }

        private void invokeSMSIntentNoImage(String phoneNumber, String message) {
                Intent sendIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this.cordova.getActivity());

                        sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);

                        if (defaultSmsPackageName != null) {
                                sendIntent.setPackage(defaultSmsPackageName);
                        }
                } else {
                        sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", message);
                        // See http://stackoverflow.com/questions/7242190/sending-sms-using-intent-does-not-add-recipients-on-some-devices
                        sendIntent.putExtra("address", phoneNumber);
                        sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
                }
                this.cordova.getActivity().startActivity(sendIntent);
        }

        @SuppressLint("NewApi")
        private void invokeSMSIntent(String phoneNumber, String message, String imageFile) {
                Intent sendIntent;
                //if (!"".equals(phoneNumber)) {
                        sendIntent = new Intent(Intent.ACTION_SEND);
                        //sendIntent.putExtra("address",phoneNumber);
                        sendIntent.putExtra("sms_body", message);

                        String imageDataBytes = imageFile.substring(imageFile.indexOf(",")+1);

                        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        String saveFilePath = Environment.getExternalStorageDirectory() + "/HealthAngel";
                        File dir = new File(saveFilePath);

                        if(!dir.exists())
                                dir.mkdirs();

                        File file = new File(dir, "logo.png");

                        FileOutputStream fOut = null;

                        try {
                                fOut = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        decodedByte.compress(Bitmap.CompressFormat.PNG, 40, fOut);

                        try {
                                fOut.flush();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        try {
                                fOut.close();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(saveFilePath + "/logo.png")));
                        sendIntent.setType("image/*");
                        this.cordova.getActivity().startActivity(sendIntent);
                    //}                

        }



	private boolean hasPermission() {
		return cordova.hasPermission(android.Manifest.permission.SEND_SMS);
	}

	private void requestPermission() {
		cordova.requestPermission(this, SEND_SMS_REQ_CODE, android.Manifest.permission.SEND_SMS);
	}

	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		for (int r : grantResults) {
			if (r == PackageManager.PERMISSION_DENIED) {
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
				return;
			}
		}
		sendSMS();
	}

	private boolean sendSMS() {
		cordova.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					//parsing arguments
					String separator = ";";
					if (android.os.Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
						// See http://stackoverflow.com/questions/18974898/send-sms-through-intent-to-multiple-phone-numbers/18975676#18975676
						separator = ",";
					}
					String phoneNumber = args.getJSONArray(0).join(separator).replace("\"", "");
					String message = args.getString(1);
					String method = args.getString(2);
					boolean replaceLineBreaks = Boolean.parseBoolean(args.getString(3));

					// replacing \n by new line if the parameter replaceLineBreaks is set to true
					if (replaceLineBreaks) {
						message = message.replace("\\n", System.getProperty("line.separator"));
					}
					if (!checkSupport()) {
						callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS not supported on this platform"));
						return;
					}
					if (method.equalsIgnoreCase("INTENT")) {
						invokeSMSIntent(phoneNumber, message);
						// always passes success back to the app
						callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
					} else {
						send(phoneNumber, message);
					}
					return;
				} catch (JSONException ex) {
					callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
				}
			}
		});
		return true;
	}

	private boolean checkSupport() {
		Activity ctx = this.cordova.getActivity();
		return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	@SuppressLint("NewApi")
	private void invokeSMSIntent(String phoneNumber, String message) {
		Intent sendIntent;
		if ("".equals(phoneNumber) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this.cordova.getActivity());

			sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/plain");
			sendIntent.putExtra(Intent.EXTRA_TEXT, message);

			if (defaultSmsPackageName != null) {
				sendIntent.setPackage(defaultSmsPackageName);
			}
		} else {
			sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("sms_body", message);
			// See http://stackoverflow.com/questions/7242190/sending-sms-using-intent-does-not-add-recipients-on-some-devices
			sendIntent.putExtra("address", phoneNumber);
			sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
		}
		this.cordova.getActivity().startActivity(sendIntent);
	}

        private void send(String phoneNumber, String message) {
                SmsManager manager = SmsManager.getDefault();
                PendingIntent sentIntent = PendingIntent.getBroadcast(this.cordova.getActivity(), 0, new Intent(INTENT_FILTER_SMS_SENT), 0);

                // Use SendMultipartTextMessage if the message requires it
                int parts_size = manager.divideMessage(message).size();
                if (parts_size > 1) {
                        ArrayList<String> parts = manager.divideMessage(message);
                        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                        for (int i = 0; i < parts_size; ++i) {
                                sentIntents.add(sentIntent);
                        }
                        manager.sendMultipartTextMessage(phoneNumber, null, parts,
                                        sentIntents, null);
                } else {
                        manager.sendTextMessage(phoneNumber, null, message, sentIntent,
                                        null);
                }
        }

}
