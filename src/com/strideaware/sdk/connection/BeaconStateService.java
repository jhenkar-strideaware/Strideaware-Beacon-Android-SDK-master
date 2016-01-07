package com.strideaware.sdk.connection;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class BeaconStateService
  implements BluetoothService
{
	private final HashMap<UUID, BluetoothGattCharacteristic> characteristics = new HashMap();
	private final HashMap<UUID, WriteCallback> writeCallbacks = new HashMap();
	
	static boolean mCurrentIsStridAwareNewBeacon = false;//�Ƿ���StridAware�°�Ĺ̼�
	
	public void processGattServices(List<BluetoothGattService> services)
	{
		for (BluetoothGattService service : services)
			if (StrideAwareUuid.BEACON_STATE_SERVICE.equals(service.getUuid())) {
				if (service.getCharacteristic(StrideAwareUuid.BEACON_STATE_CHAR) != null)
				{
					this.characteristics.put(StrideAwareUuid.BEACON_STATE_CHAR, service.getCharacteristic(StrideAwareUuid.BEACON_STATE_CHAR));	
				}	
			}
	}

	public boolean hasCharacteristic(UUID uuid)
	{
		return this.characteristics.containsKey(uuid);
	}
	
	public void update(BluetoothGattCharacteristic characteristic)
	{
		this.characteristics.put(characteristic.getUuid(), characteristic);
	}
	
	public BluetoothGattCharacteristic beforeCharacteristicWrite(UUID uuid, WriteCallback callback) {
		if (callback != null)
		{
			this.writeCallbacks.put(uuid, callback);
		}
		return (BluetoothGattCharacteristic)this.characteristics.get(uuid);
	}
 
	public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
		WriteCallback writeCallback = (WriteCallback)this.writeCallbacks.remove(characteristic.getUuid());
		if (status == 0)
			writeCallback.onSuccess();
		else
			writeCallback.onError();
	}

	public Collection<BluetoothGattCharacteristic> getAvailableCharacteristics() {
		List chars = new ArrayList(this.characteristics.values());
		chars.removeAll(Collections.singleton(null));
		return chars;
	}

	public Integer getBeaconState() {
		
		if (mCurrentIsStridAwareNewBeacon)
		{
				return this.characteristics.containsKey(StrideAwareUuid.BEACON_STATE_CHAR) ? Integer.valueOf(getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(StrideAwareUuid.BEACON_STATE_CHAR)).getValue())) : null;	
		}
		return 1;		
	}
	
	private static int getUnsignedByte(byte[] bytes) {
		return unsignedByteToInt(bytes[0]);
	}
	private static int unsignedByteToInt(byte value)
	{
		return value & 0xFF;
	}
}
