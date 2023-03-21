/**
 * Copyright (c) 2022 aoshiguchen
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.proxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Mapper
public interface LicenseMapper extends BaseMapper<LicenseDO> {

    default List<LicenseDO> listAll() {
        return this.selectList(new LambdaQueryWrapper<>());
    }

    default List<LicenseDO> listByUserId(Integer userId) {
        return this.selectList(new LambdaQueryWrapper<LicenseDO>()
                .eq(LicenseDO::getUserId, userId)
        );
    }

    default LicenseDO queryById(Integer licenseId) {
        return this.selectOne(new LambdaQueryWrapper<LicenseDO>()
                .eq(LicenseDO::getId, licenseId));
    }

    default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<LicenseDO>()
                .eq(LicenseDO::getId, id)
                .set(LicenseDO::getEnable, enable)
                .set(LicenseDO::getUpdateTime, updateTime)
        );
    }

    default void updateOnlineStatus(Integer id, Integer isOnline, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<LicenseDO>()
                .eq(LicenseDO::getId, id)
                .set(LicenseDO::getIsOnline, isOnline)
                .set(LicenseDO::getUpdateTime, updateTime)
        );
    }

    default void updateOnlineStatus(Integer isOnline, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<LicenseDO>()
                .set(LicenseDO::getIsOnline, updateTime)
                .set(LicenseDO::getUpdateTime, updateTime)
        );
    }

    default void reset(Integer id, String key, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<LicenseDO>()
                .eq(LicenseDO::getId, id)
                .set(LicenseDO::getKey, key)
                .set(LicenseDO::getUpdateTime, updateTime)
        );
    }

    default LicenseDO findById(Integer id) {
        return this.selectById(id);
    }

    default void update(Integer id, String name, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<LicenseDO>()
                .eq(LicenseDO::getId, id)
                .set(LicenseDO::getName, name)
                .set(LicenseDO::getUpdateTime, updateTime)
        );
    }

    default List<LicenseDO> findByIds(Set<Integer> ids) {
        return selectBatchIds(ids);
    }

    default LicenseDO checkRepeat(Integer userId, String name) {
        return this.selectOne(new LambdaQueryWrapper<LicenseDO>()
                .eq(LicenseDO::getUserId, userId)
                .eq(LicenseDO::getName, name)
                .last("limit 1")
        );
    }

    default LicenseDO checkRepeat(Integer userId, String name, Set<Integer> excludeIds) {
        return this.selectOne(new LambdaQueryWrapper<LicenseDO>()
                .eq(LicenseDO::getUserId, userId)
                .eq(LicenseDO::getName, name)
                .notIn(LicenseDO::getId, excludeIds)
                .last("limit 1")
        );
    }

    default LicenseDO findByKey(String licenseKey) {
        return selectOne(new LambdaQueryWrapper<LicenseDO>()
                .eq(LicenseDO::getKey, licenseKey)
        );
    }
}
