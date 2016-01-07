package com.strideaware.sdk.internal;

import java.util.Arrays;
/**
 * @author JAALEE, Inc
 * 
 * @Support dev@jaalee.com
 * @Sales: sales@jaalee.com
 * 
 * @see http://www.jaalee.com/
 */

public final class Objects
{
	public static boolean equal(Object a, Object b)
	{
		return (a == b) || ((a != null) && (a.equals(b)));
	}

	public static int hashCode(Object[] objects)
	{
		return Arrays.hashCode(objects);
	}

	public static ToStringHelper toStringHelper(Object self)
	{
		return new ToStringHelper(simpleName(self.getClass()));
	}

	public static ToStringHelper toStringHelper(Class<?> clazz)
	{
		return new ToStringHelper(simpleName(clazz));
	}

	public static ToStringHelper toStringHelper(String className)
	{
		return new ToStringHelper(className);
	}

	private static String simpleName(Class<?> clazz)
	{
		String name = clazz.getName();

		name = name.replaceAll("\\$[0-9]+", "\\$");

		int start = name.lastIndexOf('$');

		if (start == -1) {
			start = name.lastIndexOf('.');
		}
		return name.substring(start + 1);
	}

	public static <T> T firstNonNull(T first, T second)
	{
		return first != null ? first : Preconditions.checkNotNull(second);
	}

	public static final class ToStringHelper
	{
		private final String className;
		private ValueHolder holderHead = new ValueHolder();
		private ValueHolder holderTail = this.holderHead;
		private boolean omitNullValues = false;

		private ToStringHelper(String className)
		{
			this.className = ((String)Preconditions.checkNotNull(className));
		}

		public ToStringHelper omitNullValues()
		{
			this.omitNullValues = true;
			return this;
		}

		public ToStringHelper add(String name, Object value)
		{
			return addHolder(name, value);
		}

		public ToStringHelper add(String name, boolean value)
		{
			return addHolder(name, String.valueOf(value));
		}

		public ToStringHelper add(String name, char value)
		{
			return addHolder(name, String.valueOf(value));
		}

		public ToStringHelper add(String name, double value)
		{
			return addHolder(name, String.valueOf(value));
		}

		public ToStringHelper add(String name, float value)
		{
			return addHolder(name, String.valueOf(value));
		}

		public ToStringHelper add(String name, int value)
		{
			return addHolder(name, String.valueOf(value));
		}

		public ToStringHelper add(String name, long value)
		{
			return addHolder(name, String.valueOf(value));
		}

		public ToStringHelper addValue(Object value)
		{
			return addHolder(value);
		}

		public ToStringHelper addValue(boolean value)
		{
			return addHolder(String.valueOf(value));
		}

		public ToStringHelper addValue(char value)
		{
			return addHolder(String.valueOf(value));
		}

		public ToStringHelper addValue(double value)
		{
			return addHolder(String.valueOf(value));
		}

		public ToStringHelper addValue(float value)
		{
			return addHolder(String.valueOf(value));
		}

		public ToStringHelper addValue(int value)
		{
	       return addHolder(String.valueOf(value));
		}

		public ToStringHelper addValue(long value)
		{
			return addHolder(String.valueOf(value));
		}

		public String toString()
		{
			boolean omitNullValuesSnapshot = this.omitNullValues;
			String nextSeparator = "";
			StringBuilder builder = new StringBuilder(32).append(this.className).append('{');

			for (ValueHolder valueHolder = this.holderHead.next; valueHolder != null; 
					valueHolder = valueHolder.next) {
				if ((!omitNullValuesSnapshot) || (valueHolder.value != null)) {
					builder.append(nextSeparator);
					nextSeparator = ", ";

					if (valueHolder.name != null) {
						builder.append(valueHolder.name).append('=');
					}
					builder.append(valueHolder.value);
				}
			}
			return builder.append('}').toString();
		}

		private ValueHolder addHolder() {
			ValueHolder valueHolder = new ValueHolder();
			this.holderTail = (this.holderTail.next = valueHolder);
			return valueHolder;
		}

		private ToStringHelper addHolder(Object value) {
			ValueHolder valueHolder = addHolder();
			valueHolder.value = value;
			return this;
		}

		private ToStringHelper addHolder(String name, Object value) {
			ValueHolder valueHolder = addHolder();
			valueHolder.value = value;
			valueHolder.name = ((String)Preconditions.checkNotNull(name));
			return this;
		}

		private static final class ValueHolder
		{
			String name;
			Object value;
			ValueHolder next;
		}
	}
}
