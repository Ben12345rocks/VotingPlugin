package com.Ben12345rocks.VotingPlugin.TopVoter;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Listeners.DateChangedEvent;
import com.Ben12345rocks.AdvancedCore.Listeners.DayChangeEvent;
import com.Ben12345rocks.AdvancedCore.Listeners.MonthChangeEvent;
import com.Ben12345rocks.AdvancedCore.Listeners.PreDateChangedEvent;
import com.Ben12345rocks.AdvancedCore.Listeners.WeekChangeEvent;
import com.Ben12345rocks.AdvancedCore.Objects.UUID;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AdvancedCore.YML.YMLFileHandler;
import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Config.Config;
import com.Ben12345rocks.VotingPlugin.Objects.VoteUser;
import com.Ben12345rocks.VotingPlugin.OtherRewards.OtherVoteReward;
import com.Ben12345rocks.VotingPlugin.UserManager.UserManager;

import ninja.egg82.patterns.ServiceLocator;

public class TopVoterHandler implements Listener {
	/** The instance. */
	static TopVoterHandler instance = new TopVoterHandler();

	private Main main = ServiceLocator.getService(Main.class);

	/**
	 * Gets the single instance of TopVoter.
	 *
	 * @return single instance of TopVoter
	 */
	public static TopVoterHandler getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new top voter.
	 */
	private TopVoterHandler() {
	}

	public ArrayList<String> getTopVoterBlackList() {
		return Config.getInstance().getBlackList();
	}

	/**
	 * Top voters weekly.
	 *
	 * @return the string[]
	 */

	public String[] getTopVotersWeekly() {
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<VoteUser> users = main.convertSet(main.topVoterWeekly.keySet());
		for (int i = 0; i < users.size(); i++) {
			String line = Config.getInstance().getFormatCommandVoteTopLine().replace("%num%", "" + (i + 1))
					.replace("%player%", users.get(i).getPlayerName())
					.replace("%votes%", "" + main.topVoterWeekly.get(users.get(i)));
			msg.add(line);
		}
		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	private HashMap<Integer, String> handlePlaces(Set<String> places) {
		HashMap<Integer, String> place = new HashMap<Integer, String>();
		for (String p : places) {
			String[] data = p.split("-");
			try {
				if (data.length > 1) {
					for (int i = Integer.parseInt(data[0]); i < Integer.parseInt(data[1]); i++) {
						place.put(i, p);
					}
				} else {
					place.put(Integer.parseInt(data[0]), p);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return place;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDateChanged(DateChangedEvent event) {
		main.setUpdate(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDayChange(DayChangeEvent event) {

		for (String uuid : UserManager.getInstance().getAllUUIDs()) {
			VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
			if (user.getDailyTotal() == 0 && user.getDayVoteStreak() != 0) {
				user.setDayVoteStreak(0);
			} else {
				user.addDayVoteStreak();
			}
			OtherVoteReward.getInstance().checkVoteStreak(user, "Day");

			if (user.getHighestDailyTotal() < user.getDailyTotal()) {
				user.setHighestDailyTotal(user.getDailyTotal());
			}
		}

		if (Config.getInstance().getStoreTopVotersDaily()) {
			main.debug("Storing TopVoters Daily");
			storeDailyTopVoters();
		}

		if (Config.getInstance().getDailyAwardsEnabled()) {
			HashMap<Integer, String> places = handlePlaces(Config.getInstance().getDailyPossibleRewardPlaces());
			int i = 0;
			int lastTotal = -1;
			for (VoteUser user : main.topVoterDaily.keySet()) {
				if (!Config.getInstance().getTopVoterIgnorePermission() || !user.isTopVoterIgnore()) {
					if (Config.getInstance().getTopVoterAwardsTies()) {
						if (user.getDailyTotal() != lastTotal) {
							i++;
						}
					} else {
						i++;
					}
					if (places.containsKey(i)) {
						user.giveDailyTopVoterAward(i, places.get(i));
					}
				}
				lastTotal = user.getDailyTotal();
			}
		}
		resetDailyTotals();

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMonthChange(MonthChangeEvent event) {
		for (String uuid : UserManager.getInstance().getAllUUIDs()) {
			VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
			if (user.getMonthTotal() == 0 && user.getMonthVoteStreak() != 0) {
				user.setMonthVoteStreak(0);
			} else {
				user.addMonthVoteStreak();
				OtherVoteReward.getInstance().checkVoteStreak(user, "Month");
			}

			if (user.getHighestMonthlyTotal() < user.getMonthTotal()) {
				user.setHighestMonthlyTotal(user.getMonthTotal());
			}
		}

		if (Config.getInstance().getStoreTopVotersMonthly()) {
			main.debug("Storing TopVoters Monthly");
			storeMonthlyTopVoters();
		}

		if (Config.getInstance().getMonthlyAwardsEnabled()) {
			HashMap<Integer, String> places = handlePlaces(Config.getInstance().getMonthlyPossibleRewardPlaces());
			int i = 0;
			int lastTotal = -1;
			for (VoteUser user : main.topVoterMonthly.keySet()) {

				if (!Config.getInstance().getTopVoterIgnorePermission() || !user.isTopVoterIgnore()) {
					if (Config.getInstance().getTopVoterAwardsTies()) {
						if (user.getMonthTotal() != lastTotal) {
							i++;
						}
					} else {
						i++;
					}
					if (places.containsKey(i)) {
						user.giveMonthlyTopVoterAward(i, places.get(i));
					}
				}
				lastTotal = user.getMonthTotal();
			}
		}
		resetMonthlyTotals();

		if (Config.getInstance().getResetMilestonesMonthly()) {
			for (String uuid : UserManager.getInstance().getAllUUIDs()) {
				VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
				user.setMilestoneCount(0);
				user.setHasGottenMilestone(new HashMap<String, Boolean>());
			}

		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPreDateChanged(PreDateChangedEvent event) {
		main.setUpdate(true);
		main.update();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWeekChange(WeekChangeEvent event) {
		for (String uuid : UserManager.getInstance().getAllUUIDs()) {
			VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
			if (user.getWeeklyTotal() == 0 && user.getWeekVoteStreak() != 0) {
				user.setWeekVoteStreak(0);
			} else {
				user.addWeekVoteStreak();
				OtherVoteReward.getInstance().checkVoteStreak(user, "Week");
			}

			if (user.getHighestWeeklyTotal() < user.getWeeklyTotal()) {
				user.setHighestWeeklyTotal(user.getWeeklyTotal());
			}
		}

		if (Config.getInstance().getStoreTopVotersWeekly()) {
			main.debug("Storing TopVoters Weekly");
			storeWeeklyTopVoters();
		}

		if (Config.getInstance().getWeeklyAwardsEnabled()) {
			HashMap<Integer, String> places = handlePlaces(Config.getInstance().getWeeklyPossibleRewardPlaces());
			int i = 0;
			int lastTotal = -1;
			for (VoteUser user : main.topVoterWeekly.keySet()) {
				if (!Config.getInstance().getTopVoterIgnorePermission() || !user.isTopVoterIgnore()) {
					if (Config.getInstance().getTopVoterAwardsTies()) {
						if (user.getWeeklyTotal() != lastTotal) {
							i++;
						}
					} else {
						i++;
					}
					if (places.containsKey(i)) {
						user.giveWeeklyTopVoterAward(i, places.get(i));
					}
				}
				lastTotal = user.getWeeklyTotal();
			}
		}
		resetWeeklyTotals();

	}

	public void register() {
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	public void resetDailyTotals() {
		for (String uuid : UserManager.getInstance().getAllUUIDs()) {
			VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
			if (user.getDailyTotal() != 0) {
				user.resetDailyTotalVotes();
			}
		}
	}

	public void resetMonthlyTotals() {
		for (String uuid : UserManager.getInstance().getAllUUIDs()) {
			VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
			if (user.getMonthTotal() != 0) {
				user.resetMonthlyTotalVotes();
			}
		}
	}

	public void resetWeeklyTotals() {
		for (String uuid : UserManager.getInstance().getAllUUIDs()) {
			VoteUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
			if (user.getWeeklyTotal() != 0) {
				user.resetWeeklyTotalVotes();
			}
		}
	}

	public LinkedHashMap<VoteUser, Integer> sortByValues(LinkedHashMap<VoteUser, Integer> topVoterAllTime,
			final boolean order) {

		List<Entry<VoteUser, Integer>> list = new LinkedList<Entry<VoteUser, Integer>>(topVoterAllTime.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<VoteUser, Integer>>() {
			@Override
			public int compare(Entry<VoteUser, Integer> o1, Entry<VoteUser, Integer> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		LinkedHashMap<VoteUser, Integer> sortedMap = new LinkedHashMap<VoteUser, Integer>();
		for (Entry<VoteUser, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public void storeDailyTopVoters() {
		LocalDateTime time = LocalDateTime.now().minusHours(5);
		String month = time.getMonth().toString();
		int year = time.getYear();
		int day = time.getDayOfMonth();
		YMLFileHandler file = new YMLFileHandler(new File(main.getDataFolder(),
				"TopVoter" + File.separator + "Daily" + File.separator + year + "_" + month + "_" + day + ".yml"));
		file.setup();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterDaily.entrySet()) {
			topVoters.add(count + ": " + entry.getKey().getPlayerName() + ": " + entry.getValue());
			count++;
		}
		file.getData().set("Day", topVoters);
		file.saveData();
	}

	public void storeMonthlyTopVoters() {
		LocalDateTime time = LocalDateTime.now().minusHours(10);
		String month = time.getMonth().toString();
		int year = time.getYear();
		YMLFileHandler file = new YMLFileHandler(new File(main.getDataFolder(),
				"TopVoter" + File.separator + "Monthly" + File.separator + month + "_" + year + ".yml"));
		file.setup();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterMonthly.entrySet()) {
			topVoters.add(count + ": " + entry.getKey().getPlayerName() + ": " + entry.getValue());
			count++;
		}
		file.getData().set("Month", topVoters);
		file.saveData();
	}

	public void storeWeeklyTopVoters() {
		LocalDateTime time = LocalDateTime.now().minusHours(10);
		String month = time.getMonth().toString();
		int year = time.getYear();
		int week = time.getDayOfYear();
		YMLFileHandler file = new YMLFileHandler(new File(main.getDataFolder(),
				"TopVoter" + File.separator + "Weekly" + File.separator + year + "_" + month + "_" + week + ".yml"));
		file.setup();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterWeekly.entrySet()) {
			topVoters.add(count + ": " + entry.getKey().getPlayerName() + ": " + entry.getValue());
			count++;
		}
		file.getData().set("Week", topVoters);
		file.saveData();
	}

	/**
	 * Top voter all time
	 *
	 * @param page
	 *            the page
	 * @return the string[]
	 */
	public String[] topVoterAllTime(int page) {
		int pagesize = Config.getInstance().getFormatPageSize();
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterAllTime.entrySet()) {
			String line = Config.getInstance().getFormatCommandVoteTopLine();
			line = line.replace("%num%", "" + count);
			line = line.replace("%player%", entry.getKey().getPlayerName());
			line = line.replace("%votes%", "" + entry.getValue());
			topVoters.add(line);
			count++;
		}

		int pageSize = (topVoters.size() / pagesize);
		if ((topVoters.size() % pagesize) != 0) {
			pageSize++;
		}

		String title = Config.getInstance().getFormatCommandVoteTopTitle();
		title = title.replace("%page%", "" + page);
		title = title.replace("%maxpages%", "" + pageSize);
		title = title.replace("%Top%", "All Time");
		msg.add(StringUtils.getInstance().colorize(title));

		for (int i = (page - 1) * pagesize; (i < topVoters.size()) && (i < (((page - 1) * pagesize) + 10)); i++) {
			msg.add(topVoters.get(i));
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	/**
	 * Top voter weekly.
	 *
	 * @param page
	 *            the page
	 * @return the string[]
	 */
	public String[] topVoterDaily(int page) {
		int pagesize = Config.getInstance().getFormatPageSize();
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterDaily.entrySet()) {
			String line = Config.getInstance().getFormatCommandVoteTopLine();
			line = line.replace("%num%", "" + count);
			line = line.replace("%player%", entry.getKey().getPlayerName());
			line = line.replace("%votes%", "" + entry.getValue());
			topVoters.add(line);
			count++;
		}

		int pageSize = (topVoters.size() / pagesize);
		if ((topVoters.size() % pagesize) != 0) {
			pageSize++;
		}

		String title = Config.getInstance().getFormatCommandVoteTopTitle();
		title = title.replace("%page%", "" + page);
		title = title.replace("%maxpages%", "" + pageSize);
		title = title.replace("%Top%", "Daily");
		msg.add(StringUtils.getInstance().colorize(title));

		for (int i = (page - 1) * pagesize; (i < topVoters.size()) && (i < (((page - 1) * pagesize) + 10)); i++) {
			msg.add(topVoters.get(i));
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	/**
	 * Top voter monthly
	 *
	 * @param page
	 *            the page
	 * @return the string[]
	 */
	public String[] topVoterMonthly(int page) {
		int pagesize = Config.getInstance().getFormatPageSize();
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterMonthly.entrySet()) {
			String line = Config.getInstance().getFormatCommandVoteTopLine();
			line = line.replace("%num%", "" + count);
			line = line.replace("%player%", entry.getKey().getPlayerName());
			line = line.replace("%votes%", "" + entry.getValue());
			topVoters.add(line);
			count++;
		}

		int pageSize = (topVoters.size() / pagesize);
		if ((topVoters.size() % pagesize) != 0) {
			pageSize++;
		}

		String title = Config.getInstance().getFormatCommandVoteTopTitle();
		title = title.replace("%page%", "" + page);
		title = title.replace("%maxpages%", "" + pageSize);
		title = title.replace("%Top%", "Monthly");
		msg.add(StringUtils.getInstance().colorize(title));

		for (int i = (page - 1) * pagesize; (i < topVoters.size()) && (i < (((page - 1) * pagesize) + 10)); i++) {
			msg.add(topVoters.get(i));
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	/**
	 * Top voters all time
	 *
	 * @return the string[]
	 */
	public String[] topVotersAllTime() {
		ArrayList<String> msg = new ArrayList<String>();
		List<Entry<VoteUser, Integer>> list = new LinkedList<Entry<VoteUser, Integer>>(main.topVoterAllTime.entrySet());
		int i = 0;
		for (Entry<VoteUser, Integer> entry : list) {
			String line = "%num%: %player%, %votes%";
			line = line.replace("%num%", "" + (i + 1));
			try {
				line = line.replace("%player%", entry.getKey().getPlayerName());
			} catch (Exception ex) {
				AdvancedCoreHook.getInstance().debug(ex);
			}
			line = line.replace("%votes%", "" + entry.getValue());

			msg.add(line);
			i++;
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	/**
	 * Top voters daily.
	 *
	 * @return the string[]
	 */

	public String[] topVotersDaily() {
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<VoteUser> users = main.convertSet(main.topVoterDaily.keySet());
		for (int i = 0; i < users.size(); i++) {
			String line = "%num%: %player%, %votes%";
			line = line.replace("%num%", "" + (i + 1));
			try {
				line = line.replace("%player%", users.get(i).getPlayerName());
			} catch (Exception ex) {
				AdvancedCoreHook.getInstance().debug(ex);
			}
			line = line.replace("%votes%", "" + main.topVoterMonthly.get(users.get(i)));
			msg.add(line);
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	/**
	 * Top voters.
	 *
	 * @return the string[]
	 */
	public String[] topVotersMonthly() {
		ArrayList<String> msg = new ArrayList<String>();
		List<Entry<VoteUser, Integer>> list = new LinkedList<Entry<VoteUser, Integer>>(main.topVoterMonthly.entrySet());
		int i = 0;
		for (Entry<VoteUser, Integer> entry : list) {
			String line = "%num%: %player%, %votes%";
			line = line.replace("%num%", "" + (i + 1));
			try {
				line = line.replace("%player%", entry.getKey().getPlayerName());
			} catch (Exception ex) {
				AdvancedCoreHook.getInstance().debug(ex);
			}
			line = line.replace("%votes%", "" + entry.getValue());

			msg.add(line);
			i++;
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	/**
	 * Top voter weekly.
	 *
	 * @param page
	 *            the page
	 * @return the string[]
	 */
	public String[] topVoterWeekly(int page) {
		int pagesize = Config.getInstance().getFormatPageSize();
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<String> topVoters = new ArrayList<String>();
		int count = 1;
		for (Entry<VoteUser, Integer> entry : main.topVoterWeekly.entrySet()) {
			String line = Config.getInstance().getFormatCommandVoteTopLine();
			line = line.replace("%num%", "" + count);
			line = line.replace("%player%", entry.getKey().getPlayerName());
			line = line.replace("%votes%", "" + entry.getValue());
			topVoters.add(line);
			count++;
		}

		int pageSize = (topVoters.size() / pagesize);
		if ((topVoters.size() % pagesize) != 0) {
			pageSize++;
		}

		String title = Config.getInstance().getFormatCommandVoteTopTitle();
		title = title.replace("%page%", "" + page);
		title = title.replace("%maxpages%", "" + pageSize);
		title = title.replace("%Top%", "Weekly");
		msg.add(StringUtils.getInstance().colorize(title));

		for (int i = (page - 1) * pagesize; (i < topVoters.size()) && (i < (((page - 1) * pagesize) + 10)); i++) {
			msg.add(topVoters.get(i));
		}

		msg = ArrayUtils.getInstance().colorize(msg);
		return ArrayUtils.getInstance().convert(msg);
	}

	public synchronized void updateTopVoters(ArrayList<VoteUser> users1) {
		ArrayList<VoteUser> users = new ArrayList<VoteUser>();
		ArrayList<String> blackList = getTopVoterBlackList();
		for (VoteUser user : users1) {
			if (!blackList.contains(user.getPlayerName())) {
				if ((!Config.getInstance().getTopVoterIgnorePermission() || !user.isTopVoterIgnore())
						&& !user.isBanned()) {
					users.add(user);
				}
			}
		}
		main.topVoterAllTime.clear();
		if (Config.getInstance().getLoadTopVoterAllTime()) {
			for (VoteUser user : users) {
				int total = user.getAllTimeTotal();
				if (total > 0) {
					main.topVoterAllTime.put(user, total);
				}
			}
			main.topVoterAllTime = sortByValues(main.topVoterAllTime, false);
			main.debug("All Time TopVoter loaded");
		}

		main.topVoterMonthly.clear();
		if (Config.getInstance().getLoadTopVoterMonthly()) {
			for (VoteUser user : users) {
				int total = user.getMonthTotal();
				if (total > 0) {
					main.topVoterMonthly.put(user, total);
				}
			}
			main.topVoterMonthly = sortByValues(main.topVoterMonthly, false);
			main.debug("Monthly TopVoter loaded");
		}

		main.topVoterWeekly.clear();
		if (Config.getInstance().getLoadTopVoterWeekly()) {
			for (VoteUser user : users) {
				int total = user.getWeeklyTotal();
				if (total > 0) {
					main.topVoterWeekly.put(user, total);
				}
			}

			main.topVoterWeekly = sortByValues(main.topVoterWeekly, false);
			main.debug("Weekly TopVoter loaded");
		}

		main.topVoterDaily.clear();
		if (Config.getInstance().getLoadTopVoterDaily()) {
			for (VoteUser user : users) {
				int total = user.getDailyTotal();
				if (total > 0) {
					main.topVoterDaily.put(user, total);
				}
			}
			main.topVoterDaily = sortByValues(main.topVoterDaily, false);
			main.debug("Daily TopVoter loaded");
		}

		main.debug("Updated TopVoter");
	}
}
