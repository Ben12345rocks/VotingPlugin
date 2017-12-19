package com.Ben12345rocks.VotingPlugin.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.Ben12345rocks.AdvancedCore.YML.YMLFile;
import com.Ben12345rocks.VotingPlugin.Main;

// TODO: Auto-generated Javadoc
/**
 * The Class Config.
 */
public class Config extends YMLFile {

	/** The instance. */
	static Config instance = new Config();

	/** The plugin. */
	static Main plugin = Main.plugin;

	/**
	 * Gets the single instance of Config.
	 *
	 * @return single instance of Config
	 */
	public static Config getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new config.
	 */
	public Config() {
		super(new File(Main.plugin.getDataFolder(), "Config.yml"));
	}

	/**
	 * Allow un joined.
	 *
	 * @return true, if successful
	 */
	public boolean allowUnJoined() {
		return getData().getBoolean("AllowUnjoined");
	}

	/**
	 * Gets the all sites reward.
	 *
	 * @return the all sites reward
	 */
	public String getAllSitesRewardPath() {
		return "AllSites";
	}

	public boolean getAlternateUUIDLookup() {
		return getData().getBoolean("AlternateUUIDLookup");
	}

	/**
	 * Gets the rewards.
	 *
	 * @return the rewards
	 */
	public String getAnySiteRewardsPath() {
		return "AnySiteRewards";

	}

	/**
	 * Gets the auto create vote sites.
	 *
	 * @return the auto create vote sites
	 */
	public boolean getAutoCreateVoteSites() {
		return getData().getBoolean("AutoCreateVoteSites");
	}

	public boolean getAutoDownload() {
		return getData().getBoolean("AutoDownload");
	}

	public boolean getAutoKillInvs() {
		return getData().getBoolean("AutoKillInvs", true);
	}

	public ConfigurationSection getBackButton() {
		return getData().getConfigurationSection("BackButton");
	}

	/**
	 * Gets the black list.
	 *
	 * @return the black list
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getBlackList() {
		return (ArrayList<String>) getData().getList("BlackList", new ArrayList<String>());
	}

	/**
	 * Gets the broad cast votes enabled.
	 *
	 * @return the broad cast votes enabled
	 */
	public boolean getBroadCastVotesEnabled() {
		return getData().getBoolean("BroadcastVote", true);
	}

	public boolean getDisableCheckOnWorldChange() {
		return getData().getBoolean("DisableCheckOnWorldChange");
	}

	public boolean getClearCacheOnUpdate() {
		return getData().getBoolean("ClearCacheOnUpdate");
	}

	public boolean getCommandsUseGUIBest() {
		return getData().getBoolean("Commands.UseGUI.Best");
	}

	public boolean getCommandsUseGUILast() {
		return getData().getBoolean("Commands.UseGUI.Last", true);
	}

	public boolean getCommandsUseGUINext() {
		return getData().getBoolean("Commands.UseGUI.Next", true);
	}

	public boolean getCommandsUseGUIStreak() {
		return getData().getBoolean("Commands.UseGUI.Streak");
	}

	public boolean getCommandsUseGUIToday() {
		return getData().getBoolean("Commands.UseGUI.Today", true);
	}

	public boolean getCommandsUseGUITopVoter() {
		return getData().getBoolean("Commands.UseGUI.TopVoter", true);
	}

	public boolean getCommandsUseGUITotal() {
		return getData().getBoolean("Commands.UseGUI.Total", true);
	}

	public boolean getCommandsUseGUIVote() {
		return getData().getBoolean("Commands.UseGUI.Vote", true);
	}

	public boolean getCountFakeVotes() {
		return getData().getBoolean("CountFakeVotes", true);
	}

	/**
	 * Gets the cumulative reward enabled.
	 *
	 * @param cumulative
	 *            the cumulative
	 * @return the cumulative reward enabled
	 */
	public boolean getCumulativeRewardEnabled(int cumulative) {
		return getData().getBoolean("Cumulative." + cumulative + ".Enabled");
	}

	/**
	 * Gets the cumulative rewards path
	 *
	 * @param cumulative
	 *            the cumulative
	 * @return the cumulative rewards
	 */
	public String getCumulativeRewardsPath(int cumulative) {
		return "Cumulative." + cumulative + ".Rewards";
	}

	/**
	 * Gets the cumulative votes.
	 *
	 * @return the cumulative votes
	 */
	public Set<String> getCumulativeVotes() {
		try {
			Set<String> set = getData().getConfigurationSection("Cumulative").getKeys(false);
			if (set != null) {
				return set;
			}
			return new HashSet<String>();
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	/**
	 * Gets the cumulative votes in same day.
	 *
	 * @param cumulative
	 *            the cumulative
	 * @return the cumulative votes in same day
	 */
	public boolean getCumulativeVotesInSameDay(int cumulative) {
		return getData().getBoolean("Cumulative." + cumulative + ".VotesInSameDay");
	}

	public boolean getCumulativeVotesInSameWeek(int cumulative) {
		return getData().getBoolean("Cumulative." + cumulative + ".VotesInSameWeek");
	}

	/**
	 * Gets the daily award rewards path
	 *
	 * @param pos
	 *            the pos
	 * @return the daily award rewards
	 */
	public String getDailyAwardRewardsPath(int pos) {
		return "DailyAwards." + pos + ".Rewards";
	}

	/**
	 * Gets the daily awards enabled.
	 *
	 * @return the daily awards enabled
	 */
	public boolean getDailyAwardsEnabled() {
		return getData().getBoolean("EnableDailyAwards");
	}

	/**
	 * Gets the daily possible reward places.
	 *
	 * @return the daily possible reward places
	 */
	public Set<String> getDailyPossibleRewardPlaces() {
		try {
			return getData().getConfigurationSection("DailyAwards").getKeys(false);
		} catch (Exception ex) {
			return new HashSet<String>();

		}
	}

	public String getDataStorage() {
		return getData().getString("DataStorage", "FLAT");
	}

	/**
	 * Gets the debug enabled.
	 *
	 * @return the debug enabled
	 */
	public boolean getDebugEnabled() {
		return getData().getBoolean("Debug");
	}

	/**
	 * Gets the debug info ingame.
	 *
	 * @return the debug info ingame
	 */
	public boolean getDebugInfoIngame() {
		return getData().getBoolean("DebugInfoIngame");
	}

	public int getDelayBetweenUpdates() {
		return getData().getInt("DelayBetweenUpdates", 3);
	}

	public boolean getDisableNoServiceSiteMessage() {
		return getData().getBoolean("DisableNoServiceSiteMessage");
	}

	public boolean getExtraDebug() {
		return getData().getBoolean("ExtraDebug");
	}

	/**
	 * Gets the first vote rewards.
	 *
	 * @return the first vote rewards
	 */
	public String getFirstVoteRewardsPath() {
		return "FirstVote";
	}

	/**
	 * Gets the broad cast msg.
	 *
	 * @return the broad cast msg
	 */
	public String getFormatBroadCastMsg() {
		return getData().getString("Format.BroadcastMsg",
				"&6[&4Broadcast&6] &2Thanks &c%player% &2for voting on %SiteName%");

	}

	/**
	 * Gets the broadcast when online.
	 *
	 * @return the broadcast when online
	 */
	public boolean getFormatBroadcastWhenOnline() {
		return getData().getBoolean("Format.BroadcastWhenOnline");
	}

	/**
	 * Gets the commands vote auto input sites.
	 *
	 * @return the commands vote auto input sites
	 */
	public boolean getFormatCommandsVoteAutoInputSites() {
		return getData().getBoolean("Format.Commands.Vote.AutoInputSites");
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatCommandsVoteBestLines() {
		return (ArrayList<String>) getData().getList("Format.Commands.Vote.Best.Lines", new ArrayList<String>());
	}

	public String getFormatCommandsVoteBestTitle() {
		return getData().getString("Format.Commands.Vote.Best.Title", "&3&l%player% Best Votes");
	}

	/**
	 * Gets the commands vote help line.
	 *
	 * @return the commands vote help line
	 */
	public String getFormatCommandsVoteHelpLine() {
		return getData().getString("Format.Commands.Vote.Help.Line", "&3&l%Command% - &3%HelpMessage%");
	}

	/**
	 * Gets the commands vote help require permission.
	 *
	 * @return the commands vote help require permission
	 */
	public boolean getFormatCommandsVoteHelpRequirePermission() {
		return getData().getBoolean("Format.Commands.Vote.Help.RequirePermission");
	}

	/**
	 * Gets the commands vote help title.
	 *
	 * @return the commands vote help title
	 */
	public String getFormatCommandsVoteHelpTitle() {
		return getData().getString("Format.Commands.Vote.Help.Title", "Voting Player Help");

	}

	/**
	 * Gets the commands vote last line.
	 *
	 * @return the commands vote last line
	 */
	public String getFormatCommandsVoteLastLine() {
		return getData().getString("Format.Commands.Vote.Last.Line", "&3%SiteName%: &6%time%");
	}

	/**
	 * Gets the commands vote last title.
	 *
	 * @return the commands vote last title
	 */
	public String getFormatCommandsVoteLastTitle() {
		return getData().getString("Format.Commands.Vote.Last.Title", "&3&l%player% Last Vote Times:");

	}

	/**
	 * Gets the commands vote next info can vote.
	 *
	 * @return the commands vote next info can vote
	 */
	public String getFormatCommandsVoteNextInfoCanVote() {
		return getData().getString("Format.Commands.Vote.Next.Info.CanVote", "Go Vote!");
	}

	/**
	 * Gets the commands vote next info error.
	 *
	 * @return the commands vote next info error
	 */
	public String getFormatCommandsVoteNextInfoError() {
		return getData().getString("Format.Commands.Vote.Next.Info.Error", "");
	}

	/**
	 * Gets the commands vote next info time.
	 *
	 * @return the commands vote next info time
	 */
	public String getFormatCommandsVoteNextInfoTime() {
		return getData().getString("Format.Commands.Vote.Next.Info.TimeUntilVote",
				"&cCould not caculate time until next vote!");
	}

	public String getFormatCommandsVoteNextInfoVoteDelayDaily() {
		return getData().getString("Format.Commands.Vote.Next.Info.VoteDelayDaily",
				"%hours% Hours and %minutes% Minutes");
	}

	/**
	 * Gets the commands vote next layout.
	 *
	 * @return the commands vote next layout
	 */
	public String getFormatCommandsVoteNextLayout() {
		return getData().getString("Format.Commands.Vote.Next.Layout", "&3%SiteName%: &6%info%");
	}

	/**
	 * Gets the commands vote next title.
	 *
	 * @return the commands vote next title
	 */
	public String getFormatCommandsVoteNextTitle() {
		return getData().getString("Format.Commands.Vote.Next.Title", "&3&l%player% Next Votes:");

	}

	/**
	 * Gets the commands vote party.
	 *
	 * @return the commands vote party
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatCommandsVoteParty() {
		ArrayList<String> msg = new ArrayList<String>();
		msg.add("&cCurrently at &6%Votes%&c, &6%NeededVotes% &cmore votes to go to reach &6%VotesRequired%");
		return (ArrayList<String>) getData().getList("Format.Commands.Vote.Party", msg);

	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatCommandsVoteStreakLines() {
		return (ArrayList<String>) getData().getList("Format.Commands.Vote.Streak.Lines", new ArrayList<String>());
	}

	public String getFormatCommandsVoteStreakTitle() {
		return getData().getString("Format.Commands.Vote.Streak.Title", "&3&l%player% Vote Streak");
	}

	/**
	 * Gets the commands vote text.
	 *
	 * @return the commands vote text
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatCommandsVoteText() {
		ArrayList<String> str = new ArrayList<String>();
		str.add("&4&lVote for our server!");
		return (ArrayList<String>) getData().getList("Format.Commands.Vote.Text", str);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatCommandsVoteTotal() {
		ArrayList<String> list = (ArrayList<String>) getData().getList("Format.Commands.Vote.Total",
				new ArrayList<String>());
		if (list.isEmpty()) {
			list.add("&3&l%player% Total Votes:");
			list.add("&3&lDaily Total: &6&l%DailyTotal%");
			list.add("&3&lWeekly Total: &6&l%WeeklyTotal%");
			list.add("&3&lMonthly Total: &6&l%MonthlyTotal%");
			list.add("&3&lAllTime Total: &6&l%AllTimeTotal%");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatCommandsVoteTotalAll() {

		ArrayList<String> list = (ArrayList<String>) getData().getList("Format.Commands.Vote.TotalAll",
				new ArrayList<String>());
		if (list.isEmpty()) {
			list.add("&3&lServer Total Votes:");
			list.add("&3&lDaily Total: &6&l%DailyTotal%");
			list.add("&3&lWeekly Total: &6&l%WeeklyTotal%");
			list.add("&3&lMonthly Total: &6&l%MonthlyTotal%");
			list.add("&3&lAllTime Total: &6&l%AllTimeTotal%");
		}
		return list;
	}

	/**
	 * Gets the commands vote URLS.
	 *
	 * @return the commands vote URLS
	 */
	public String getFormatCommandsVoteURLS() {
		return getData().getString("Format.Commands.Vote.Sites", "&4%num%: &c&l%SiteName% - &c%url%");
	}

	/**
	 * Gets the command vote points.
	 *
	 * @return the command vote points
	 */
	public String getFormatCommandVotePoints() {
		return getData().getString("Format.Commands.Vote.Points", "&a%Player% currently has &a&l%Points%&a Points!");

	}

	/**
	 * Gets the command vote top line.
	 *
	 * @return the command vote top line
	 */
	public String getFormatCommandVoteTopLine() {
		return getData().getString("Format.Commands.Vote.Top.Line", "&c%num%: &6%player%, %votes%");
	}

	/**
	 * Gets the command vote top title.
	 *
	 * @return the command vote top title
	 */
	public String getFormatCommandVoteTopTitle() {
		return getData().getString("Format.Commands.Vote.Top.Title", "&3Top %Top% Voters %page%/%maxpages%");

	}

	/**
	 * Gets the format help line.
	 *
	 * @return the format help line
	 */
	public String getFormatHelpLine() {
		return getData().getString("Format.HelpLine", "&3&l%Command% - &3%HelpMessage%");
	}

	public String getFormatNextPage() {
		return getData().getString("Format.NextPage", "&aNext Page");
	}

	/**
	 * Gets the format no perms.
	 *
	 * @return the format no perms
	 */
	public String getFormatNoPerms() {
		return getData().getString("Format.NoPerms", "&cYou do not have enough permission!");
	}

	public String getFormatNotNumber() {
		return getData().getString("Format.NotNumber", "&cError on &6%arg%&c, number expected!");
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public int getFormatPageSize() {
		return 10;
	}

	public String getFormatPrevPage() {
		return getData().getString("Format.PrevPage", "&aPrevious Page");
	}

	public String getFormatShopFailedMsg() {
		String msg = getData().getString("Format.ShopFailed", "&cYou do not have %Points% points to purhcase this!");

		return msg;

	}

	public String getFormatShopPurchaseMsg() {
		return getData().getString("Format.ShopPurchase", "&aYou bought the %Identifier% for %Points% Points!");

	}

	/**
	 * Gets the sign top voter sign line 1.
	 *
	 * @return the sign top voter sign line 1
	 */
	public String getFormatSignTopVoterSignLine1() {
		return getData().getString("Format.Signs.TopVoterSign.Line1", "TopVoter: %SiteName%");

	}

	/**
	 * Gets the sign top voter sign line 2.
	 *
	 * @return the sign top voter sign line 2
	 */
	public String getFormatSignTopVoterSignLine2() {
		return getData().getString("Format.Signs.TopVoterSign.Line2", "#%position%");
	}

	/**
	 * Gets the sign top voter sign line 3.
	 *
	 * @return the sign top voter sign line 3
	 */
	public String getFormatSignTopVoterSignLine3() {
		return getData().getString("Format.Signs.TopVoterSign.Line3", "%player%");

	}

	/**
	 * Gets the sign top voter sign line 4.
	 *
	 * @return the sign top voter sign line 4
	 */
	public String getFormatSignTopVoterSignLine4() {
		return getData().getString("Format.Signs.TopVoterSign.Line4", "%votes% Votes");

	}

	/**
	 * Gets the sign top voter sign right click message.
	 *
	 * @return the sign top voter sign right click message
	 */
	public String getFormatSignTopVoterSignRightClickMessage() {
		return getData().getString("Format.Signs.RightClickMessage",
				"&c&l%player% &cis &c&l%position% &cwith &c&l%votes% &cin &c&l%SiteName%");

	}

	/**
	 * Gets the time format.
	 *
	 * @return the time format
	 */
	public String getFormatTimeFormat() {
		return getData().getString("Format.TimeFormat", "EEE, d MMM yyyy HH:mm");

	}

	public String getFormatTopVoterAllTime() {
		return getData().getString("Format.TopVoter.AllTime", "AllTime");
	}

	public String getFormatTopVoterDaily() {
		return getData().getString("Format.TopVoter.Daily", "Daily");
	}

	public String getFormatTopVoterMonthly() {
		return getData().getString("Format.TopVoter.Monthly", "Monthly");
	}

	/**
	 * Gets the top voter reward msg.
	 *
	 * @return the top voter reward msg
	 */
	public String getFormatTopVoterRewardMsg() {
		return getData().getString("Format.TopVoterAwardMsg",
				"&aYou came in %place% in top voters of the month! Here is an award!");

	}

	public String getFormatTopVoterWeekly() {
		return getData().getString("Format.TopVoter.Weekly", "Weekly");
	}

	/**
	 * Gets the vote help.
	 *
	 * @return the vote help
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getFormatVoteHelp() {
		return (ArrayList<String>) getData().getList("Format.Commands.Vote.Help.Lines", new ArrayList<String>());
	}

	public ConfigurationSection getGUIVoteBestDayBestItem() {
		return getData().getConfigurationSection("GUI.VoteBest.DayBest.Item");
	}

	public ConfigurationSection getGUIVoteBestMonthBestItem() {
		return getData().getConfigurationSection("GUI.VoteBest.MonthBest.Item");
	}

	public String getGUIVoteBestName() {
		return getData().getString("GUI.VoteBest.Name", "VoteBest: %player%");
	}

	public ConfigurationSection getGUIVoteBestWeekBestItem() {
		return getData().getConfigurationSection("GUI.VoteBest.WeekBest.Item");
	}

	public String getGUIVoteGUIName() {
		return getData().getString("GUI.VoteGUIName", "&cVoteGUI: &c&l%player%");
	}

	public boolean getGUIVoteLastBackButton() {
		return getData().getBoolean("GUI.VoteLast.BackButton");
	}

	public String getGUIVoteLastName() {
		return getData().getString("GUI.VoteLast.Name", "VoteLast: %player%");
	}

	public boolean getGUIVoteNextBackButton() {
		return getData().getBoolean("GUI.VoteNext.BackButton");
	}

	public String getGUIVoteNextName() {
		return getData().getString("GUI.VoteNext.Name", "VoteNext: %player%");
	}

	public String getGUIVoteRewardName() {
		return getData().getString("GUI.VoteRewardName", "VoteReward");
	}

	public boolean getGUIVoteStreakBackButton() {
		return getData().getBoolean("GUI.VoteStreak.BackButton");
	}

	public ConfigurationSection getGUIVoteStreakCurrentDayStreakItem() {
		return getData().getConfigurationSection("GUI.VoteStreak.CurrentDayStreak.Item");
	}

	public ConfigurationSection getGUIVoteStreakCurrentMonthStreakItem() {
		return getData().getConfigurationSection("GUI.VoteStreak.CurrentMonthStreak.Item");
	}

	public ConfigurationSection getGUIVoteStreakCurrentWeekStreakItem() {
		return getData().getConfigurationSection("GUI.VoteStreak.CurrentWeekStreak.Item");
	}

	public ConfigurationSection getGUIVoteStreakHighestDayStreakItem() {
		return getData().getConfigurationSection("GUI.VoteStreak.HighestDayStreak.Item");
	}

	public ConfigurationSection getGUIVoteStreakHighestMonthStreakItem() {
		return getData().getConfigurationSection("GUI.VoteStreak.HighestMonthStreak.Item");
	}

	public ConfigurationSection getGUIVoteStreakHighestWeekStreakItem() {
		return getData().getConfigurationSection("GUI.VoteStreak.HighestWeekStreak.Item");
	}

	public String getGUIVoteStreakName() {
		return getData().getString("GUI.VoteStreak.Name", "VoteStreak");
	}

	public boolean getGUIVoteTodayBackButton() {
		return getData().getBoolean("GUI.VoteToday.BackButton");
	}

	public String getGUIVoteTodayName() {
		return getData().getString("GUI.VoteToday.Name", "VoteToday");
	}

	public boolean getGUIVoteTopBackButton() {
		return getData().getBoolean("GUI.VoteToday.BackButton");
	}

	public String getGUIVoteTopItemLore() {
		return getData().getString("GUI.VoteTop.Item.Lore", "&3&lVotes: &3%votes%");
	}

	public String getGUIVoteTopItemName() {
		return getData().getString("GUI.VoteTop.Item.Name", "&3&l%position%: &3%player%");
	}

	public String getGUIVoteTopName() {
		return getData().getString("GUI.VoteTop.Name", "VoteTop %topvoter%");
	}

	public int getGUIVoteTopSize() {
		return getData().getInt("GUI.VoteTop.Size", 27);
	}

	public ConfigurationSection getGUIVoteTopSwitchItem() {
		return getData().getConfigurationSection("GUI.VoteTop.SwitchItem");
	}

	public ConfigurationSection getGUIVoteTotalAllTimeTotalItem() {
		return getData().getConfigurationSection("GUI.VoteTotal.AllTimeTotal.Item");
	}

	public boolean getGUIVoteTotalBackButton() {
		return getData().getBoolean("GUI.VoteTotal.BackButton");
	}

	public ConfigurationSection getGUIVoteTotalDayTotalItem() {
		return getData().getConfigurationSection("GUI.VoteTotal.DayTotal.Item");
	}

	public ConfigurationSection getGUIVoteTotalMonthTotalItem() {
		return getData().getConfigurationSection("GUI.VoteTotal.MonthTotal.Item");
	}

	public String getGUIVoteTotalName() {
		return getData().getString("GUI.VoteTotal.Name", "VoteTotal: %player%");
	}

	public ConfigurationSection getGUIVoteTotalWeekTotalItem() {
		return getData().getConfigurationSection("GUI.VoteTotal.WeekTotal.Item");
	}

	public boolean getGUIVoteURLBackButton() {
		return getData().getBoolean("GUI.VoteURL.BackButton");
	}

	public String getGUIVoteURLName() {
		return getData().getString("GUI.VoteURL.Name", "&cVoteURL");
	}

	public String getGUIVoteURLURLText() {
		return getData().getString("GUI.VoteURL.URLText", "%VoteUrl%");
	}

	public String getGUIVoteURLSiteName() {
		return getData().getString("GUI.VoteURLSite.Name", "VoteSite %site%");
	}

	public int getIdentifierCost(String identifier) {
		return getData().getInt("Shop." + identifier + ".Cost");
	}

	public String getIdentifierFromSlot(int slot) {
		for (String identifier : getIdentifiers()) {
			if (getIdentifierSlot(identifier) == slot) {
				return identifier;
			}
		}
		return null;
	}

	public int getIdentifierItemAmount(String identifier) {
		return getData().getInt("Shop." + identifier + ".Item.Amount");
	}

	public String getIdentifierRewardsPath(String identifier) {
		return "Shop." + identifier + ".Rewards";

	}

	public Set<String> getIdentifiers() {
		return getData().getConfigurationSection("Shop").getKeys(false);
	}

	public ConfigurationSection getIdentifierSection(String identifier) {
		return getData().getConfigurationSection("Shop." + identifier);
	}

	public int getIdentifierSlot(String identifier) {
		return getData().getInt("Shop." + identifier + ".Slot");
	}

	public boolean getLoadTopVoterAllTime() {
		return getData().getBoolean("LoadTopVoter.AllTime", true);
	}

	public boolean getLoadTopVoterDaily() {
		return getData().getBoolean("LoadTopVoter.Daily");
	}

	public boolean getLoadTopVoterMonthly() {
		return getData().getBoolean("LoadTopVoter.Monthly", true);
	}

	public boolean getLoadTopVoterWeekly() {
		return getData().getBoolean("LoadTopVoter.Weekly");
	}

	/**
	 * Gets the log debug to file.
	 *
	 * @return the log debug to file
	 */
	public boolean getLogDebugToFile() {
		return getData().getBoolean("LogDebugToFile", true);
	}

	/**
	 * Gets the log votes to file.
	 *
	 * @return the log votes to file
	 */
	public boolean getLogVotesToFile() {
		return getData().getBoolean("LogVotesToFile");
	}

	/**
	 * Gets the milestone reward enabled.
	 *
	 * @param milestones
	 *            the milestones
	 * @return the milestone reward enabled
	 */
	public boolean getMilestoneRewardEnabled(int milestones) {
		return getData().getBoolean("MileStones." + milestones + ".Enabled");
	}

	/**
	 * Gets the milestone rewards.
	 *
	 * @param milestones
	 *            the milestones
	 * @return the milestone rewards
	 */
	public String getMilestoneRewardsPath(int milestones) {
		return "MileStones." + milestones + ".Rewards";
	}

	/**
	 * Gets the milestone votes.
	 *
	 * @return the milestone votes
	 */
	public Set<String> getMilestoneVotes() {
		try {
			Set<String> set = getData().getConfigurationSection("MileStones").getKeys(false);
			if (set != null) {
				return set;
			}
			return new HashSet<String>();
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	/**
	 * Gets the monthly award rewards.
	 *
	 * @param pos
	 *            the pos
	 * @return the monthly award rewards
	 */
	public String getMonthlyAwardRewardsPath(int pos) {
		return "MonthlyAwards." + pos + ".Rewards";
	}

	/**
	 * Gets the monthly awards enabled.
	 *
	 * @return the monthly awards enabled
	 */
	public boolean getMonthlyAwardsEnabled() {
		return getData().getBoolean("EnableMonthlyAwards");
	}

	/**
	 * Gets the monthly possible reward places.
	 *
	 * @return the monthly possible reward places
	 */
	public Set<String> getMonthlyPossibleRewardPlaces() {
		try {
			return getData().getConfigurationSection("MonthlyAwards").getKeys(false);
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	public ConfigurationSection getMySql() {
		return getData().getConfigurationSection("MySQL");
	}

	public int getPointsOnVote() {
		return getData().getInt("PointsOnVote", 1);
	}

	/**
	 * Gets the request API default method.
	 *
	 * @return the request API default method
	 */
	public String getRequestAPIDefaultMethod() {
		return getData().getString("RequestAPI.DefaultMethod", "Anvil");
	}

	/**
	 * Gets the request API disabled methods.
	 *
	 * @return the request API disabled methods
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getRequestAPIDisabledMethods() {
		return (ArrayList<String>) getData().getList("RequestAPI.DisabledMethods", new ArrayList<String>());
	}

	public boolean getResetMilestonesMonthly() {
		return getData().getBoolean("ResetMilestonesMonthly");
	}

	/**
	 * Gets the send scoreboards.
	 *
	 * @return the send scoreboards
	 */
	public boolean getSendScoreboards() {
		return getData().getBoolean("SendScoreboards");
	}

	/**
	 * Gets the store top voters daily.
	 *
	 * @return the store top voters daily
	 */
	public boolean getStoreTopVotersDaily() {
		return getData().getBoolean("StoreTopVoters.Daily");
	}

	/**
	 * Gets the store top voters monthly.
	 *
	 * @return the store top voters monthly
	 */
	public boolean getStoreTopVotersMonthly() {
		return getData().getBoolean("StoreTopVoters.Monthly");
	}

	/**
	 * Gets the store top voters weekly.
	 *
	 * @return the store top voters weekly
	 */
	public boolean getStoreTopVotersWeekly() {
		return getData().getBoolean("StoreTopVoters.Weekly");
	}

	public boolean getTopVoterIgnorePermission() {
		return getData().getBoolean("TopVoterIgnorePermission");
	}

	public int getUserVotesRequired() {
		return getData().getInt("VoteParty.UserVotesRequired");
	}

	/**
	 * Gets the vote GUI slot command.
	 *
	 * @param slot
	 *            the slot
	 * @return the vote GUI slot command
	 */

	public String getVoteGUISlotCommand(String slot) {
		return getData().getString("GUI.VoteGUI." + slot + ".Command", "");
	}

	/**
	 * Gets the vote GUI slot lore.
	 *
	 * @param slot
	 *            the slot
	 * @return the vote GUI slot lore
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getVoteGUISlotLore(String slot) {
		return (ArrayList<String>) getData().getList("GUI.VoteGUI." + slot + ".Lore", new ArrayList<String>());
	}

	/**
	 * Gets the vote GUI slots.
	 *
	 * @return the vote GUI slots
	 */
	public Set<String> getVoteGUISlots() {
		try {
			return getData().getConfigurationSection("GUI.VoteGUI").getKeys(false);
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	public ConfigurationSection getVoteGUISlotSection(String slot) {
		return getData().getConfigurationSection("GUI.VoteGUI." + slot + ".Item");
	}

	/**
	 * Gets the vote GUI slot slot.
	 *
	 * @param slot
	 *            the slot
	 * @return the vote GUI slot slot
	 */
	public int getVoteGUISlotSlot(String slot) {
		return getData().getInt("GUI.VoteGUI." + slot + ".Slot");
	}

	public String getVotePartyBroadcast() {
		return getData().getString("VoteParty.Broadcast", "");
	}

	public boolean getVotePartyCountFakeVotes() {
		return getData().getBoolean("VoteParty.CountFakeVotes", true);
	}

	/**
	 * Gets the vote party enabled.
	 *
	 * @return the vote party enabled
	 */
	public boolean getVotePartyEnabled() {
		return getData().getBoolean("VoteParty.Enabled");
	}

	/**
	 * Gets the vote party give all players.
	 *
	 * @return the vote party give all players
	 */
	public boolean getVotePartyGiveAllPlayers() {
		return getData().getBoolean("VoteParty.GiveAllPlayers");
	}

	public boolean getVotePartyResetEachDay() {
		return getData().getBoolean("VoteParty.ResetEachDay");
	}

	public boolean getAddTotals() {
		return getData().getBoolean("AddTotals", true);
	}

	public boolean getVotePartyResetMontly() {
		return getData().getBoolean("VoteParty.ResetMonthly");
	}

	/**
	 * Gets the vote party rewards.
	 *
	 * @return the vote party rewards
	 */
	public String getVotePartyRewardsPath() {
		return "VoteParty.Rewards";
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getVotePartyCommands() {
		return (ArrayList<String>) getData().getList("VoteParty.Commands", new ArrayList<String>());
	}

	/**
	 * Gets the vote party votes required.
	 *
	 * @return the vote party votes required
	 */
	public int getVotePartyVotesRequired() {
		return getData().getInt("VoteParty.VotesRequired");
	}

	/**
	 * Gets the enabled.
	 *
	 * @return the enabled
	 */
	public boolean getVoteRemindingEnabled() {
		return getData().getBoolean("VoteReminding.Enabled");
	}

	/**
	 * Gets the remind delay.
	 *
	 * @return the remind delay
	 */
	public int getVoteRemindingRemindDelay() {
		return getData().getInt("VoteReminding.RemindDelay", 30);
	}

	/**
	 * Gets the remind on login.
	 *
	 * @return the remind on login
	 */
	public boolean getVoteRemindingRemindOnLogin() {
		return getData().getBoolean("VoteReminding.RemindOnLogin");
	}

	/**
	 * Gets the remind only once.
	 *
	 * @return the remind only once
	 */
	public boolean getVoteRemindingRemindOnlyOnce() {
		return getData().getBoolean("VoteReminding.RemindOnlyOnce");
	}

	/**
	 * Gets the rewards.
	 *
	 * @return the rewards
	 */
	public String getVoteRemindingRewardsPath() {
		return "VoteReminding.Rewards";
	}

	public boolean getVoteShopBackButton() {
		return getData().getBoolean("VoteShopBackButton", true);
	}

	public boolean getGiveDefaultPermission() {
		return getData().getBoolean("GiveDefaultPermission", true);
	}

	public String getVoteShopName() {
		return getData().getString("GUI.VoteShopName", "VoteShop");
	}

	public String getFormatUserNotExist() {
		return getData().getString("Format.UserNotExist", "&cUser does not exist: %player%");
	}

	public boolean getPurgeOldData() {
		return getData().getBoolean("PurgeOldData");
	}

	public int getPurgeMin() {
		return getData().getInt("PurgeMin", 90);
	}

	/**
	 * Gets the vote site items.
	 *
	 * @param site
	 *            the site
	 * @return the vote site items
	 */
	public Set<String> getVoteSiteItems(String site) {
		String siteName = site.replace(".", "-");
		try {
			return getData().getConfigurationSection("GUI.VoteReward." + siteName + ".Items").getKeys(false);
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	public ConfigurationSection getVoteSiteItemsSection(String site, String item) {
		String siteName = site.replace(".", "-");
		return getData().getConfigurationSection("GUI.VoteReward." + siteName + ".Items." + item);
	}

	/**
	 * Gets the vote site items slot.
	 *
	 * @param site
	 *            the site
	 * @param item
	 *            the item
	 * @return the vote site items slot
	 */
	public int getVoteSiteItemsSlot(String site, String item) {
		String siteName = site.replace(".", "-");
		return getData().getInt("GUI.VoteReward." + siteName + ".Items." + item + ".Slot");
	}

	/**
	 * Gets the votes required.
	 *
	 * @return the votes required
	 */
	public int getVotesRequired() {
		return getData().getInt("VotesRequired");
	}

	public boolean getVoteStreakRewardEnabled(String type, String s) {
		return getData().getBoolean("VoteStreak." + type + "." + s + ".Enabled");
	}

	public String getVoteStreakRewardsPath(String type, String string) {
		return "VoteStreak." + type + "." + string + ".Rewards";
	}

	public Set<String> getVoteStreakVotes(String type) {
		try {
			Set<String> set = getData().getConfigurationSection("VoteStreak." + type).getKeys(false);
			if (set != null) {
				return set;
			}
			return new HashSet<String>();
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	public String getVoteTopDefault() {
		return getData().getString("VoteTopDefault", "Monthly");
	}

	public ConfigurationSection getVoteURLAlreadyVotedAllUrlsButtonItemSection() {
		if (getData().isConfigurationSection("GUI.VoteURL.AllUrlsButton.AlreadyVotedItem")) {
			return getData().getConfigurationSection("GUI.VoteURL.AllUrlsButton.AlreadyVotedItem");
		} else {
			return getData().getConfigurationSection("GUI.VoteURL.AlreadyVotedItem");
		}
	}

	public ConfigurationSection getVoteURLAlreadyVotedItemSection() {
		return getData().getConfigurationSection("GUI.VoteURL.AlreadyVotedItem");
	}

	public ConfigurationSection getVoteURLCanVoteAllUrlsButtonItemSection() {
		if (getData().isConfigurationSection("GUI.VoteURL.AllUrlsButton.CanVoteItem")) {
			return getData().getConfigurationSection("GUI.VoteURL.AllUrlsButton.CanVoteItem");
		} else {
			return getData().getConfigurationSection("GUI.VoteURL.CanVoteItem");
		}
	}

	public ConfigurationSection getVoteURLCanVoteItemSection() {
		return getData().getConfigurationSection("GUI.VoteURL.CanVoteItem");
	}

	/**
	 * Gets the vote URL next vote.
	 *
	 * @return the vote URL next vote
	 */
	public String getVoteURLNextVote() {
		return getData().getString("GUI.VoteURL.NextVote", "&cCan Vote In: %Info%");

	}

	/**
	 * Gets the vote URL see URL.
	 *
	 * @return the vote URL see URL
	 */
	public String getVoteURLSeeURL() {
		return getData().getString("GUI.VoteURL.SeeURL", "&cClick to see URL");
	}

	/**
	 * Gets the vote URL site name.
	 *
	 * @return the vote URL site name
	 */
	public String getVoteURLSiteName() {
		return getData().getString("GUI.VoteURL.SiteName", "&c%Name%");
	}

	/**
	 * Gets the vote URL view all urls button enabled.
	 *
	 * @return the vote URL view all urls button enabled
	 */
	public boolean getVoteURLViewAllUrlsButtonEnabled() {
		return getData().getBoolean("GUI.VoteURL.ViewAllUrlsButtonEnabled");
	}

	/**
	 * Gets the weekly award rewards.
	 *
	 * @param pos
	 *            the pos
	 * @return the weekly award rewards
	 */
	public String getWeeklyAwardRewardsPath(int pos) {
		return "WeeklyAwards." + pos + ".Rewards";
	}

	/**
	 * Gets the weekly awards enabled.
	 *
	 * @return the weekly awards enabled
	 */
	public boolean getWeeklyAwardsEnabled() {
		return getData().getBoolean("EnableWeeklyAwards");
	}

	/**
	 * Gets the weekly possible reward places.
	 *
	 * @return the weekly possible reward places
	 */
	public Set<String> getWeeklyPossibleRewardPlaces() {
		try {
			return getData().getConfigurationSection("WeeklyAwards").getKeys(false);
		} catch (Exception ex) {
			return new HashSet<String>();
		}
	}

	@Override
	public void onFileCreation() {
		plugin.saveResource("Config.yml", true);
	}

	/**
	 * Sets the allow un joined.
	 *
	 * @param value
	 *            the new allow un joined
	 */
	public void setAllowUnJoined(boolean value) {
		getData().set("AllowUnjoined", value);
		saveData();
	}

	/**
	 * Sets the rewards.
	 *
	 * @param rewards
	 *            the new rewards
	 */
	public void setAnySiteRewards(ArrayList<String> rewards) {
		getData().set("AnySiteRewards", rewards);
		saveData();
	}

	/**
	 * Sets the broadcast vote enabled.
	 *
	 * @param value
	 *            the new broadcast vote enabled
	 */
	public void setBroadcastVoteEnabled(boolean value) {
		getData().set("BroadcastVote", value);
		saveData();
	}

	/**
	 * Sets the debug enabled.
	 *
	 * @param value
	 *            the new debug enabled
	 */
	public void setDebugEnabled(boolean value) {
		getData().set("Debug", value);
		saveData();
	}

	/**
	 * Sets the debug info ingame.
	 *
	 * @param value
	 *            the new debug info ingame
	 */
	public void setDebugInfoIngame(boolean value) {
		getData().set("DebugInfoIngame", value);
		saveData();
	}

	/**
	 * Sets the top voter awards enabled.
	 *
	 * @param value
	 *            the new top voter awards enabled
	 */
	public void setTopVoterAwardsEnabled(boolean value) {
		getData().set("TopVoterAwards", value);
		saveData();
	}

	public void setVoteRemindingEnabled(boolean value) {
		getData().set("VoteReminding.Enabled", value);
		saveData();
	}

	public void setVoteRemindingRemindDelay(int value) {
		getData().set("VoteReminding.RemindDelay", value);
		saveData();
	}

	public void setVoteRemindingRemindOnLogin(boolean value) {
		getData().set("VoteReminding.RemindOnLogin", value);
		saveData();
	}

	public void setVoteRemindingRemindOnlyOnce(boolean value) {
		getData().set("VoteReminding.RemindOnlyOnce", value);
		saveData();
	}

	public void setVoteRemindingRewards(ArrayList<String> value) {
		getData().set("VoteReminding.Rewards", value);
		saveData();
	}

}
