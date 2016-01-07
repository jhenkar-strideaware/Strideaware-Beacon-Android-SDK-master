package com.strideaware.sdk.connection;
 
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


 public class BeaconNameService implements BluetoothService
 {
	 private final HashMap<UUID, BluetoothGattCharacteristic> characteristics = new HashMap<UUID, BluetoothGattCharacteristic>();
	 private final HashMap<UUID, WriteCallback> writeCallbacks = new HashMap();
	 static boolean mCurrentIsStridAwareNewBeacon = false;//�Ƿ���StridAware�°�Ĺ̼�
	 
	 public void processGattServices(List<BluetoothGattService> services)
	 {
		 for (BluetoothGattService service : services)
			 if (StrideAwareUuid.BEACON_NAME.equals(service.getUuid())) {
					if (service.getCharacteristic(StrideAwareUuid.BEACON_NAME_CHAR) != null)
					{
						this.characteristics.put(StrideAwareUuid.BEACON_NAME_CHAR, service.getCharacteristic(StrideAwareUuid.BEACON_NAME_CHAR));	
					}
					
					if (service.getCharacteristic(StrideAwareUuid.BEACON_NEW_NAME_CHAR) != null)
					{
						this.characteristics.put(StrideAwareUuid.BEACON_NEW_NAME_CHAR, service.getCharacteristic(StrideAwareUuid.BEACON_NEW_NAME_CHAR));	
					}
					
					
			 }
	 }
 
	public void update(BluetoothGattCharacteristic characteristic)
	{
		this.characteristics.put(characteristic.getUuid(), characteristic);
		if (!mCurrentIsStridAwareNewBeacon)
		{
			byte [] Value = characteristic.getValue();
			
			WriteCallback writeCallback = (WriteCallback)this.writeCallbacks.remove(characteristic.getUuid());
			if (writeCallback != null)
			{
				if (Value[0] == 1)
				{
					writeCallback.onSuccess();
				}
				else
				{
					writeCallback.onError();
				}
			}
		}
	}
	
	
	public BluetoothGattCharacteristic beforeCharacteristicWrite(UUID uuid, WriteCallback callback) {
		if (callback != null)
		{
			this.writeCallbacks.put(uuid, callback);
		}
		return (BluetoothGattCharacteristic)this.characteristics.get(uuid);
	}
		
	 public Collection<BluetoothGattCharacteristic> getAvailableCharacteristics() {
		 List chars = new ArrayList(this.characteristics.values());
		 chars.removeAll(Collections.singleton(null));
		 return chars;
	 } 
	 
	public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
		if (mCurrentIsStridAwareNewBeacon)
		{
			WriteCallback writeCallback = (WriteCallback)this.writeCallbacks.remove(characteristic.getUuid());
			if (status == 0)
				writeCallback.onSuccess();
			else
				writeCallback.onError();
		}
	}	 
	 
	 public String getBeaconName()
	 {
		 if (mCurrentIsStridAwareNewBeacon)
		 {
			 return this.characteristics.containsKey(StrideAwareUuid.BEACON_NEW_NAME_CHAR) ?
					 getStringValue(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_NEW_NAME_CHAR)).getValue()) : null;
		 }
		 return this.characteristics.containsKey(StrideAwareUuid.BEACON_NAME_CHAR) ?
				 getStringValue(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_NAME_CHAR)).getValue()) : null;		
	 }
	 
	 
	 
	 private static String getStringValue(byte[] bytes) {
		 int indexOfFirstZeroByte = 0;
		 while (bytes[indexOfFirstZeroByte] != 0) {
			 indexOfFirstZeroByte++;
		 }

		 byte[] strBytes = new byte[indexOfFirstZeroByte];
		 for (int i = 0; i != indexOfFirstZeroByte; i++) {
			 strBytes[i] = bytes[i];
		 }
			
		 return new String(strBytes);
	 }			 
 
//	 public boolean isAuthSeedCharacteristic(BluetoothGattCharacteristic characteristic) {
//		 return characteristic.getUuid().equals(StridAwareUuid.AUTH_SEED_CHAR);
//	 }
// 
//	 public boolean isAuthVectorCharacteristic(BluetoothGattCharacteristic characteristic) {
//		 return characteristic.getUuid().equals(StridAwareUuid.AUTH_VECTOR_CHAR);
//	 }
// 
//	 public BluetoothGattCharacteristic getAuthSeedCharacteristic() {
//		 return (BluetoothGattCharacteristic)this.characteristics.get(StridAwareUuid.AUTH_SEED_CHAR);
//	 }
// 
//	 public BluetoothGattCharacteristic getAuthVectorCharacteristic() {
//		 return (BluetoothGattCharacteristic)this.characteristics.get(StridAwareUuid.AUTH_VECTOR_CHAR);
//	 }
 }
