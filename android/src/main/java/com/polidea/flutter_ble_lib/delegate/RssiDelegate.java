package com.polidea.flutter_ble_lib.delegate;

import android.support.annotation.NonNull;
import android.util.Log;

import com.polidea.flutter_ble_lib.constant.ArgumentKey;
import com.polidea.flutter_ble_lib.constant.MethodName;
import com.polidea.flutter_ble_lib.converter.BleErrorJsonConverter;
import com.polidea.multiplatformbleadapter.BleAdapter;
import com.polidea.multiplatformbleadapter.Device;
import com.polidea.multiplatformbleadapter.OnErrorCallback;
import com.polidea.multiplatformbleadapter.OnSuccessCallback;
import com.polidea.multiplatformbleadapter.errors.BleError;
import com.polidea.multiplatformbleadapter.errors.BleErrorCode;

import java.util.Arrays;
import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class RssiDelegate extends CallDelegate {

    private static final String TAG = RssiDelegate.class.getName();
    private static List<String> supportedMethods =  Arrays.asList(MethodName.RSSI);

    private BleAdapter bleAdapter;
    private BleErrorJsonConverter bleErrorJsonConverter = new BleErrorJsonConverter();

    public RssiDelegate(BleAdapter bleAdapter) {
        super(supportedMethods);
        this.bleAdapter = bleAdapter;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        switch (methodCall.method) {
            case MethodName.RSSI:
                rssi(methodCall.<String>argument(ArgumentKey.DEVICE_IDENTIFIER),
                    methodCall.<String>argument(ArgumentKey.TRANSACTION_ID),
                    result);
                return;
            default:
                throw new IllegalArgumentException(methodCall.method + " cannot be handled by this delegate");
        }
    }

    private void rssi(@NonNull String deviceIdentifier, String transactionId, @NonNull final MethodChannel.Result result) {
        Log.d(TAG, "Read rssi for device " + deviceIdentifier + " transactionId: " + transactionId);
        bleAdapter.readRSSIForDevice(deviceIdentifier, transactionId, new OnSuccessCallback<Device>() {
            @Override
            public void onSuccess(Device device) {
                result.success(device.getRssi());
            }
        }, new OnErrorCallback() {
            @Override
            public void onError(BleError error) {
                result.error(String.valueOf(error.errorCode.code), error.reason, bleErrorJsonConverter.toJson(error));
            }
        });
    }
}