package fun.asgc.neutrino.core.base;


import fun.asgc.neutrino.core.util.TypeUtil;

import java.io.Serializable;
import java.util.*;

/**
 * 用于统一参数配置
 * 1、有序性（适用于方法参数）
 * 2、key为String类型时，忽略大小写、忽略分隔符(_、-)，兼容启动命令传参 + 配置文件传参
 * 3、父级委托机制：如果存在父级，且当前实例查不到，则委托父级查找。（适用于配置优先级：启动参数 > 配置文件 > 默认值）
 * @author: aoshiguchen
 * @date: 2023/3/1
 */
public class Kv<K,V> implements Map<K,V> , Serializable, Cloneable {
    private LinkedHashMap<K, V> _m;
    private HashMap<K, K> _k;
    private Locale locale;
    private String SEPARATOR = "_|-";
    private Kv<K,V> parent;
    
    private Kv() {
        this(null, 16, null);
    }

    private Kv(Kv<K,V> parent) {
        this(parent, 16, null);
    }

    public static <K,V> Kv<K,V> of() {
        return new Kv<>();
    }

    public static <K,V> Kv<K,V> of(Kv<K,V> parent) {
        return new Kv(parent);
    }

    public Kv<K,V> set(K k, V v) {
        this.put(k, v);
        return this;
    }

    public Kv(Kv<K,V> parent, int initialCapacity, Locale locale) {
        this.parent = parent;
        this._m = new LinkedHashMap<K, V>(initialCapacity) {
            @Override
            public boolean containsKey(Object key) {
                return Kv.this.containsKey(key);
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean doRemove = Kv.this.removeEldestEntry(eldest);
                if (doRemove) {
                    if (eldest.getKey() instanceof  String) {
                        _k.remove(convertKey((String) eldest.getKey()));
                    } else {
                        _k.remove(eldest.getKey());
                    }
                }
                return doRemove;
            }
        };
        this._k = new HashMap<>(initialCapacity);
        this.locale = (locale != null ? locale : Locale.getDefault());
    }
    
    @Override
    public int size() {
        return this._m.size();
    }

    public int stackSize() {
        return this._m.size() + (null == parent ? 0 : parent.stackSize());
    }

    @Override
    public boolean isEmpty() {
        return this._m.isEmpty();
    }

    public boolean isStackEmpty() {
        boolean res = this._m.isEmpty();
        if (res && null != parent) {
            res = parent.isStackEmpty();
        }
        return res;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean convertKey = false;
        if (key instanceof String) {
            convertKey = this._k.containsKey(convertKey((String) key));
        } else {
            convertKey = this._k.containsKey(key);
        }
        return convertKey;
    }

    public boolean stackContainsKey(K k) {
        return this.containsKey(k) || (null != parent && parent.containsKey(k));
    }

    @Override
    public boolean containsValue(Object value) {
        return this._m.containsValue(value);
    }

    public boolean stackContainsValue(Object value) {
        return this.containsValue(value) || (null != parent && parent.containsValue(value));
    }

    @Override
    public V get(Object key) {
        K k = null;
        try {
            k = (K) key;
        } catch (Exception e) {
            return null;
        }
        if (k instanceof String) {
            k = this._k.get(convertKey((String) k));
        }
        if (null != k) {
            return this._m.get(k);
        }
        return null;
    }

    public V stackGet(K k) {
        V res = get(k);
        if (null == res && null != parent) {
            res = parent.stackGet(k);
        }
        return res;
    }

    public V stackGetOrDefault(K k, V defaultValue) {
        V res = this.stackGet(k);
        if (null == res) {
            res = defaultValue;
        }
        return res;
    }

    @Override
    public V put(K key, V value) {
        K k = key;
        if (null != k && k instanceof String) {
            k = (K) convertKey((String) key);
        }
        K oldKey = this._k.put(k, key);
        if (null != oldKey && !oldKey.equals(key)) {
            this._m.remove(oldKey);
        }
        return this._m.put(key, value);
    }

    @Override
    public V remove(Object key) {
        K k1 = null;
        try {
            k1 = (K) key;
        } catch (Exception e) {
            return null;
        }
        K k2 = null;
        if (k1 instanceof String) {
            k1 = (K) convertKey((String) k1);
            k2 = this._k.remove(k1);
        } else {
            k2 = this._k.remove(k1);
        }
        if (null != k2) {
            return this._m.remove(k2);
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m.isEmpty()) {
            return;
        }
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        this._k.clear();
        this._m.clear();
    }

    @Override
    public Set<K> keySet() {
        return this._m.keySet();
    }

    @Override
    public Collection<V> values() {
        return this._m.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this._m.entrySet();
    }

    public Locale getLocale() {
        return this.locale;
    }

    protected String convertKey(String key) {
        return key.toLowerCase(getLocale()).replaceAll(SEPARATOR, "");
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return false;
    }

    @Override
    protected Kv<K,V> clone() {
        Kv<K,V> res = new Kv<>();
        if (null != parent) {
            res.parent = parent.clone();
        }
        res._m = (LinkedHashMap<K, V>) _m.clone();
        res._k = (HashMap<K, K>) _k.clone();
        res.locale = locale;
        return res;
    }

    // ====== 为了方便取值操作，take开头的方法，全部基于栈式取值 =======
    public V take(K k) {
        return this.stackGet(k);
    }

    public V take(K k, V defaultValue) {
        return this.stackGetOrDefault(k, defaultValue);
    }

    public String takeStr(K k) {
        return TypeUtil.conversion(take(k), String.class);
    }

    public String takeStr(K k, String defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), String.class);
    }

    public Byte takeByte(K k) {
        return TypeUtil.conversion(take(k), Byte.class);
    }

    public Byte takeByte(K k, Byte defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Byte.class);
    }

    public Character takeChar(K k) {
        return TypeUtil.conversion(take(k), Character.class);
    }

    public Character takeChar(K k, Character defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Character.class);
    }

    public Boolean takeBool(K k) {
        return TypeUtil.conversion(take(k), Boolean.class);
    }

    public Boolean takeBool(K k, Boolean defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Boolean.class);
    }

    public Short takeShort(K k) {
        return TypeUtil.conversion(take(k), Short.class);
    }

    public Short takeShort(K k, Short defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Short.class);
    }

    public Integer takeInt(K k) {
        return TypeUtil.conversion(take(k), Integer.class);
    }

    public Integer takeInt(K k, Integer defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Integer.class);
    }

    public Long takeLong(K k) {
        return TypeUtil.conversion(take(k), Long.class);
    }

    public Long takeLong(K k, Long defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Long.class);
    }

    public Float takeFloat(K k) {
        return TypeUtil.conversion(take(k), Float.class);
    }

    public Float takeFloat(K k, Float defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Float.class);
    }

    public Double takeDouble(K k) {
        return TypeUtil.conversion(take(k), Double.class);
    }

    public Double takeDouble(K k, Double defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Double.class);
    }

    public Date takeDate(K k) {
        return TypeUtil.conversion(take(k), Date.class);
    }

    public Date takeDate(K k, Date defaultValue) {
        return TypeUtil.conversion(take(k, (V)defaultValue), Date.class);
    }
}
