package fun.asgc.neutrino.core.base;


import fun.asgc.neutrino.core.util.TypeUtil;

import java.io.Serializable;
import java.util.*;

/**
 * 用于统一参数配置 <br/>
 * <p>
 * 1、有序性（适用于方法参数）<br/>
 * 2、key为String类型时，忽略大小写、忽略分隔符(_、-)，兼容启动命令传参 + 配置文件传参 <br/>
 * 3、父级委托机制：如果存在父级，且当前实例查不到，则委托父级查找。（适用于配置优先级：启动参数 > 配置文件 > 默认值） <br/>
 * </p>
 *
 * 使用说明：<br/>
 * 1、take开头的方法，适用于配置层。利用parent委托机制，做优先级。 如5级配置：默认值 -> 内部配置文件 -> 环境变量 -> 外部配置文件 -> 启动参数 <br/>
 * 2、inx开头的方法，适用于不关心参数名称，根据下标取值。如普通方法参数、sql顺序参数（非具名参数） <br/>
 * 3、get方法取值，没有委托机制，适用于sql具名参数、bean方法具名参数 <br/>
 * @author: aoshiguchen
 * @date: 2023/3/1
 */
public class Kv<K,V> implements Map<K,V> , Serializable, Cloneable {
    private HashMap<K,K> aliasMap;
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

    public Kv<K,V> setAlias(K k, K alias) {
        this.aliasMap.put(convertKey(alias), k);
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
        this.aliasMap = new HashMap<>(initialCapacity);
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

    protected K convertKey(Object k) {
        K res = null;
        try {
            res = (K) convertKey((String) k);
        } catch (Exception e) {
            res = (K)k;
        }
        return res;
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
        res.aliasMap = (HashMap<K, K>) aliasMap.clone();
        res.locale = locale;
        return res;
    }

    // ====== 为了方便取值操作，get方法保持Map接口原有语义，不支持栈式取值 =======
    @Override
    public V get(Object key) {
        K k = this.aliasMap.get(convertKey(key));
        try {
            if (null == k) {
                k = (K) key;
            }
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

    public V get(Object key, V defaultValue) {
        V res = get(key);
        if (null == res) {
            res = defaultValue;
        }
        return res;
    }

    public String getStr(K k) {
        return TypeUtil.conversion(get(k), String.class);
    }

    public String getStr(K k, String defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), String.class);
    }

    public Byte getByte(K k) {
        return TypeUtil.conversion(get(k), Byte.class);
    }

    public Byte getByte(K k, Byte defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Byte.class);
    }

    public Character getChar(K k) {
        return TypeUtil.conversion(get(k), Character.class);
    }

    public Character getChar(K k, Character defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Character.class);
    }

    public Boolean getBool(K k) {
        return TypeUtil.conversion(get(k), Boolean.class);
    }

    public Boolean getBool(K k, Boolean defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Boolean.class);
    }

    public Short getShort(K k) {
        return TypeUtil.conversion(get(k), Short.class);
    }

    public Short getShort(K k, Short defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Short.class);
    }

    public Integer getInt(K k) {
        return TypeUtil.conversion(get(k), Integer.class);
    }

    public Integer getInt(K k, Integer defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Integer.class);
    }

    public Long getLong(K k) {
        return TypeUtil.conversion(get(k), Long.class);
    }

    public Long getLong(K k, Long defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Long.class);
    }

    public Float getFloat(K k) {
        return TypeUtil.conversion(get(k), Float.class);
    }

    public Float getFloat(K k, Float defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Float.class);
    }

    public Double getDouble(K k) {
        return TypeUtil.conversion(get(k), Double.class);
    }

    public Double getDouble(K k, Double defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Double.class);
    }

    public Date getDate(K k) {
        return TypeUtil.conversion(get(k), Date.class);
    }

    public Date getDate(K k, Date defaultValue) {
        return TypeUtil.conversion(get(k, (V)defaultValue), Date.class);
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

    // ====== 为了方便取值操作，idx开头的方法，全部基于下标取值 =======
    public V idx(int i) {
        if (i >= 0 && i < this.size()) {
            return (V)this._m.values().toArray(new Object[]{})[i];
        }
        return null;
    }

    public V idx(int i, V defaultValue) {
        V res = idx(i);
        if (null == res) {
            res = defaultValue;
        }
        return defaultValue;
    }

    public String idxStr(int i) {
        return TypeUtil.conversion(idx(i), String.class);
    }

    public String idxStr(int i, String defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), String.class);
    }

    public Byte idxByte(int i) {
        return TypeUtil.conversion(idx(i), Byte.class);
    }

    public Byte idxByte(int i, Byte defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Byte.class);
    }

    public Character idxChar(int i) {
        return TypeUtil.conversion(idx(i), Character.class);
    }

    public Character idxChar(int i, Character defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Character.class);
    }

    public Boolean idxBool(int i) {
        return TypeUtil.conversion(idx(i), Boolean.class);
    }

    public Boolean idxBool(int i, Boolean defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Boolean.class);
    }

    public Short idxShort(int i) {
        return TypeUtil.conversion(idx(i), Short.class);
    }

    public Short idxShort(int i, Short defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Short.class);
    }

    public Integer idxInt(int i) {
        return TypeUtil.conversion(idx(i), Integer.class);
    }

    public Integer idxInt(int i, Integer defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Integer.class);
    }

    public Long idxLong(int i) {
        return TypeUtil.conversion(idx(i), Long.class);
    }

    public Long idxLong(int i, Long defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Long.class);
    }

    public Float idxFloat(int i) {
        return TypeUtil.conversion(idx(i), Float.class);
    }

    public Float idxFloat(int i, Float defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Float.class);
    }

    public Double idxDouble(int i) {
        return TypeUtil.conversion(idx(i), Double.class);
    }

    public Double idxDouble(int i, Double defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Double.class);
    }

    public Date idxDate(int i) {
        return TypeUtil.conversion(idx(i), Date.class);
    }

    public Date idxDate(int i, Date defaultValue) {
        return TypeUtil.conversion(idx(i, (V)defaultValue), Date.class);
    }
}
