/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fun.asgc.neutrino.core.cache;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import fun.asgc.neutrino.core.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class LocalCache implements Cache<String,Object>, Preservable {

	private JSONObject cache;
	private String path;

	public LocalCache(String path) {
		this.cache = new JSONObject();
		this.path = path;
		FileUtil.makeDirs(path);
		this.load();
	}

	@Override
	public void set(String k, Object v) {
		cache.put(k, v);
		this.save();
	}

	@Override
	public Object get(String k) {
		return cache.getJSONObject(k);
	}

	@Override
	public boolean containsKey(String k) {
		return cache.containsKey(k);
	}

	@Override
	public boolean containsValue(Object v) {
		return cache.containsValue(v);
	}

	@Override
	public Set<String> keySet() {
		return cache.keySet();
	}

	@Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	@Override
	public void clear() {
		cache.clear();
		save();
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public Object isOnePeek() {
		if (size() != 1) {
			return null;
		}
		return cache.values().stream().findFirst().get();
	}

	@Override
	public Collection<Object> values() {
		return cache.values();
	}

	@Override
	public synchronized void save() {
		FileUtil.write(path, JSONObject.toJSONString(cache));
	}

	@Override
	public synchronized void load() {
		String content = FileUtil.readContentAsString(path);
		if (StringUtils.isNotEmpty(content)) {
			cache = JSONObject.parseObject(content);
		}
	}

	public JSONObject getJSONObject(String key) {
		Object value = this.cache.get(key);
		if (value instanceof JSONObject) {
			return (JSONObject)value;
		} else if (value instanceof Map) {
			return new JSONObject((Map)value);
		} else {
			return value instanceof String ? JSON.parseObject((String)value) : (JSONObject)JSON.toJSON(value);
		}
	}

	public JSONArray getJSONArray(String key) {
		Object value = this.cache.get(key);
		if (value instanceof JSONArray) {
			return (JSONArray)value;
		} else if (value instanceof List) {
			return new JSONArray((List)value);
		} else {
			return value instanceof String ? (JSONArray)JSON.parse((String)value) : (JSONArray)JSON.toJSON(value);
		}
	}

	public <T> T getObject(String key, Class<T> clazz) {
		Object obj = this.cache.get(key);
		return TypeUtils.castToJavaBean(obj, clazz);
	}

	public <T> T getObject(String key, Type type) {
		Object obj = this.cache.get(key);
		return TypeUtils.cast(obj, type, ParserConfig.getGlobalInstance());
	}

	public <T> T getObject(String key, TypeReference typeReference) {
		Object obj = this.cache.get(key);
		return typeReference == null ? (T)obj : TypeUtils.cast(obj, typeReference.getType(), ParserConfig.getGlobalInstance());
	}

	public Boolean getBoolean(String key) {
		Object value = this.get(key);
		return value == null ? null : TypeUtils.castToBoolean(value);
	}

	public byte[] getBytes(String key) {
		Object value = this.get(key);
		return value == null ? null : TypeUtils.castToBytes(value);
	}

	public boolean getBooleanValue(String key) {
		Object value = this.get(key);
		Boolean booleanVal = TypeUtils.castToBoolean(value);
		return booleanVal == null ? false : booleanVal;
	}

	public Byte getByte(String key) {
		Object value = this.get(key);
		return TypeUtils.castToByte(value);
	}

	public byte getByteValue(String key) {
		Object value = this.get(key);
		Byte byteVal = TypeUtils.castToByte(value);
		return byteVal == null ? 0 : byteVal;
	}

	public Short getShort(String key) {
		Object value = this.get(key);
		return TypeUtils.castToShort(value);
	}

	public short getShortValue(String key) {
		Object value = this.get(key);
		Short shortVal = TypeUtils.castToShort(value);
		return shortVal == null ? 0 : shortVal;
	}

	public Integer getInteger(String key) {
		Object value = this.get(key);
		return TypeUtils.castToInt(value);
	}

	public int getIntValue(String key) {
		Object value = this.get(key);
		Integer intVal = TypeUtils.castToInt(value);
		return intVal == null ? 0 : intVal;
	}

	public Long getLong(String key) {
		Object value = this.get(key);
		return TypeUtils.castToLong(value);
	}

	public long getLongValue(String key) {
		Object value = this.get(key);
		Long longVal = TypeUtils.castToLong(value);
		return longVal == null ? 0L : longVal;
	}

	public Float getFloat(String key) {
		Object value = this.get(key);
		return TypeUtils.castToFloat(value);
	}

	public float getFloatValue(String key) {
		Object value = this.get(key);
		Float floatValue = TypeUtils.castToFloat(value);
		return floatValue == null ? 0.0F : floatValue;
	}

	public Double getDouble(String key) {
		Object value = this.get(key);
		return TypeUtils.castToDouble(value);
	}

	public double getDoubleValue(String key) {
		Object value = this.get(key);
		Double doubleValue = TypeUtils.castToDouble(value);
		return doubleValue == null ? 0.0D : doubleValue;
	}

	public BigDecimal getBigDecimal(String key) {
		Object value = this.get(key);
		return TypeUtils.castToBigDecimal(value);
	}

	public BigInteger getBigInteger(String key) {
		Object value = this.get(key);
		return TypeUtils.castToBigInteger(value);
	}

	public String getString(String key) {
		Object value = this.get(key);
		return value == null ? null : value.toString();
	}

	public Date getDate(String key) {
		Object value = this.get(key);
		return TypeUtils.castToDate(value);
	}

	public java.sql.Date getSqlDate(String key) {
		Object value = this.get(key);
		return (java.sql.Date) TypeUtils.castToSqlDate(value);
	}

	public Timestamp getTimestamp(String key) {
		Object value = this.get(key);
		return (Timestamp) TypeUtils.castToTimestamp(value);
	}
}
