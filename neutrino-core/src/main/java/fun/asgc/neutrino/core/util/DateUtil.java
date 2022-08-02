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
package fun.asgc.neutrino.core.util;

import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class DateUtil {
	private static final Cache<String, SimpleDateFormat> sdfCache = new MemoryCache<>();
	private static SimpleDateFormat getSimpleDateFormat(String format) {
		try {
			return LockUtil.doubleCheckProcess(
				() -> !sdfCache.containsKey(format),
				format,
				() -> sdfCache.set(format, new SimpleDateFormat(format)),
				() -> sdfCache.get(format)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析日期
	 *
	 * @param date    日期类
	 * @param pattern 日期格式
	 * @return 返回日期
	 */
	public static Date parseStr(String date, String pattern) {
		if (date == null || pattern == null) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date parse = null;
		try {
			parse = simpleDateFormat.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return parse;
	}


	/**
	 * @param date    时间。若为空，则返回空串
	 * @param pattern 时间格式化
	 * @return 格式化后的时间字符串.
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * @param date    日期
	 * @param pattern 格式
	 * @return 日期类型
	 */
	public static Date parse(String date, String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 是否在指定时间段之间
	 *
	 * @param judgeTime 比较时间
	 * @param beginTime 开始时间
	 * @param endTime   结束时间
	 * @param flag      0-[beginTime<=judgeTime<=endTime]
	 *                  1-[beginTime<judgeTime<=endTime]
	 *                  2-[beginTime<=judgeTime<endTime]
	 *                  3-[beginTime<judgeTime<endTime]
	 * @return 判断结果
	 */
	public static boolean isBetweenTimes(Date judgeTime, Date beginTime, Date endTime, int flag) {
		switch (flag) {
			case 0:
				return (beginTime.getTime() <= judgeTime.getTime() && judgeTime.getTime() <= endTime.getTime());
			case 1:
				return (beginTime.getTime() < judgeTime.getTime() && judgeTime.getTime() <= endTime.getTime());
			case 2:
				return (beginTime.getTime() <= judgeTime.getTime() && judgeTime.getTime() < endTime.getTime());
			case 3:
				return (beginTime.getTime() < judgeTime.getTime() && judgeTime.getTime() < endTime.getTime());
			default:
				return false;
		}
	}

	/**
	 * 取得日期所在月的month个月同一天<br />
	 *
	 * @param date  日期
	 * @param month 月
	 * @return 日期
	 */
	public static Date getMonthDay(Date date, int month) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	/**
	 * 获取该日期当月最后一天
	 *
	 * @param date 日期
	 * @return 结果日期
	 */
	public static Date getMonthEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDayEnd(date));
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	/**
	 * 计算当期时间相差的日期
	 *
	 * @param date   设置时间
	 * @param field  日历字段.<br/>eg:Calendar.MONTH,Calendar.DAY_OF_MONTH,<br/>Calendar.HOUR_OF_DAY等.
	 * @param amount 相差的数值
	 * @return 计算后的日志
	 */
	public static Date addDate(Date date, int field, int amount) {
		Calendar c = Calendar.getInstance();
		if (date != null) {
			c.setTime(date);
		}
		c.add(field, amount);
		return c.getTime();
	}

	/**
	 * 计算当期时间相差的日期
	 *
	 * @param field  日历字段.<br/>eg:Calendar.MONTH,Calendar.DAY_OF_MONTH,<br/>Calendar.HOUR_OF_DAY等.
	 * @param amount 相差的数值
	 * @return 计算后的日志
	 */
	public static Date addDate(int field, int amount) {
		return addDate(null, field, amount);
	}

	/**
	 * 设置Calendar的小时、分钟、秒、毫秒
	 *
	 * @param calendar    日历
	 * @param hour        小时
	 * @param minute      分钟
	 * @param second      秒
	 * @param milliSecond 毫秒
	 * @return 结果日期
	 */
	public static void setCalender(Calendar calendar, int hour, int minute, int second, int milliSecond) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, milliSecond);
	}

	/**
	 * 获取某个时间段内的每天时间日期
	 *
	 * @param beginDate 开始日期
	 * @param endDate   结束日期
	 * @return 返回结果
	 */
	public static List<String> getBetweenTimes(String beginDate, String endDate) {
		if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate)) {
			return Collections.emptyList();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dBegin = null;
		Date dEnd = null;
		try {
			dBegin = sdf.parse(beginDate);
			dEnd = sdf.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate)) {
			return Collections.emptyList();
		}

		return findDates(dBegin, dEnd);
	}

	/**
	 * @param dBegin 开始时间
	 * @param dEnd   结束时间
	 * @return 结果集
	 */
	public static List<String> findDates(Date dBegin, Date dEnd) {
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		lDate.add(sd.format(dBegin));
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(sd.format(calBegin.getTime()));
		}
		return lDate;
	}

	/**
	 * 取得日期所在年的下一年同一天<br />
	 * 注：若参数date为空，则取得第当前年所对应的下一年
	 *
	 * @param date
	 * @return
	 */
	public static Date getNextYearDay(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.YEAR, 1);
		return cal.getTime();
	}


	/**
	 * 获取指定天开始时间
	 *
	 * @param date 日期
	 * @return 获得该日期的开始
	 */
	public static Date getDayBegin(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setCalender(calendar, 0, 0, 0, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当天开始时间
	 *
	 * @return 获得该日期的开始
	 */
	public static Date getDayBegin() {
		return getDayBegin(new Date());
	}

	/**
	 * 获取指定天结束时间
	 *
	 * @param date 日期
	 * @return 获得该日期的结束
	 */
	public static Date getDayEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setCalender(calendar, 23, 59, 59, 999);
		return calendar.getTime();
	}

	/**
	 * 获取指定天结束时间
	 *
	 * @param date 日期
	 * @return 获得该日期的结束
	 */
	public static Date getDayEnd2(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setCalender(calendar, 23, 59, 59, 000);
		return calendar.getTime();
	}

	/**
	 * 日期遍历接口
	 * @param startDate 开始日期 yyyy-MM-dd
	 * @param endDate 结束日期 yyyy-MM-dd
	 * @param consumer 执行器回调
	 */
	public static void dayForEach(String startDate, String endDate, Consumer<String> consumer) {
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate) || null == consumer) {
			return;
		}
		Date current = parse(startDate, "yyyy-MM-dd");
		Date end = parse(endDate, "yyyy-MM-dd");
		while (!current.after(end)) {
			consumer.accept(format(current, "yyyy-MM-dd"));
			current = addDate(current, Calendar.DATE, 1);
		}
	}
}
