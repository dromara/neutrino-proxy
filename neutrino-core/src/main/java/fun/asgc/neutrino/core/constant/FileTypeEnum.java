package fun.asgc.neutrino.core.constant;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: aoshiguchen
 * @date: 2023/3/6
 */
@AllArgsConstructor
@Getter
public enum FileTypeEnum {
    YML(1, "yml", Sets.newHashSet(".yml", ".yaml")),
    PROPERTIES(2, "properties", Sets.newHashSet(".properties")),
    JSON(3, "json", Sets.newHashSet(".json")),
    ;

    private Integer type;
    private String desc;
    private Set<String> suffixSet;
    private static final Map<Integer, FileTypeEnum> typeMap = Stream.of(FileTypeEnum.values()).collect(Collectors.toMap(FileTypeEnum::getType, Function.identity()));

    public static FileTypeEnum ofType(Integer type) {
        return typeMap.get(type);
    }
}
