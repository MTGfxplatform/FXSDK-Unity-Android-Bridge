package com.mintegral.detailroi.unity.bridge;

import android.app.Application;

import com.mintegral.detailroi.event.bean.IAPEventBean;
import com.mintegral.detailroi.event.bean.IAPItemPair;
import com.mintegral.detailroi.event.out.IAPPayStateEnum;
import com.mintegral.detailroi.event.out.UserEventManager;
import com.mintegral.detailroi.out.AlphaSDKFactory;

import org.json.JSONObject;

import java.util.List;

public class UnityAndroidAlphaSDKManager {

    public static void initAlphaSDK(Application application, String channel, String appId){
        AlphaSDKFactory.getAlphaSDK().init(application, channel, appId);
    }

    public static void changeDebugState(boolean state){
        AlphaSDKFactory.getAlphaSDK().enDebug(state);
    }

    public static void updateChannel(String channel){
        AlphaSDKFactory.getAlphaSDK().updateChannel(channel);
    }

    public void exit() {
        AlphaSDKFactory.getAlphaSDK().exit();
    }

    public void sendCustomEvent(String eventName, JSONObject EventInfo){
        UserEventManager userEventManager = (UserEventManager) AlphaSDKFactory.getAlphaSDK().getUserEventManager();
        userEventManager.sendCustomEvent(eventName, EventInfo);
    }

    public void sendIapEvent(List<IAPItemPair> list,String transactionId, IAPPayStateEnum iapPayStateEnum,String currency,float amount,String failReason){
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
