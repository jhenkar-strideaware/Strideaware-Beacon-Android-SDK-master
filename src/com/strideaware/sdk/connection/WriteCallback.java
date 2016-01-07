package com.strideaware.sdk.connection;

/**
 * Callback used to indicate status of updating beacon characteristic.
 *
 */
public abstract interface WriteCallback
{
	public abstract void onSuccess();

	public abstract void onError();
}