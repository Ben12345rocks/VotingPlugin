package com.bencodez.votingplugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.CodeSource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.bencodez.advancedcore.AdvancedCorePlugin;
import com.bencodez.advancedcore.api.command.CommandHandler;
import com.bencodez.advancedcore.api.inventory.editgui.EditGUIButton;
import com.bencodez.advancedcore.api.inventory.editgui.valuetypes.EditGUIValueNumber;
import com.bencodez.advancedcore.api.item.ItemBuilder;
import com.bencodez.advancedcore.api.javascript.JavascriptPlaceholderRequest;
import com.bencodez.advancedcore.api.messages.StringParser;
import com.bencodez.advancedcore.api.metrics.BStatsMetrics;
import com.bencodez.advancedcore.api.metrics.MCStatsMetrics;
import com.bencodez.advancedcore.api.misc.MiscUtils;
import com.bencodez.advancedcore.api.rewards.Reward;
import com.bencodez.advancedcore.api.rewards.RewardHandler;
import com.bencodez.advancedcore.api.rewards.RewardPlaceholderHandle;
import com.bencodez.advancedcore.api.rewards.injected.RewardInject;
import com.bencodez.advancedcore.api.rewards.injected.RewardInjectConfigurationSection;
import com.bencodez.advancedcore.api.rewards.injected.RewardInjectInt;
import com.bencodez.advancedcore.api.rewards.injected.RewardInjectValidator;
import com.bencodez.advancedcore.api.updater.Updater;
import com.bencodez.advancedcore.api.user.UUID;
import com.bencodez.advancedcore.api.user.UserStorage;
import com.bencodez.advancedcore.logger.Logger;
import com.bencodez.advancedcore.nms.NMSManager;
import com.bencodez.votingplugin.commands.CommandLoader;
import com.bencodez.votingplugin.commands.executers.CommandAdminVote;
import com.bencodez.votingplugin.commands.executers.CommandVote;
import com.bencodez.votingplugin.commands.gui.AdminGUI;
import com.bencodez.votingplugin.commands.tabcompleter.AdminVoteTabCompleter;
import com.bencodez.votingplugin.commands.tabcompleter.VoteTabCompleter;
import com.bencodez.votingplugin.config.BungeeSettings;
import com.bencodez.votingplugin.config.Config;
import com.bencodez.votingplugin.config.ConfigVoteSites;
import com.bencodez.votingplugin.config.GUI;
import com.bencodez.votingplugin.config.SpecialRewardsConfig;
import com.bencodez.votingplugin.cooldown.CoolDownCheck;
import com.bencodez.votingplugin.data.ServerData;
import com.bencodez.votingplugin.listeners.BlockBreak;
import com.bencodez.votingplugin.listeners.PlayerCommandSendListener;
import com.bencodez.votingplugin.listeners.PlayerInteract;
import com.bencodez.votingplugin.listeners.PlayerJoinEvent;
import com.bencodez.votingplugin.listeners.PlayerVoteListener;
import com.bencodez.votingplugin.listeners.SignChange;
import com.bencodez.votingplugin.listeners.VotiferEvent;
import com.bencodez.votingplugin.listeners.VotingPluginUpdateEvent;
import com.bencodez.votingplugin.objects.VoteSite;
import com.bencodez.votingplugin.placeholders.MVdWPlaceholders;
import com.bencodez.votingplugin.placeholders.PlaceHolders;
import com.bencodez.votingplugin.signs.SignHandler;
import com.bencodez.votingplugin.signs.Signs;
import com.bencodez.votingplugin.topvoter.TopVoter;
import com.bencodez.votingplugin.topvoter.TopVoterHandler;
import com.bencodez.votingplugin.updater.CheckUpdate;
import com.bencodez.votingplugin.user.VotingPluginUser;
import com.bencodez.votingplugin.user.UserManager;
import com.bencodez.votingplugin.voteparty.VoteParty;
import com.bencodez.votingplugin.votereminding.VoteReminding;
import com.vexsoftware.votifier.Votifier;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class Main.
 */
public class VotingPluginMain extends AdvancedCorePlugin {

	/** The config. */
	public static Config config;

	/** The config vote sites. */
	public static ConfigVoteSites configVoteSites;

	/** The plugin. */
	public static VotingPluginMain plugin;

	@Getter
	private LinkedHashMap<TopVoter, LinkedHashMap<VotingPluginUser, Integer>> topVoter;

	@Getter
	private LinkedHashMap<VotingPluginUser, Integer> lastMonthTopVoter;

	@Getter
	@Setter
	private Updater updater;

	@Getter
	@Setter
	private ArrayList<CommandHandler> voteCommand;

	@Getter
	@Setter
	private ArrayList<CommandHandler> adminVoteCommand;

	@Getter
	private List<VoteSite> voteSites;

	@Getter
	private LinkedHashMap<VotingPluginUser, HashMap<VoteSite, LocalDateTime>> voteToday;

	@Getter
	@Setter
	private ArrayList<SignHandler> signs;

	@Getter
	private Logger voteLog;

	@Getter
	@Setter
	private boolean update = true;

	@Getter
	private boolean updateStarted = false;

	@Getter
	private boolean ymlError = false;

	private boolean votifierLoaded = true;

	@Getter
	private LinkedHashMap<java.util.UUID, ArrayList<String>> advancedTab = new LinkedHashMap<java.util.UUID, ArrayList<String>>();

	@Getter
	private SpecialRewardsConfig specialRewardsConfig;

	@Getter
	private BungeeSettings bungeeSettings;

	public void basicBungeeUpdate() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			VotingPluginUser user = UserManager.getInstance().getVotingPluginUser(player);
			user.clearCache();
			user.offVote();
			user.checkOfflineRewards();
		}
	}

	/**
	 * Check votifier.
	 */
	private void checkVotifier() {
		try {
			Class.forName("com.vexsoftware.votifier.model.VotifierEvent");
		} catch (ClassNotFoundException e) {
			if (!BungeeSettings.getInstance().isUseBungeecoord()) {
				plugin.getLogger()
						.warning("No VotifierEvent found, install Votifier, NuVotifier, or another Votifier plugin");
			} else {
				plugin.debug("No VotifierEvent found, but usebungeecoord enabled");
			}
			votifierLoaded = false;
		}
	}

	private boolean checkVotifierLoaded() {
		try {
			Class.forName("com.vexsoftware.votifier.Votifier");
			if (Votifier.getInstance().getVoteReceiver() == null) {
				return false;
			}
		} catch (ClassNotFoundException e) {
			debug("Using NuVotiifer?");
		}
		return true;
	}

	private void checkYMLError() {
		if (config.isFailedToRead() || configVoteSites.isFailedToRead()) {
			ymlError = true;
		} else if (!config.isFailedToRead() && !configVoteSites.isFailedToRead()) {
			ymlError = false;
		}

		if (config.isFailedToRead()) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

				@Override
				public void run() {
					plugin.getLogger().severe("Failed to load Config.yml");
				}
			}, 10);
		}

		if (configVoteSites.isFailedToRead()) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

				@Override
				public void run() {
					plugin.getLogger().severe("Failed to load VoteSites.yml");
				}

			}, 10);
		}
	}

	public void convertDataStorage(UserStorage from, UserStorage to) {
		if (from == null || to == null) {
			throw new RuntimeException("Invalid Storage Method");
		}
		UserStorage cur = getStorageType();
		getOptions().setStorageType(from);
		if (getStorageType().equals(UserStorage.MYSQL) && getMysql() != null) {
			getMysql().clearCache();
		}
		ArrayList<String> uuids = new ArrayList<String>(UserManager.getInstance().getAllUUIDs());

		while (uuids.size() > 0) {
			HashMap<VotingPluginUser, HashMap<String, String>> data = new HashMap<VotingPluginUser, HashMap<String, String>>();
			getOptions().setStorageType(from);
			loadUserAPI(getOptions().getStorageType());
			// setStorageType(to);

			if (getStorageType().equals(UserStorage.MYSQL) && getMysql() != null) {
				getMysql().clearCache();
			}

			ArrayList<String> converted = new ArrayList<String>();
			int i = 0;
			while (i < Config.getInstance().getConvertAmount() && i < uuids.size()) {
				String uuid = uuids.get(i);
				try {
					VotingPluginUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));

					HashMap<String, String> values = new HashMap<String, String>();
					for (String key : user.getData().getKeys()) {
						String value = user.getData().getValue(key);
						if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("null")) {
							values.put(key, value);
						}
					}
					i++;
					converted.add(uuid);
					data.put(user, values);
					debug("[Convert] Added " + uuid);
				} catch (Exception e) {
					debug(e);
					plugin.getLogger().warning("Exception occoured for '" + uuid + "': " + e.getMessage()
							+ ", turn debug on to see full stack traces");
				}
			}

			try {
				wait(Config.getInstance().getConvertDelay());
			} catch (Exception e) {
			}

			uuids.removeAll(converted);

			plugin.getLogger().info("Finished getting data from " + from.toString() + " Converting " + data.size()
					+ " users, " + uuids.size() + " left to convert");

			getOptions().setStorageType(to);
			loadUserAPI(getOptions().getStorageType());
			if (getStorageType().equals(UserStorage.MYSQL) && getMysql() != null) {
				getMysql().clearCache();
			}

			writeConvertData(data);
		}

		getOptions().setStorageType(cur);
		reload();

		plugin.getLogger().info("Finished convertting");
	}

	public ArrayList<VotingPluginUser> convertSet(Set<VotingPluginUser> set) {
		return new ArrayList<VotingPluginUser>(set);
	}

	public LinkedHashMap<VotingPluginUser, Integer> getTopVoter(TopVoter top) {
		return topVoter.get(top);
	}

	/**
	 * Gets the user.
	 *
	 * @param uuid the uuid
	 * @return the user
	 */
	public VotingPluginUser getUser(UUID uuid) {
		return UserManager.getInstance().getVotingPluginUser(uuid);
	}

	public VoteParty getVoteParty() {
		return VoteParty.getInstance();
	}

	/**
	 * Gets the vote site.
	 *
	 * @param site the site name
	 * @return the vote site
	 */
	public VoteSite getVoteSite(String site) {
		String siteName = getVoteSiteName(site);
		for (VoteSite voteSite : getVoteSites()) {
			if (voteSite.getKey().equalsIgnoreCase(siteName) || voteSite.getDisplayName().equals(siteName)) {
				return voteSite;
			}
		}
		if (Config.getInstance().isAutoCreateVoteSites() && !configVoteSites.getVoteSitesNames().contains(siteName)) {
			configVoteSites.generateVoteSite(siteName);
			return new VoteSite(siteName.replace(".", "_"));
		}
		return null;

	}

	/**
	 * Gets the vote site name.
	 *
	 * @param urls the url
	 * @return the vote site name
	 */
	public String getVoteSiteName(String... urls) {
		ArrayList<String> sites = ConfigVoteSites.getInstance().getVoteSitesNames();
		for (String url : urls) {
			if (url == null) {
				return null;
			}
			if (sites != null) {
				for (String siteName : sites) {
					String URL = ConfigVoteSites.getInstance().getServiceSite(siteName);
					if (URL != null) {
						if (URL.equalsIgnoreCase(url)) {
							return siteName;
						}
					}
				}
				for (String siteName : sites) {
					if (siteName.equalsIgnoreCase(url)) {
						return siteName;
					}
				}
			}
			return url;
		}
		for (String url : urls) {
			return url;
		}
		return "";

	}

	public String getVoteSiteServiceSite(String name) {
		ArrayList<String> sites = ConfigVoteSites.getInstance().getVoteSitesNames();
		if (name == null) {
			return null;
		}
		if (sites != null) {
			for (String siteName : sites) {
				String URL = ConfigVoteSites.getInstance().getServiceSite(siteName);
				if (URL != null) {
					if (URL.equalsIgnoreCase(name)) {
						return URL;
					}
					if (name.equalsIgnoreCase(siteName)) {
						return URL;
					}
				}
			}
		}
		return name;

	}

	public UserManager getVotingPluginUserManager() {
		return UserManager.getInstance();
	}

	public boolean hasVoteSite(String site) {
		String siteName = getVoteSiteName(site);
		for (VoteSite voteSite : getVoteSites()) {
			if (voteSite.getKey().equalsIgnoreCase(siteName) || voteSite.getDisplayName().equals(siteName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isVoteSite(String voteSite) {
		for (VoteSite site : getVoteSites()) {
			if (site.getKey().equalsIgnoreCase(voteSite)) {
				return true;
			}
		}
		return false;
	}

	private void loadTimer() {
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				getTimer().schedule(new TimerTask() {

					@Override
					public void run() {
						if (plugin != null) {
							update();
						} else {
							cancel();
						}
					}
				}, 1000, 1000 * 60 * Config.getInstance().getDelayBetweenUpdates());

				getTimer().schedule(new TimerTask() {

					@Override
					public void run() {
						if (plugin != null) {
							CoolDownCheck.getInstance().checkAll();
						} else {
							cancel();
						}
					}
				}, 1000 * 60 * 10, 1000 * 60 * 5);

				getTimer().schedule(new TimerTask() {

					@Override
					public void run() {
						if (plugin != null && Config.getInstance().isExtraBackgroundUpdate()) {
							basicBungeeUpdate();
						} else {
							cancel();
						}
					}
				}, 1000, 1000 * 30);

			}
		}, 40L);

	}

	/**
	 * Load vote sites.
	 */
	public void loadVoteSites() {
		configVoteSites.setup();
		voteSites = Collections.synchronizedList(new ArrayList<VoteSite>());
		voteSites.addAll(configVoteSites.getVoteSitesLoad());

		if (voteSites.size() == 0) {
			plugin.getLogger().warning("Detected no voting sites, this may mean something isn't properly setup");
		}

		plugin.debug("Loaded VoteSites");

	}

	/**
	 * Log vote.
	 *
	 * @param date       the date
	 * @param playerName the player name
	 * @param voteSite   the vote site
	 */
	public void logVote(LocalDateTime date, String playerName, String voteSite) {
		if (Config.getInstance().isLogVotesToFile()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String str = formatter.format(date);
			voteLog.logToFile(str + ": " + playerName + " voted on " + voteSite);
		}
	}

	/**
	 * Metrics.
	 */
	private void metrics() {
		try {
			MCStatsMetrics metrics = new MCStatsMetrics(this);
			metrics.start();
			plugin.debug("Loaded Metrics");
		} catch (IOException e) {
			plugin.getLogger().info("Can't submit metrics stats");
		}

		BStatsMetrics metrics = new BStatsMetrics(this);

		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_firstvote") {

			@Override
			public String getValue() {
				if (RewardHandler.getInstance().hasRewards(SpecialRewardsConfig.getInstance().getData(),
						SpecialRewardsConfig.getInstance().getFirstVoteRewardsPath())) {
					return "True";
				} else {
					return "False";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_everysite") {

			@Override
			public String getValue() {
				if (RewardHandler.getInstance().hasRewards(ConfigVoteSites.getInstance().getData(),
						ConfigVoteSites.getInstance().getEverySiteRewardPath())) {
					return "True";
				} else {
					return "False";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_allsites") {

			@Override
			public String getValue() {
				if (RewardHandler.getInstance().hasRewards(SpecialRewardsConfig.getInstance().getData(),
						SpecialRewardsConfig.getInstance().getAllSitesRewardPath())) {
					return "True";
				} else {
					return "False";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_cumulative") {

			@Override
			public String getValue() {
				if (SpecialRewardsConfig.getInstance().getCumulativeVotes().size() == 0) {
					return "False";
				} else {
					for (String cum : SpecialRewardsConfig.getInstance().getCumulativeVotes()) {
						if (SpecialRewardsConfig.getInstance().getCumulativeRewardEnabled(Integer.parseInt(cum))) {
							return "True";
						}
					}
					return "False";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_voteparty") {

			@Override
			public String getValue() {
				if (!SpecialRewardsConfig.getInstance().getVotePartyEnabled()) {
					return "False";
				} else {
					return "True";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_milestone") {

			@Override
			public String getValue() {
				if (SpecialRewardsConfig.getInstance().getMilestoneVotes().size() == 0) {
					return "False";
				} else {
					for (String milestone : SpecialRewardsConfig.getInstance().getMilestoneVotes()) {
						if (SpecialRewardsConfig.getInstance().getMilestoneRewardEnabled(Integer.parseInt(milestone))) {
							return "True";
						}
					}
					return "False";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_anysitereward") {

			@Override
			public String getValue() {
				if (RewardHandler.getInstance().hasRewards(SpecialRewardsConfig.getInstance().getData(),
						SpecialRewardsConfig.getInstance().getAnySiteRewardsPath())) {
					return "True";
				} else {
					return "False";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_votestreakday") {

			@Override
			public String getValue() {
				for (String s : SpecialRewardsConfig.getInstance().getVoteStreakVotes("Day")) {

					if (SpecialRewardsConfig.getInstance().getVoteStreakRewardEnabled("Day", s)
							&& RewardHandler.getInstance().hasRewards(SpecialRewardsConfig.getInstance().getData(),
									SpecialRewardsConfig.getInstance().getVoteStreakRewardsPath("Day", s))) {
						return "True";
					}

				}
				return "False";
			}
		});

		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_votestreakweek") {

			@Override
			public String getValue() {
				for (String s : SpecialRewardsConfig.getInstance().getVoteStreakVotes("Week")) {

					if (SpecialRewardsConfig.getInstance().getVoteStreakRewardEnabled("Week", s)
							&& RewardHandler.getInstance().hasRewards(SpecialRewardsConfig.getInstance().getData(),
									SpecialRewardsConfig.getInstance().getVoteStreakRewardsPath("Week", s))) {
						return "True";
					}
				}

				return "False";
			}
		});

		metrics.addCustomChart(new BStatsMetrics.SimplePie("extrarewards_votestreakmonth") {

			@Override
			public String getValue() {
				for (String s : SpecialRewardsConfig.getInstance().getVoteStreakVotes("Month")) {

					if (SpecialRewardsConfig.getInstance().getVoteStreakRewardEnabled("Month", s)
							&& RewardHandler.getInstance().hasRewards(SpecialRewardsConfig.getInstance().getData(),
									SpecialRewardsConfig.getInstance().getVoteStreakRewardsPath("Month", s))) {
						return "True";
					}
				}

				return "False";
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("numberofsites") {

			@Override
			public String getValue() {
				return "" + plugin.voteSites.size();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("numberofrewards") {

			@Override
			public String getValue() {
				return "" + RewardHandler.getInstance().getRewards().size();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("autocreatevotesites") {

			@Override
			public String getValue() {
				return "" + Config.getInstance().isAutoCreateVoteSites();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("BungeeMethod") {

			@Override
			public String getValue() {
				if (BungeeSettings.getInstance().isUseBungeecoord()) {
					return "" + BungeeHandler.getInstance().getMethod().toString();
				} else {
					return "Disabled";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("numberofusers") {

			@Override
			public String getValue() {
				int total = UserManager.getInstance().getAllUUIDs().size();
				if (total < 1000) {
					return "<1000";
				} else if (total > 10000) {
					return ">10000";
				} else {
					return "1000-10000";
				}
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("data_storage") {

			@Override
			public String getValue() {
				return getOptions().getStorageType().toString();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("DisableCheckOnWorldChange") {

			@Override
			public String getValue() {
				return "" + getOptions().isDisableCheckOnWorldChange();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("votereminding_enabled") {

			@Override
			public String getValue() {
				return "" + Config.getInstance().getVoteRemindingEnabled();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("LoadTopVoter_Monthly") {

			@Override
			public String getValue() {
				return "" + Config.getInstance().getLoadTopVoterMonthly();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("LoadTopVoter_Weekly") {

			@Override
			public String getValue() {
				return "" + Config.getInstance().getLoadTopVoterWeekly();
			}
		});
		metrics.addCustomChart(new BStatsMetrics.SimplePie("LoadTopVoter_Daily") {

			@Override
			public String getValue() {
				return "" + Config.getInstance().getLoadTopVoterDaily();
			}
		});
	}

	private YamlConfiguration getVersionFile() {
		try {
			CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
			if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream zip = null;
				zip = new ZipInputStream(jar.openStream());
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e != null) {
						String name = e.getName();
						if (name.equals("votingpluginversion.yml")) {
							Reader defConfigStream = new InputStreamReader(zip);
							if (defConfigStream != null) {
								YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
								defConfigStream.close();
								return defConfig;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Getter
	private String time = "";
	@Getter
	private String profile = "";

	private void loadVersionFile() {
		YamlConfiguration conf = getVersionFile();
		if (conf != null) {
			time = conf.getString("time", "");
			profile = conf.getString("profile", "");
		}
	}

	@Override
	public void onPostLoad() {
		loadVersionFile();
		getOptions().setServer(BungeeSettings.getInstance().getServer());
		if (BungeeSettings.getInstance().isUseBungeecoord()) {
			BungeeHandler.getInstance().load();

			if (getOptions().getServer().equalsIgnoreCase("PleaseSet")) {
				getLogger()
						.warning("Bungeecoord is true and server name is not set, bungeecoord features may not work");
			}

		}

		registerCommands();
		checkVotifier();
		registerEvents();
		CheckUpdate.getInstance().startUp();
		VoteReminding.getInstance().loadRemindChecking();
		plugin.signs = new ArrayList<SignHandler>();

		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run() {
				Signs.getInstance().loadSigns();
			}
		});

		lastMonthTopVoter = new LinkedHashMap<VotingPluginUser, Integer>();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				TopVoterHandler.getInstance().loadLastMonth();
				debug("Loaded last month top voters");
			}
		});
		topVoter = new LinkedHashMap<TopVoter, LinkedHashMap<VotingPluginUser, Integer>>();
		for (TopVoter top : TopVoter.values()) {
			topVoter.put(top, new LinkedHashMap<VotingPluginUser, Integer>());
		}
		voteToday = new LinkedHashMap<VotingPluginUser, HashMap<VoteSite, LocalDateTime>>();
		voteLog = new Logger(plugin, new File(plugin.getDataFolder() + File.separator + "Log", "votelog.txt"));

		AdminGUI.getInstance().loadHook();

		// vote party
		if (SpecialRewardsConfig.getInstance().getVotePartyEnabled()) {
			VoteParty.getInstance().check();
		}
		VoteParty.getInstance().register();

		TopVoterHandler.getInstance().register();

		metrics();

		// javascript api
		getJavascriptEngineRequests().add(new JavascriptPlaceholderRequest("User") {

			@Override
			public Object getObject(OfflinePlayer player) {
				return getVotingPluginUserManager().getVotingPluginUser(player);
			}
		});
		getJavascriptEngine().put("VotingPluginHooks", VotingPluginHooks.getInstance());

		loadTimer();

		// placeholderapi loading
		PlaceHolders.getInstance().load();
		
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			MVdWPlaceholders.getInstance().loadMVdWPlaceholders();
		}

		// set columns
		if (getStorageType().equals(UserStorage.MYSQL) && Config.getInstance().isAlterColumns()) {
			getMysql().alterColumnType("TopVoterIgnore", "VARCHAR(5)");
			getMysql().alterColumnType("CheckWorld", "VARCHAR(5)");
			getMysql().alterColumnType("Reminded", "VARCHAR(5)");
			getMysql().alterColumnType("DisableBroadcast", "VARCHAR(5)");
			getMysql().alterColumnType("LastOnline", "VARCHAR(20)");
			getMysql().alterColumnType("PlayerName", "VARCHAR(30)");
			getMysql().alterColumnType("DailyTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("WeeklyTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("DayVoteStreak", "INT DEFAULT '0'");
			getMysql().alterColumnType("BestDayVoteStreak", "INT DEFAULT '0'");
			getMysql().alterColumnType("WeekVoteStreak", "INT DEFAULT '0'");
			getMysql().alterColumnType("BestWeekVoteStreak", "INT DEFAULT '0'");
			getMysql().alterColumnType("VotePartyVotes", "INT DEFAULT '0'");
			getMysql().alterColumnType("MonthVoteStreak", "INT DEFAULT '0'");
			getMysql().alterColumnType("Points", "INT DEFAULT '0'");
			getMysql().alterColumnType("HighestDailyTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("AllTimeTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("HighestMonthlyTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("MilestoneCount", "INT DEFAULT '0'");
			getMysql().alterColumnType("MonthTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("HighestWeeklyTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("LastMonthTotal", "INT DEFAULT '0'");
			getMysql().alterColumnType("OfflineRewards", "MEDIUMTEXT");
		}

		// Add rewards
		RewardHandler.getInstance().addInjectedReward(new RewardInjectInt("Points", 0) {

			@Override
			public String onRewardRequest(Reward reward, com.bencodez.advancedcore.api.user.AdvancedCoreUser user, int num,
					HashMap<String, String> placeholders) {
				UserManager.getInstance().getVotingPluginUser(user).addPoints(num);
				return null;
			}
		}.synchronize().addEditButton(
				new EditGUIButton(new ItemBuilder(Material.PAPER), new EditGUIValueNumber("Points", null) {

					@Override
					public void setValue(Player player, Number value) {
						Reward reward = (Reward) getInv().getData("Reward");
						reward.getConfig().set("Points", value.intValue());
					}
				})).validator(new RewardInjectValidator() {

					@Override
					public void onValidate(Reward reward, RewardInject inject, ConfigurationSection data) {
						if (data.getInt(inject.getPath(), -1) == 0) {
							warning(reward, inject, "Points can not be 0");
						}
					}
				}));

		RewardHandler.getInstance().addInjectedReward(new RewardInjectConfigurationSection("VoteBossBar") {

			@Override
			public String onRewardRequested(Reward arg0, com.bencodez.advancedcore.api.user.AdvancedCoreUser user,
					ConfigurationSection section, HashMap<String, String> placeholders) {
				if (section.getBoolean("Enabled")) {
					user.sendBossBar(
							StringParser.getInstance().replacePlaceHolder(section.getString("Message", ""),
									placeholders),
							section.getString("Color", "BLUE"), section.getString("Style", "SOLID"),
							(double) UserManager.getInstance().getVotingPluginUser(user).getSitesVotedOn()
									/ plugin.getVoteSites().size(),
							section.getInt("Delay", 30));
				}
				return null;
			}
		});

		for (final TopVoter top : TopVoter.values()) {
			RewardHandler.getInstance().addPlaceholder(new RewardPlaceholderHandle("Total_" + top.toString()) {

				@Override
				public String getValue(Reward reward, com.bencodez.advancedcore.api.user.AdvancedCoreUser user) {
					VotingPluginUser vUser = UserManager.getInstance().getVotingPluginUser(user);
					return "" + vUser.getTotal(top);
				}
			});
		}

		plugin.getLogger().info("Enabled VotingPlugin " + plugin.getDescription().getVersion());
		if (getProfile().equals("dev")) {
			plugin.getLogger().warning("Using dev build, this is not a stable build, use at your own risk");
		}

		boolean hasRewards = RewardHandler.getInstance().hasRewards(ConfigVoteSites.getInstance().getData(),
				ConfigVoteSites.getInstance().getEverySiteRewardPath());

		boolean issues = true;
		ArrayList<String> services = ServerData.getInstance().getServiceSites();
		for (VoteSite site : getVoteSites()) {
			if (!site.hasRewards() && !hasRewards) {
				issues = false;
				plugin.getLogger().warning("No rewards detected for the site: " + site.getKey()
						+ ". See https://github.com/Ben12345rocks/AdvancedCore/wiki/Rewards on how to add rewards");
			}

			boolean contains = false;
			for (String service : services) {
				if (service.equalsIgnoreCase(site.getServiceSite())) {
					contains = true;
				}
			}
			if (!contains) {
				issues = false;
				plugin.getLogger().warning("No vote has been recieved from " + site.getServiceSite()
						+ ", may be an invalid service site. Vote on the site and look in console for a service site, if you get nothing then there is an issue with votifier");
			}
		}

		if (!issues) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

				@Override
				public void run() {
					plugin.getLogger().warning(
							"Detected an issue with voting sites, check the server startup log for more details");
				}
			}, 30l);
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				if (!checkVotifierLoaded()) {
					plugin.getLogger().warning("Detected votifier not loaded properly, check startup for details");
				}
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onPreLoad() {
		plugin = this;

		// disable plugin for older versions below 1.12

		if (NMSManager.getInstance().isVersion("1.7", "1.8", "1.9", "1.10", "1.11")) {
			plugin.getLogger().severe("Detected running " + Bukkit.getVersion()
					+ ", this version is not supported on this build, read the plugin page. Disabling...");
			if (!Config.getInstance().isOverrideVersionDisable()) {
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			} else {
				plugin.getLogger().warning("Overriding version disable, beware of using this! This may cause issues!");
			}
		}

		setupFiles();

		loadVoteSites();

		setJenkinsSite("ben12345rocks.com");
		updateAdvancedCoreHook();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onUnLoad() {
		Signs.getInstance().storeSigns();
		HandlerList.unregisterAll(plugin);
		if (BungeeSettings.getInstance().isUseBungeecoord()) {
			try {
				BungeeHandler.getInstance().close();
			} catch (Exception e) {
				debug(e);
			}
		}
		plugin = null;
	}

	/**
	 * Register commands.
	 */
	private void registerCommands() {
		CommandLoader.getInstance().loadCommands();
		CommandLoader.getInstance().loadAliases();

		// /vote, /v
		getCommand("vote").setExecutor(new CommandVote(this));
		getCommand("vote").setTabCompleter(new VoteTabCompleter());
		// getCommand("v").setExecutor(new CommandVote(this));
		// getCommand("v").setTabCompleter(new VoteTabCompleter());

		// /adminvote, /av
		getCommand("adminvote").setExecutor(new CommandAdminVote(this));
		getCommand("adminvote").setTabCompleter(new AdminVoteTabCompleter());
		getCommand("av").setExecutor(new CommandAdminVote(this));
		getCommand("av").setTabCompleter(new AdminVoteTabCompleter());

		Permission perm = Bukkit.getPluginManager().getPermission("VotingPlugin.Player");
		if (perm != null) {
			if (Config.getInstance().getGiveDefaultPermission()) {
				perm.setDefault(PermissionDefault.TRUE);
				getLogger().info("Giving VotingPlugin.Player permission by default, can be disabled in the config");
			} else {
				perm.setDefault(PermissionDefault.OP);
			}
		}

		plugin.debug("Loaded Commands");

	}

	/**
	 * Register events.
	 */
	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new PlayerJoinEvent(this), this);
		if (votifierLoaded) {
			pm.registerEvents(new VotiferEvent(this), this);
		}
		pm.registerEvents(new PlayerVoteListener(this), this);
		pm.registerEvents(new SignChange(this), this);
		pm.registerEvents(new BlockBreak(this), this);
		pm.registerEvents(new PlayerInteract(this), this);
		pm.registerEvents(new VotingPluginUpdateEvent(this), this);
		if (!NMSManager.getInstance().isVersion("1.12")) {
			pm.registerEvents(new PlayerCommandSendListener(this), this);
		}
		pm.registerEvents(new CoolDownCheck(this), this);

		plugin.debug("Loaded Events");

	}

	/**
	 * Reload.
	 */
	@Override
	public void reload() {
		reloadPlugin(false);
	}

	private void reloadPlugin(boolean userStorage) {
		setUpdate(true);

		config.reloadData();
		config.loadValues();

		configVoteSites.reloadData();

		specialRewardsConfig.reloadData();

		GUI.getInstance().reloadData();

		bungeeSettings.reloadData();
		checkYMLError();

		updateAdvancedCoreHook();
		plugin.loadVoteSites();
		reloadAdvancedCore(userStorage);
		getOptions().setServer(BungeeSettings.getInstance().getServer());
		PlaceHolders.getInstance().load();
		CoolDownCheck.getInstance().checkAll();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				update();
			}
		});
	}

	public void reloadAll() {
		reloadPlugin(true);
	}

	@Getter
	private GUI gui;

	private void setupFiles() {
		config = Config.getInstance();
		config.setup();
		// config.loadValues();

		configVoteSites = ConfigVoteSites.getInstance();
		configVoteSites.setup();

		specialRewardsConfig = SpecialRewardsConfig.getInstance();
		specialRewardsConfig.setup();
		// specialRewardsConfig.loadValues();

		bungeeSettings = BungeeSettings.getInstance();
		bungeeSettings.setup();
		// bungeeSettings.loadValues();

		gui = GUI.getInstance();
		gui.setup();

		checkYMLError();

		plugin.debug("Loaded Files");

	}

	/**
	 * Update.
	 */
	public void update() {
		if (update || Config.getInstance().isAlwaysUpdate()) {
			if (!updateStarted && plugin != null) {
				if (!Config.getInstance().isUpdateWithPlayersOnlineOnly() || Bukkit.getOnlinePlayers().size() != 0) {
					updateStarted = true;
					update = false;

					synchronized (plugin) {
						if (getStorageType().equals(UserStorage.MYSQL)) {
							if (getMysql() == null) {
								plugin.debug("MySQL not loaded yet");
								return;
							} else if (Config.getInstance().isClearCacheOnUpdate()
									|| BungeeSettings.getInstance().isUseBungeecoord()) {
								getMysql().clearCache();
							} else {
								getMysql().clearCacheBasic();
							}
						}

						plugin.debug("Starting background task");
						long time = System.currentTimeMillis();
						try {
							ArrayList<String> uuids = UserManager.getInstance().getAllUUIDs();
							ArrayList<VotingPluginUser> users = new ArrayList<VotingPluginUser>();
							for (String uuid : uuids) {
								if (uuid != null && !uuid.isEmpty()) {
									VotingPluginUser user = UserManager.getInstance().getVotingPluginUser(new UUID(uuid));
									users.add(user);
									// extraDebug("Loading " + uuid);
									// java.lang.Thread.sleep(5000);
								}
							}
							update = false;
							long time1 = ((System.currentTimeMillis() - time) / 1000);
							plugin.debug("Finished loading player data in " + time1 + " seconds, " + users.size()
									+ " users");
							TopVoterHandler.getInstance().updateTopVoters(users);
							updateVoteToday(users);
							ServerData.getInstance().updateValues();
							Signs.getInstance().updateSigns();

							if (!Config.getInstance().isExtraBackgroundUpdate()) {
								for (Player player : Bukkit.getOnlinePlayers()) {
									VotingPluginUser user = UserManager.getInstance().getVotingPluginUser(player);
									user.offVote();
								}
							}
							time1 = ((System.currentTimeMillis() - time) / 1000);
							plugin.debug("Background task finished in " + time1 + " seconds");
						} catch (Exception ex) {
							plugin.getLogger().info("Looks like something went wrong");
							ex.printStackTrace();
						}
					}

					updateStarted = false;
				}
			}
		}
	}

	public void updateVoteToday(ArrayList<VotingPluginUser> users) {
		plugin.getVoteToday().clear();

		for (VotingPluginUser user : users) {
			HashMap<VoteSite, LocalDateTime> times = new HashMap<VoteSite, LocalDateTime>();
			for (VoteSite voteSite : plugin.getVoteSites()) {
				long time = user.getTime(voteSite);
				if ((LocalDateTime.now().getDayOfMonth() == MiscUtils.getInstance().getDayFromMili(time))
						&& (LocalDateTime.now().getMonthValue() == MiscUtils.getInstance().getMonthFromMili(time))
						&& (LocalDateTime.now().getYear() == MiscUtils.getInstance().getYearFromMili(time))) {

					times.put(voteSite, LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));

				}
			}
			if (times.keySet().size() > 0) {
				plugin.getVoteToday().put(user, times);
			}

		}
		plugin.debug("Updated VoteToday");
	}

	public void updateAdvancedCoreHook() {
		getJavascriptEngine().put("VotingPlugin", this);
		allowDownloadingFromSpigot(15358);
		setConfigData(Config.getInstance().getData());
		if (BungeeSettings.getInstance().isUseBungeecoord()) {
			getOptions().setClearCacheOnJoin(true);
		}
	}

	private void writeConvertData(HashMap<VotingPluginUser, HashMap<String, String>> data) {
		boolean checkInt = getStorageType().equals(UserStorage.MYSQL);
		for (Entry<VotingPluginUser, HashMap<String, String>> entry : data.entrySet()) {
			try {
				for (Entry<String, String> values : entry.getValue().entrySet()) {
					String value = values.getValue();
					if (value != null && !value.equalsIgnoreCase("null")) {
						if (checkInt) {
							if (StringParser.getInstance().isInt(value)) {
								entry.getKey().getData().setInt(values.getKey(), Integer.parseInt(value));
							} else {
								entry.getKey().getData().setString(values.getKey(), value);
							}
						} else {
							entry.getKey().getData().setString(values.getKey(), value);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				plugin.getLogger()
						.warning("Exception occoured for '" + entry.getKey().getUUID() + "': " + e.getMessage());
			}
		}
	}

}