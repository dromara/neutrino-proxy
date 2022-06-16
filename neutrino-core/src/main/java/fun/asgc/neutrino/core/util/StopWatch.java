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

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class StopWatch {

	/**
	 * Identifier of this stop watch.
	 * Handy when we have output from multiple stop watches
	 * and need to distinguish between them in log or console output.
	 */
	private final String id;

	private boolean keepTaskList = true;

	private final List<TaskInfo> taskList = new LinkedList<>();

	/** Start time of the current task */
	private long startTimeMillis;

	/** Name of the current task */
	private String currentTaskName;

	private TaskInfo lastTaskInfo;

	private int taskCount;

	/** Total running time */
	private long totalTimeMillis;


	/**
	 * Construct a new stop watch. Does not start any task.
	 */
	public StopWatch() {
		this("");
	}

	/**
	 * Construct a new stop watch with the given id.
	 * Does not start any task.
	 * @param id identifier for this stop watch.
	 * Handy when we have output from multiple stop watches
	 * and need to distinguish between them.
	 */
	public StopWatch(String id) {
		this.id = id;
	}


	/**
	 * Return the id of this stop watch, as specified on construction.
	 * @return the id (empty String by default)
	 * @since 4.2.2
	 * @see #StopWatch(String)
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Determine whether the TaskInfo array is built over time. Set this to
	 * "false" when using a StopWatch for millions of intervals, or the task
	 * info structure will consume excessive memory. Default is "true".
	 */
	public void setKeepTaskList(boolean keepTaskList) {
		this.keepTaskList = keepTaskList;
	}


	/**
	 * Start an unnamed task. The results are undefined if {@link #stop()}
	 * or timing methods are called without invoking this method.
	 * @see #stop()
	 */
	public void start() throws IllegalStateException {
		start("");
	}

	/**
	 * Start a named task. The results are undefined if {@link #stop()}
	 * or timing methods are called without invoking this method.
	 * @param taskName the name of the task to start
	 * @see #stop()
	 */
	public void start(String taskName) throws IllegalStateException {
		if (this.currentTaskName != null) {
			throw new IllegalStateException("Can't start StopWatch: it's already running");
		}
		this.currentTaskName = taskName;
		this.startTimeMillis = System.currentTimeMillis();
	}

	/**
	 * Stop the current task. The results are undefined if timing
	 * methods are called without invoking at least one pair
	 * {@code start()} / {@code stop()} methods.
	 * @see #start()
	 */
	public void stop() throws IllegalStateException {
		if (this.currentTaskName == null) {
			throw new IllegalStateException("Can't stop StopWatch: it's not running");
		}
		long lastTime = System.currentTimeMillis() - this.startTimeMillis;
		this.totalTimeMillis += lastTime;
		this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
		if (this.keepTaskList) {
			this.taskList.add(this.lastTaskInfo);
		}
		++this.taskCount;
		this.currentTaskName = null;
	}

	/**
	 * Return whether the stop watch is currently running.
	 * @see #currentTaskName()
	 */
	public boolean isRunning() {
		return (this.currentTaskName != null);
	}

	/**
	 * Return the name of the currently running task, if any.
	 * @since 4.2.2
	 * @see #isRunning()
	 */
	public String currentTaskName() {
		return this.currentTaskName;
	}


	/**
	 * Return the time taken by the last task.
	 */
	public long getLastTaskTimeMillis() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task interval");
		}
		return this.lastTaskInfo.getTimeMillis();
	}

	/**
	 * Return the name of the last task.
	 */
	public String getLastTaskName() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task name");
		}
		return this.lastTaskInfo.getTaskName();
	}

	/**
	 * Return the last task as a TaskInfo object.
	 */
	public TaskInfo getLastTaskInfo() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task info");
		}
		return this.lastTaskInfo;
	}


	/**
	 * Return the total time in milliseconds for all tasks.
	 */
	public long getTotalTimeMillis() {
		return this.totalTimeMillis;
	}

	/**
	 * Return the total time in seconds for all tasks.
	 */
	public double getTotalTimeSeconds() {
		return this.totalTimeMillis / 1000.0;
	}

	/**
	 * Return the number of tasks timed.
	 */
	public int getTaskCount() {
		return this.taskCount;
	}

	/**
	 * Return an array of the data for tasks performed.
	 */
	public TaskInfo[] getTaskInfo() {
		if (!this.keepTaskList) {
			throw new UnsupportedOperationException("Task info is not being kept!");
		}
		return this.taskList.toArray(new TaskInfo[0]);
	}


	/**
	 * Return a short description of the total running time.
	 */
	public String shortSummary() {
		return "StopWatch '" + getId() + "': running time (millis) = " + getTotalTimeMillis();
	}

	/**
	 * Return a string with a table describing all tasks performed.
	 * For custom reporting, call getTaskInfo() and use the task info directly.
	 */
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder(shortSummary());
		sb.append('\n');
		if (!this.keepTaskList) {
			sb.append("No task info kept");
		}
		else {
			sb.append("-----------------------------------------\n");
			sb.append("ms     %     Task name\n");
			sb.append("-----------------------------------------\n");
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMinimumIntegerDigits(5);
			nf.setGroupingUsed(false);
			NumberFormat pf = NumberFormat.getPercentInstance();
			pf.setMinimumIntegerDigits(3);
			pf.setGroupingUsed(false);
			for (TaskInfo task : getTaskInfo()) {
				sb.append(nf.format(task.getTimeMillis())).append("  ");
				sb.append(pf.format(task.getTimeSeconds() / getTotalTimeSeconds())).append("  ");
				sb.append(task.getTaskName()).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Return an informative string describing all tasks performed
	 * For custom reporting, call {@code getTaskInfo()} and use the task info directly.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(shortSummary());
		if (this.keepTaskList) {
			for (TaskInfo task : getTaskInfo()) {
				sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeMillis());
				long percent = Math.round((100.0 * task.getTimeSeconds()) / getTotalTimeSeconds());
				sb.append(" = ").append(percent).append("%");
			}
		}
		else {
			sb.append("; no task info kept");
		}
		return sb.toString();
	}


	/**
	 * Inner class to hold data about one task executed within the stop watch.
	 */
	public static final class TaskInfo {

		private final String taskName;

		private final long timeMillis;

		TaskInfo(String taskName, long timeMillis) {
			this.taskName = taskName;
			this.timeMillis = timeMillis;
		}

		/**
		 * Return the name of this task.
		 */
		public String getTaskName() {
			return this.taskName;
		}

		/**
		 * Return the time in milliseconds this task took.
		 */
		public long getTimeMillis() {
			return this.timeMillis;
		}

		/**
		 * Return the time in seconds this task took.
		 */
		public double getTimeSeconds() {
			return (this.timeMillis / 1000.0);
		}
	}
}
