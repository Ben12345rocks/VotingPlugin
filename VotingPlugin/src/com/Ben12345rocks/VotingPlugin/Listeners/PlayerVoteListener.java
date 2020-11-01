package com.Ben12345rocks.VotingPlugin.Listeners;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;
import com.Ben12345rocks.AdvancedCore.Util.PluginMessage.PluginMessage;
import com.Ben12345rocks.VotingPlugin.BungeeHandler;
import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Config.BungeeSettings;
import com.Ben12345rocks.VotingPlugin.Config.Config;
import com.Ben12345rocks.VotingPlugin.Config.ConfigVoteSites;
import com.Ben12345rocks.VotingPlugin.Events.PlayerVoteEvent;
import com.Ben12345rocks.VotingPlugin.Objects.User;
import com.Ben12345rocks.VotingPlugin.Objects.VoteSite;
import com.Ben12345rocks.VotingPlugin.SpecialRewards.SpecialRewards;
import com.Ben12345rocks.VotingPlugin.UserManager.UserManager;
import com.Ben12345rocks.VotingPlugin.VoteParty.VoteParty;
import com.Ben12345rocks.VotingPlugin.bungee.BungeeMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class VotiferEvent.
 */
public class PlayerVoteListener implements Listener {

	/** The config. */
	static Config config = Config.getInstance();

	/** The config vote sites. */
	static ConfigVoteSites configVoteSites = ConfigVoteSites.getInstance();

	/** The plugin. */
	static Main plugin = Main.plugin;

	private static Object object = new Object();

	/**
	 * Instantiates a new votifer event.
	 *
	 * @param plugin the plugin
	 */
	public PlayerVoteListener(Main plugin) {
		PlayerVoteListener.plugin = plugin;
	}

	/**
	 * On votifer event.
	 *
	 * @param event the event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onplayerVote(PlayerVoteEvent event) {
		String playerName = event.getPlayer();
		if (!PlayerUtils.getInstance().isValidUser(playerName, Config.getInstance().isAllowUnJoinedCheckServer())) {
			if (!config.isAllowUnjoined()) {
				plugin.getLogger().warning("Player " + playerName
						+ " has not joined before, disregarding vote, set AllowUnjoined to true to prevent this");
				return;
			}
		}
		
		if (event.isBungee()) {
			plugin.debug("BungeePlayerVote forcebungee: " + event.isForceBungee() + ", bungeetotals: " + event.getBungeeTextTotals());
		}

		VoteSite voteSite = event.getVoteSite();

		// check valid service sites
		if (voteSite == null) {
			if (!Config.getInstance().isDisableNoServiceSiteMessage()) {
				plugin.getLogger().warning("No voting site with the service site: '" + event.getServiceSite() + "'");
				plugin.getLogger().warning(
						"Please read here on how to fix it: https://github.com/Ben12345rocks/VotingPlugin/wiki/Common-Problems");

				ArrayList<String> services = new ArrayList<String>();
				for (VoteSite site : plugin.getVoteSites()) {
					services.add(site.getServiceSite());
				}
				plugin.getLogger()
						.warning("Current known service sites: " + ArrayUtils.getInstance().makeStringList(services));
			}
			return;
		}

		User user = UserManager.getInstance().getVotingPluginUser(playerName);
		user.updateName();

		if (Config.getInstance().isClearCacheOnVote() || BungeeSettings.getInstance().isUseBungeecoord()) {
			user.clearCache();
		}

		if (voteSite.isWaitUntilVoteDelay() && !user.canVoteSite(voteSite)) {
			plugin.getLogger().info(user.getPlayerName() + " must wait until votedelay is over, ignoring vote");
			return;
		}

		synchronized (object) {

			// vote party
			VoteParty.getInstance().vote(user, event.isRealVote());

			// broadcast vote if enabled in config
			if (config.isBroadcastVotesEnabled()
					&& (BungeeSettings.getInstance().isBungeeBroadcast() || !event.isBungee())) {
				if (!Config.getInstance().getFormatBroadcastWhenOnline() || user.isOnline()) {
					voteSite.broadcastVote(user);
				}
			}

			// update last vote time
			if (event.getTime() != 0) {
				user.setTime(voteSite, event.getTime());
			} else {
				user.setTime(voteSite);
			}
			user.setLastVoteCoolDownCheck(false, voteSite);

			// try logging to file
			if (Config.getInstance().isLogVotesToFile()) {
				try {
					Main.plugin.logVote(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime(),
							playerName, voteSite.getKey());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// check first vote rewards
			SpecialRewards.getInstance().checkFirstVote(user);

			if (user.isReminded()) {
				user.setReminded(false);
			}

			// check if player has voted on all sites in one day

			if (((user.isOnline() || voteSite.isGiveOffline()) && Main.plugin.getOptions().isProcessRewards())
					|| event.isBungee()) {
				user.playerVote(voteSite, true, false, event.isForceBungee());
				user.closeInv();
			} else {
				user.addOfflineVote(voteSite.getKey());
				plugin.debug(
						"Offline vote set for " + playerName + " (" + user.getUUID() + ") on " + voteSite.getKey());
			}

			// add to total votes
			if ((Config.getInstance().isCountFakeVotes() || event.isRealVote()) && event.isAddTotals()) {
				if (Config.getInstance().isAddTotals()) {
					user.addTotal();
					user.addTotalDaily();
					user.addTotalWeekly();
				}
				user.addPoints();
			}

			user.checkDayVoteStreak();

			// other rewards
			SpecialRewards.getInstance().checkAllSites(user);
			SpecialRewards.getInstance().checkCumualativeVotes(user, event.getBungeeTextTotals());
			SpecialRewards.getInstance().checkMilestone(user, event.getBungeeTextTotals());

			if (BungeeSettings.getInstance().isUseBungeecoord()) {
				if (BungeeHandler.getInstance().getMethod().equals(BungeeMethod.MYSQL)) {
					final String uuid = user.getUUID();
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

						@Override
						public void run() {
							if (Bukkit.getOnlinePlayers().size() > 0) {
								PluginMessage.getInstance().sendPluginMessage(
										PlayerUtils.getInstance().getRandomOnlinePlayer(), "VoteUpdate", uuid);
							}
						}
					}, 40);
				}
			}
		}

		plugin.setUpdate(true);
	}

}
