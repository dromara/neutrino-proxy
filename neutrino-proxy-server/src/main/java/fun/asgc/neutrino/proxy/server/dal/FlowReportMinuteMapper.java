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
package fun.asgc.neutrino.proxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportMinuteDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Mapper
public interface FlowReportMinuteMapper extends BaseMapper<FlowReportMinuteDO> {
    default void clean(Date date) {
        this.delete(new LambdaQueryWrapper<FlowReportMinuteDO>()
                .lt(FlowReportMinuteDO::getCreateTime, date)
        );
    }

    default List<FlowReportMinuteDO> findList(Set<Integer> licenseIds, String date) {
        return this.selectList(new LambdaQueryWrapper<FlowReportMinuteDO>()
                .in(FlowReportMinuteDO::getLicenseId, licenseIds)
                .eq(FlowReportMinuteDO::getDate, date)
        );
    }

    default List<FlowReportMinuteDO> findListByDateRange(Date startDate, Date endDate) {
        return this.selectList(new LambdaQueryWrapper<FlowReportMinuteDO>()
                .ge(FlowReportMinuteDO::getDate, startDate)
                .le(FlowReportMinuteDO::getDate, endDate)
        );
    }

}
