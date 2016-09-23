package nodomain.freeyourgadget.gadgetbridge.service.devices.angel;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.angel.AngelService;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.battery.BatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.battery.BatteryInfoProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfo;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo.DeviceInfoProfile;

public class AngelSupport extends AbstractBTLEDeviceSupport {

    private static final Logger LOG = LoggerFactory.getLogger(AngelSupport.class);

    private final DeviceInfoProfile<AngelSupport> deviceInfoProfile;
    private final BatteryInfoProfile<AngelSupport> batteryInfoProfile;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(DeviceInfoProfile.ACTION_DEVICE_INFO)) {
                handleDeviceInfo((DeviceInfo) intent.getParcelableExtra(DeviceInfoProfile.EXTRA_DEVICE_INFO));
            } else if (s.equals(BatteryInfoProfile.ACTION_BATTERY_INFO)) {
                handleBatteryInfo((BatteryInfo) intent.getParcelableExtra(BatteryInfoProfile.EXTRA_BATTERY_INFO));
            }
        }
    };

    public AngelSupport() {
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(GattService.UUID_SERVICE_DEVICE_INFORMATION);
        addSupportedService(GattService.UUID_SERVICE_BATTERY_SERVICE);
        addSupportedService(GattService.UUID_SERVICE_HEART_RATE);
        addSupportedService(GattService.UUID_SERVICE_HEALTH_THERMOMETER);
        addSupportedService(AngelService.UUID_SERVICE_HEALTH_JOURNAL);

        deviceInfoProfile = new DeviceInfoProfile<>(this);
        batteryInfoProfile = new BatteryInfoProfile<>(this);
        addSupportedProfile(deviceInfoProfile);
        addSupportedProfile(batteryInfoProfile);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BatteryInfoProfile.ACTION_BATTERY_INFO);
        intentFilter.addAction(DeviceInfoProfile.ACTION_DEVICE_INFO);
        broadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void dispose() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        broadcastManager.unregisterReceiver(mReceiver);
        super.dispose();
    }

    @Override
    protected TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        deviceInfoProfile.requestDeviceInfo(builder);
        builder.notify(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT), true);
        builder.notify(getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_TEMPERATURE_MEASUREMENT), true);
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
        setupHealthJournal(builder);
        batteryInfoProfile.requestBatteryInfo(builder);
        return builder;
    }

    private void setupHealthJournal(TransactionBuilder builder) {
        builder.notify(getCharacteristic(AngelService.UUID_CHARACTERISTIC_HEALTH_JOURNAL_ENTRY), true);
        builder.notify(getCharacteristic(AngelService.UUID_CHARACTERISTIC_HEALTH_JOURNAL_CONTROL_POINT), true);

        byte[] command = new byte[2];
        command[0] = AngelService.OP_CODE_QUERY;
        command[1] = AngelService.OPERATOR_ALL_ENTRIES;
        builder.write(getCharacteristic(AngelService.UUID_CHARACTERISTIC_HEALTH_JOURNAL_CONTROL_POINT), command);
    }

    @Override
    public boolean useAutoConnect() {
        return true;
    }

    @Override
    public void pair() {
        connect();
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {

    }

    @Override
    public void onSetTime() {

    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {

    }

    @Override
    public void onSetCallState(CallSpec callSpec) {

    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {

    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {

    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {

    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {

    }

    @Override
    public void onInstallApp(Uri uri) {

    }

    @Override
    public void onAppInfoReq() {

    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {

    }

    @Override
    public void onAppDelete(UUID uuid) {

    }

    @Override
    public void onAppConfiguration(UUID appUuid, String config) {

    }

    @Override
    public void onAppReorder(UUID[] uuids) {

    }

    @Override
    public void onFetchActivityData() {

    }

    @Override
    public void onReboot() {

    }

    @Override
    public void onHeartRateTest() {

    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {

    }

    @Override
    public void onFindDevice(boolean start) {

    }

    @Override
    public void onSetConstantVibration(int integer) {

    }

    @Override
    public void onScreenshotReq() {

    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {

    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {

    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {

    }

    @Override
    public boolean onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }

        UUID characteristicUUID = characteristic.getUuid();
        if (GattCharacteristic.UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT.equals(characteristicUUID)) {
            handleHeartRate(characteristic.getValue());
            return true;
        } else if (GattCharacteristic.UUID_CHARACTERISTIC_TEMPERATURE_MEASUREMENT.equals(characteristicUUID)) {
            handleTemperature(characteristic);
            return true;
        }

        LOG.info("Unhandled characterstic changed: " + characteristicUUID);
        logMessageContent(characteristic.getValue());
        return false;
    }

    @Override
    public boolean onCharacteristicRead(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic, int status) {
        if (super.onCharacteristicRead(gatt, characteristic, status)) {
            return true;
        }

        UUID characteristicUUID = characteristic.getUuid();
        LOG.info("Unhandled characteristic read: " + characteristicUUID);
        logMessageContent(characteristic.getValue());
        return false;
    }

    private void logMessageContent(byte[] value) {
        LOG.debug("RECEIVED DATA WITH LENGTH: " + ((value != null) ? value.length : "(null)"));
        if (value != null) {
            for (byte b : value) {
                LOG.debug("DATA: " + String.format("0x%2x", b));
            }
        }
    }

    private void handleBatteryInfo(BatteryInfo info) {
        batteryCmd.level = (short) info.getPercentCharged();
        handleGBDeviceEvent(batteryCmd);
    }

    private void handleDeviceInfo(DeviceInfo info) {
        LOG.debug("Device info: " + info);
        versionCmd.hwVersion = info.getHardwareRevision();
        versionCmd.fwVersion = info.getFirmwareRevision();
        handleGBDeviceEvent(versionCmd);
    }

    private void handleHeartRate(byte[] value) {
        if (value.length >= 2 && value[0] == 0) {
            int hrValue = value[1];
            LOG.debug("heart rate: " + hrValue);

            Intent intent = new Intent(DeviceService.ACTION_HEARTRATE_MEASUREMENT)
                    .putExtra(DeviceService.EXTRA_HEART_RATE_VALUE, hrValue)
                    .putExtra(DeviceService.EXTRA_TIMESTAMP, System.currentTimeMillis());
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }

    private void handleTemperature(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getValue().length >= 5) {
            int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            boolean fahrenheit = ((flags & 0x1) == 0x1);
            float temperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            LOG.debug("temperature " + temperature + (fahrenheit ? "F" : "C"));
        }
    }
}
