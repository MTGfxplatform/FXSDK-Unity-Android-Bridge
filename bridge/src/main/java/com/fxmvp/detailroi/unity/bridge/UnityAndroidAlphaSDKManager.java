package com.fxmvp.detailroi.unity.bridge;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;


import com.fxmvp.detailroi.event.bean.IAPEventBean;
import com.fxmvp.detailroi.event.bean.IAPItemPair;
import com.fxmvp.detailroi.event.out.IAPPayStateEnum;
import com.fxmvp.detailroi.event.out.UserEventManager;
import com.fxmvp.detailroi.out.AlphaSDKFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnityAndroidAlphaSDKManager {
    private static String tag = "UnityAndroidAlphaSDKManager";

    public static void initAlphaSDK(Activity activity, String channel, String appId){

        if (activity != null) {
            AlphaSDKFactory.getAlphaSDK().init(activity.getApplication(), channel, appId);
        }
    }

    public static void changeDebugState(boolean state){
        AlphaSDKFactory.getAlphaSDK().enDebug(state);
    }

    public static void updateChannel(String channel){
        AlphaSDKFactory.getAlphaSDK().updateChannel(channel);
    }

    public static void exit() {
        AlphaSDKFactory.getAlphaSDK().exit();
    }

    public  void sendCustomEvent(String eventName, String eventInfoJsonObject){
        try {
            Log.e(tag,"sendCustomEvent: eventName:"+eventName+"-eventInfoJsonObject:"+eventInfoJsonObject);
            JSONObject EventInfo = new JSONObject(eventInfoJsonObject);
            UserEventManager userEventManager = (UserEventManager) AlphaSDKFactory.getAlphaSDK().getUserEventManager();
            userEventManager.track(eventName, EventInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<IAPItemPair> jsonArrayToList(JSONArray jsonArray){
        if(jsonArray == null || jsonArray.length() ==0){
            return null;
        }
        List<IAPItemPair> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            IAPItemPair iapItemPair = new IAPItemPair(jsonObject.optString("name"),jsonObject.optInt("count"));
            list.add(iapItemPair);
        }
        return list;
    }
    public void sendIapEvent(String iAPEventJsonObjectString){
        Log.e(tag,"sendIapEvent :iAPEventJsonObjectString:"+iAPEventJsonObjectString);
        if(!TextUtils.isEmpty(iAPEventJsonObjectString)){
            try {
                JSONObject jsonObject = new JSONObject(iAPEventJsonObjectString);
                UserEventManager userEventManager = (UserEventManager) AlphaSDKFactory.getAlphaSDK().getUserEventManager();
                IAPEventBean iapEventBean = new IAPEventBean();
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                iapEventBean.setProductItems(jsonArrayToList(jsonArray));
                iapEventBean.setTransactionId(jsonObject.optString("transaction_id"));

                String result = jsonObject.optString("paystatus");
                if(!TextUtils.isEmpty(result)){
                    IAPPayStateEnum stateEnum;
                    if(result.equals("1")){
                        stateEnum = IAPPayStateEnum.success;
                    }else if (result.equals("3")){
                        stateEnum = IAPPayStateEnum.restored;
                    }else{
                        stateEnum = IAPPayStateEnum.fail;
                    }
                    iapEventBean.setPayStatus(stateEnum);
                }

                iapEventBean.setCurrency(jsonObject.optString("currency"));
                iapEventBean.setAmount((float) jsonObject.optDouble("amount"));
                iapEventBean.setFailReason(jsonObject.optString("fail_reason"));
                Log.e(tag,"sendIapEvent :iapEventBean :"+iapEventBean.jsonObject.toString());
                userEventManager.sendIAPEvent(iapEventBean);
            } catch (Exception e) {
                Log.e(tag,"sendIapEvent :error :"+e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void sendIapEvent(List<IAPItemPair> list, String transactionId, IAPPayStateEnum iapPayStateEnum, String currency, float amount, String failReason){
        UserEventManager userEventManager = (UserEventManager) AlphaSDKFactory.getAlphaSDK().getUserEventManager();
        IAPEventBean iapEventBean = new IAPEventBean();
        iapEventBean.setProductItems(list);
        iapEventBean.setTransactionId(transactionId);
        iapEventBean.setPayStatus(iapPayStateEnum);
        iapEventBean.setCurrency(currency);
        iapEventBean.setAmount(amount);
        iapEventBean.setFailReason(failReason);
        userEventManager.sendIAPEvent(iapEventBean);
    }


}
