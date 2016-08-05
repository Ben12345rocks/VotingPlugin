package com.Ben12345rocks.VotingPlugin.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Utils;
import com.Ben12345rocks.VotingPlugin.Config.Config;
import com.Ben12345rocks.VotingPlugin.Config.ConfigOtherRewards;
import com.Ben12345rocks.VotingPlugin.Config.ConfigRewards;
import com.Ben12345rocks.VotingPlugin.Config.ConfigVoteSites;
import com.swifteh.GAL.GAL;
import com.swifteh.GAL.GALVote;
import com.swifteh.GAL.VoteType;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class GALConverter {

	/** The instance. */
	static GALConverter instance = new GALConverter();

	/** The plugin. */
	static Main plugin = Main.plugin;

	private GAL galPlug = GAL.p;

	/**
	 * Gets the single instance of Utils.
	 *
	 * @return single instance of Utils
	 */
	public static GALConverter getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new utils.
	 */
	private GALConverter() {
	}

	/**
	 * Instantiates a new utils.
	 *
	 * @param plugin
	 *            the plugin
	 */
	public GALConverter(Main plugin) {
		GALConverter.plugin = plugin;
	}

	public void convert() {
		create();
	}

	public void create() {
		for (Entry<VoteType, GALVote> entry : galPlug.galVote.entries()) {
			GALVote vote = entry.getValue();
			if (entry.getKey().equals(VoteType.NORMAL)) {
				String service = vote.key;
				String rewardMessage = formatPlayer(vote.message);
				// String broadcast = vote.broadcast;
				List<String> commands = vote.commands;
				if (!service.equalsIgnoreCase("default")) {

					ConfigVoteSites.getInstance().generateVoteSite(service);
					ArrayList<String> rewards = new ArrayList<String>();
					rewards.add(service);
					ConfigVoteSites.getInstance().setRewards(service, rewards);
				} else {

					ArrayList<String> rewards = Config.getInstance()
							.getRewards();
					rewards.add(service);
					Config.getInstance().setRewards(rewards);

				}
				ConfigRewards.getInstance().setMessagesReward(service,
						rewardMessage);
				ConfigRewards.getInstance().setCommandsConsole(service,
						(ArrayList<String>) commands);
			} else if (entry.getKey().equals(VoteType.CUMULATIVE)) {
				String key = vote.key;
				if (Utils.getInstance().isInt(key)) {
					String rewardMessage = formatPlayer(vote.message);
					String broadcast = vote.broadcast;
					List<String> commands = vote.commands;
					ArrayList<String> rewards = new ArrayList<String>();
					rewards.add("cumulative" + key);
					ConfigOtherRewards.getInstance().setCumulativeRewards(
							Integer.parseInt(key), rewards);
					commands.add("broadcast " + broadcast);
					ConfigRewards.getInstance().setMessagesReward(
							"cumulative" + key, rewardMessage);
					ConfigRewards.getInstance().setCommandsConsole(
							"cumulative" + key, (ArrayList<String>) commands);
				}
			} else if (entry.getKey().equals(VoteType.LUCKY)) {
				String key = vote.key;
				if (Utils.getInstance().isInt(key)) {
					String rewardMessage = formatPlayer(vote.message);
					String broadcast = vote.broadcast;
					List<String> commands = vote.commands;
					ArrayList<String> rewards = Config.getInstance()
							.getRewards();
					rewards.add("lucky" + key);
					Config.getInstance().setRewards(rewards);
					commands.add("broadcast " + broadcast);
					ConfigRewards.getInstance().setMessagesReward(
							"lucky" + key, rewardMessage);
					ConfigRewards.getInstance().setChance("lucky" + key,
							Integer.parseInt(key));
					ConfigRewards.getInstance().setCommandsConsole(
							"lucky" + key, (ArrayList<String>) commands);
				}
			} else if (entry.getKey().equals(VoteType.PERMISSION)) {
				String key = vote.key;

				String rewardMessage = formatPlayer(vote.message);
				String broadcast = vote.broadcast;
				List<String> commands = vote.commands;
				ArrayList<String> rewards = Config.getInstance().getRewards();
				rewards.add("perm" + key);
				Config.getInstance().setRewards(rewards);
				commands.add("broadcast " + broadcast);
				ConfigRewards.getInstance().setMessagesReward("perm" + key,
						rewardMessage);
				ConfigRewards.getInstance().setRequirePermission("perm" + key,
						true);
				ConfigRewards.getInstance().setCommandsConsole("perm" + key,
						(ArrayList<String>) commands);
			}
		}
	}

	public String formatPlayer(String format) {
		return format.replace("{username}", "%player%")
				.replace("{player}", "%player%").replace("{name}", "%player%");
	}

}