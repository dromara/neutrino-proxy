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
package fun.asgc.neutrino.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应用生命周期状态枚举
 * @author: aoshiguchen
 * @date: 2022/10/10
 */
@AllArgsConstructor
@Getter
public enum AppLifeCycleStatusEnum {
    // 应用已创建
    APP_CREATE(100, "已创建"),
    // 应用已初始化
    APP_INIT(200, "配置初始化"),
    // 容器已初始化
    CONTAINER_INIT(300, "容器初始化"),
    // 应用已启动完成
    APP_STARTUP(400, "应用启动"),
    // 应用准备销毁
    APP_PRE_DESTROY(500, "应用准备销毁"),
    // 应用已销毁
    APP_DESTROY(600, "应用已销毁");

    private Integer status;
    private String desc;
    private static Map<Integer, AppLifeCycleStatusEnum> CACHE = Stream.of(AppLifeCycleStatusEnum.values()).collect(Collectors.toMap(AppLifeCycleStatusEnum::getStatus, Function.identity()));

    public static AppLifeCycleStatusEnum of(Integer status) {
        return CACHE.get(status);
    }
}
