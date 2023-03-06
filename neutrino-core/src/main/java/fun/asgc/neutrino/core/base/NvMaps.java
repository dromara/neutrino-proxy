package fun.asgc.neutrino.core.base;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.constant.FileTypeEnum;
import fun.asgc.neutrino.core.constant.MetaDataConstant;
import fun.asgc.neutrino.core.util.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置管理器
 *
 * <p>
 * 支持以下五级配置：
 * 1、别名、默认值
 * 2、内部配置文件
 * 3、环境变量
 * 4、外部配置文件
 * 5、启动参数
 * </p>
 *
 * <p>
 * 支持以下3种配置文件格式：
 * 1、yml
 * 2、properties
 * 3、json
 * </p>
 *
 * <p>
 * 支持指定以下配置：
 * 1、内部配置文件路径、格式 （可根据文件后缀自动识别格式）
 * 2、环境变量前缀 （若指定了前缀，则只加载包含该前缀的环境变量）
 * 3、外部配置文件路径、格式（可根据文件后缀自动识别格式）
 * 4、启动参数前缀（若指定了前缀，则只加载包含该前缀的启动参数）
 * </p>
 * @author: aoshiguchen
 * @date: 2023/3/5
 */
public final class NvMaps {
    private NvMap nvMap;

    private NvMaps() {
        this.nvMap = NvMap.of();
    }

    public static NvMaps of() {
        return new NvMaps();
    }

    public NvMaps stageDone() {
        this.nvMap = NvMap.of(this.nvMap);
        return this;
    }

    public NvMap getNvMap() {
        return this.nvMap;
    }

    public NvMaps setKv(Object k, Object v) {
        this.nvMap.set(k, v);
        return this;
    }

    public NvMaps setKv(Object k, Object alias, Object v) {
        this.nvMap.set(k, alias, v);
        return this;
    }

    public NvMaps setAlias(Object k, Object alias) {
        this.nvMap.setAlias(k, alias);
        return this;
    }
    public NvMaps loadFile(String path) throws IOException {
        if (StringUtil.isEmpty(path)) {
            return this;
        }
        String suffix = FileUtil.getFileSuffix(path);
        if (StringUtil.isEmpty(suffix)) {
            return this;
        }
        suffix = suffix.toLowerCase();
        if (FileTypeEnum.YML.getSuffixSet().contains(suffix)) {
            return loadFile(FileTypeEnum.YML, path);
        } else if (FileTypeEnum.PROPERTIES.getSuffixSet().contains(suffix)) {
            return loadFile(FileTypeEnum.PROPERTIES, path);
        } else if (FileTypeEnum.JSON.getSuffixSet().contains(suffix)) {
            return loadFile(FileTypeEnum.JSON, path);
        }
        return this;
    }
    public NvMaps loadYmlFile(String path) throws IOException {
        return loadFile(FileTypeEnum.YML, path);
    }
    public NvMaps loadPropertiesFile(String path) throws IOException {
        return loadFile(FileTypeEnum.PROPERTIES, path);
    }
    public NvMaps loadJsonFile(String path) throws IOException {
        return loadFile(FileTypeEnum.JSON, path);
    }
    public NvMaps loadFile(FileTypeEnum fileType, String path) throws IOException {
        try (InputStream in = FileUtil.getInputStream(path)){
            return loadFile(fileType, in);
        }
    }
    public NvMaps loadFile(FileTypeEnum fileType, InputStream in) throws IOException {
        if (null == fileType || null == in) {
            return this;
        }
        if (fileType == FileTypeEnum.YML) {
            Map<String, Object> map = new Yaml().load(in);
            load(map);
        } else if (fileType == FileTypeEnum.PROPERTIES) {
            Properties properties = new Properties();
            properties.load(in);
        } else if (fileType == FileTypeEnum.JSON) {
            String content = FileUtil.readContentAsString(in);
            JSONObject jsonObject = JSONObject.parseObject(content);
            load(jsonObject);
        }
        return null;
    }

    public NvMaps load(Properties properties) {
        if (null == properties) {
            return this;
        }
        for (String k : properties.stringPropertyNames()) {
            this.setKv(k, properties.getProperty(k));
        }
        return this;
    }

    public NvMaps load(Map<String, Object> config) {
        if (CollectionUtil.isEmpty(config)) {
            return this;
        }
        for (String key : config.keySet()) {
            Object value = config.get(key);
            if (null != value && Map.class.isAssignableFrom(value.getClass())) {
                load(key, (Map)config.get(key));
            } else {
                this.setKv(key, value);
            }
        }
        return this;
    }

    private void load(String prefix, Map<Object, Object> config) {
        for (Object key : config.keySet()) {
            String k = TypeUtil.conversion(key, String.class);
            if (null == k) {
                continue;
            }
            Object value = config.get(key);
            if (null != value && Map.class.isAssignableFrom(value.getClass())) {
                load(prefix.concat("." + k), (Map)config.get(key));
            } else {
                this.setKv(prefix.concat("." + k), value);
            }
        }
    }

    public NvMaps loadEnvironmentVariable() {
        String env = System.getenv(MetaDataConstant.ENVIRONMENT_VARIABLE_KEY);
        if (StringUtil.isEmpty(env)) {
            return this;
        }
        String[] tmp = env.split(File.pathSeparator);
        if (ArrayUtil.isEmpty(tmp) || tmp.length < 2) {
            return this;
        }
        load(kvListToMap(tmp));
        return this;
    }

    public NvMaps loadMainArgs(String[] mainArgs) {
        load(kvListToMap(mainArgs));
        return this;
    }

    private Map<String, Object> kvListToMap(String[] args) {
        Map<String, Object> map = new HashMap<>();
        if (ArrayUtil.isEmpty(args)) {
            return map;
        }
        for (String kv : args) {
            if (!StringUtil.isEmpty(kv)) {
                int index = kv.indexOf("=");
                if (index > 0 && index < kv.length() - 1) {
                    String key = kv.substring(0, index);
                    String val = kv.substring(index + 1);
                    map.put(key, val);
                }
            }
        }
        return map;
    }
}
