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
/**
 * @author StridAware, Inc
 * 
 * @Support dev@StridAware.com
 * @Sales: sales@StridAware.com
 * 
 * @see http://www.StridAware.com/
 */

public class StrideAwareService implements BluetoothService
{
	private final HashMap<UUID, BluetoothGattCharacteristic> characteristics = new HashMap();
 
	private final HashMap<UUID, WriteCallback> writeCallbacks = new HashMap();
	
	static boolean mCurrentIsStridAwareNewBeacon = false;//�Ƿ���StridAware�°�Ĺ̼�
 
	public void processGattServices(List<BluetoothGattService> services)
	{
		for (BluetoothGattService service : services)
			if (StrideAwareUuid.StridAware_BEACON_SERVICE.equals(service.getUuid())) {
				
				if (service.getCharacteristic(StrideAwareUuid.BEACON_KEEP_CONNECT_CHAR) != null)
				{
					this.characteristics.put(StrideAwareUuid.BEACON_KEEP_CONNECT_CHAR, service.getCharacteristic(StrideAwareUuid.BEACON_KEEP_CONNECT_CHAR));	
				}
				
				if (service.getCharacteristic(StrideAwareUuid.BEACON_UUID_CHAR) != null)
				{
					this.characteristics.put(StrideAwareUuid.BEACON_UUID_CHAR, service.getCharacteristic(StrideAwareUuid.BEACON_UUID_CHAR));	
				}
				
				if (service.getCharacteristic(StrideAwareUuid.MAJOR_CHAR) != null)
				{
					this.characteristics.put(StrideAwareUuid.MAJOR_CHAR, service.getCharacteristic(StrideAwareUuid.MAJOR_CHAR));	
				}
				
				if (service.getCharacteristic(StrideAwareUuid.MINOR_CHAR) != null)
				{
					this.characteristics.put(StrideAwareUuid.MINOR_CHAR, service.getCharacteristic(StrideAwareUuid.MINOR_CHAR));	
				}
				
				if (service.getCharacteristic(StrideAwareUuid.POWER_CHAR) != null)
				{
					this.characteristics.put(StrideAwareUuid.POWER_CHAR, service.getCharacteristic(StrideAwareUuid.POWER_CHAR));	
				}
				
				if (service.getCharacteristic(StrideAwareUuid.BEACON_FFF6) != null)
				{
					this.characteristics.put(StrideAwareUuid.BEACON_FFF6, service.getCharacteristic(StrideAwareUuid.BEACON_FFF6));	
				}
				
				if (service.getCharacteristic(StrideAwareUuid.BEACON_FFF7) != null)
				{
					this.characteristics.put(StrideAwareUuid.BEACON_FFF7, service.getCharacteristic(StrideAwareUuid.BEACON_FFF7));	
				}

				if (service.getCharacteristic(StrideAwareUuid.BEACON_FFF8) != null)
				{
					this.characteristics.put(StrideAwareUuid.BEACON_FFF8, service.getCharacteristic(StrideAwareUuid.BEACON_FFF8));	
				}
			}
	}
 
	public boolean hasCharacteristic(UUID uuid)
	{
		return this.characteristics.containsKey(uuid);
	}
	
	public String getBeaconUUID()
	{
		return this.characteristics.containsKey(StrideAwareUuid.BEACON_UUID_CHAR) ?
				getStringValue(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_UUID_CHAR)).getValue()) : null;
	}
	
	public int getBeaconMajor()
	{
		return this.characteristics.containsKey(StrideAwareUuid.MAJOR_CHAR) ?
				getUnsignedInt16(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.MAJOR_CHAR)).getValue()) : null;		
	}
	
	public int getBeaconMinor()
	{
		return this.characteristics.containsKey(StrideAwareUuid.MINOR_CHAR) ?
				getUnsignedInt16(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.MINOR_CHAR)).getValue()) : null;		
	}
	
	public int getBeaconPower()
	{
		return this.characteristics.containsKey(StrideAwareUuid.POWER_CHAR) ?
				getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.POWER_CHAR)).getValue()) : null;		
	}
	
	public int getBeaconMfgr()
	{
		if (mCurrentIsStridAwareNewBeacon)
		{
			return this.characteristics.containsKey(StrideAwareUuid.BEACON_FFF7) ?
				getUnsignedInt16(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_FFF7)).getValue()) : null;		
		}
		return 0;
	}	
	
	public int getBeaconBroadcastInterval()
	{
		if (mCurrentIsStridAwareNewBeacon)
		{
			return this.characteristics.containsKey(StrideAwareUuid.BEACON_FFF6) ?
				getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_FFF6)).getValue()) : null;		
		}
		else
		{
			return this.characteristics.containsKey(StrideAwareUuid.BEACON_FFF7) ?
					getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_FFF7)).getValue()) : null;			
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
 
	public Collection<BluetoothGattCharacteristic> getAvailableCharacteristics() {
		List chars = new ArrayList(this.characteristics.values());
		chars.removeAll(Collections.singleton(null));
		return chars;
	}
	
	public BluetoothGattCharacteristic getAvailableCharacteristic(UUID uuid) {
		return (BluetoothGattCharacteristic) this.characteristics.get(uuid);
	}
	
	
	public BluetoothGattCharacteristic getKeepUUIDChar() {
		return (BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_KEEP_CONNECT_CHAR);
	}
 
	public BluetoothGattCharacteristic beforeCharacteristicWrite(UUID uuid, WriteCallback callback) {
//		if (this.writeCallbacks.containsKey(uuid))
//		{
//			this.writeCallbacks.remove(uuid);
//		}
		this.writeCallbacks.put(uuid, callback);
		return (BluetoothGattCharacteristic)this.characteristics.get(uuid);
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
		else if (StrideAwareUuid.BEACON_KEEP_CONNECT_CHAR.equals(characteristic.getUuid()))
		{
			WriteCallback writeCallback = (WriteCallback)this.writeCallbacks.remove(characteristic.getUuid());
			if (status == 0)
				writeCallback.onSuccess();
			else
				writeCallback.onError();			
		}

	}
 
	private static String getStringValue(byte[] bytes) {
//		int indexOfFirstZeroByte = 0;
//		while (bytes[indexOfFirstZeroByte] != 0) {
//			indexOfFirstZeroByte++;
//		}

//		byte[] strBytes = new byte[indexOfFirstZeroByte];
//		String StrTemp = "";
//		for (int i = 0; i != indexOfFirstZeroByte; i++) {
//			strBytes[i] = bytes[i];
//		}
//		Log.i("TEST", "VALUE:"+strBytes);
//		
//		return new String(strBytes);
		
		String stmp="";  
        StringBuilder sb = new StringBuilder("");  
        for (int n = 0; n < bytes.length; n++)  
        {  
            stmp = Integer.toHexString(bytes[n] & 0xFF);  
            sb.append((stmp.length()==1)? "0"+stmp : stmp);  
            sb.append(" ");  
        }  
        return sb.toString().toUpperCase().trim();
	}
	
	private static int getUnsignedByte(byte[] bytes) {
		return unsignedByteToInt(bytes[0]);
	}
	
	private static int unsignedByteToInt(byte value)
	{
		return value & 0xFF;
	}
	
	private static int getUnsignedInt16(byte[] bytes) {
		return unsignedByteToInt(bytes[1]) + (unsignedByteToInt(bytes[0]) << 8);
	}
}
