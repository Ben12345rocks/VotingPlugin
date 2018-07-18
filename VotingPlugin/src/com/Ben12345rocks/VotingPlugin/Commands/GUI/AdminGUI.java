package com.Ben12345rocks.VotingPlugin.Commands.GUI;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory.ClickEvent;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventoryButton;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.InputMethod;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.ValueRequest;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.ValueRequestBuilder;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.BooleanListener;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.NumberListener;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.StringListener;
import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Config.ConfigVoteSites;
import com.Ben12345rocks.VotingPlugin.Events.VotiferEvent;
import com.Ben12345rocks.VotingPlugin.Objects.VoteSite;

import ninja.egg82.patterns.ServiceLocator;

public class AdminGUI {
	static AdminGUI instance = new AdminGUI();
	
	private Main main = ServiceLocator.getService(Main.class);

	/**
	 * Gets the single instance of Commands.
	 *
	 * @return single instance of Commands
	 */
	public static AdminGUI getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new commands.
	 */
	private AdminGUI() {
	}

	/**
	 * Open admin GUI.
	 *
	 * @return ArrayList of buttons
	 */
	public ArrayList<BInventoryButton> adminGUIButtons() {
		ArrayList<BInventoryButton> buttons = new ArrayList<BInventoryButton>();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("&cOnly enabled sites are listed in this section");
		lore.add("&cMiddle Click to create");
		buttons.add(new BInventoryButton("&cVoteSites", ArrayUtils.getInstance().convert(lore),
				new ItemStack(Material.STONE)) {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					Player player = event.getWhoClicked();
					if (event.getClick().equals(ClickType.MIDDLE)) {

						new ValueRequest().requestString(player, new StringListener() {

							@Override
							public void onInput(Player player, String value) {
								ConfigVoteSites.getInstance().generateVoteSite(value);
								player.sendMessage("Generated site");
								main.reload();
							}
						});
					} else {
						openAdminGUIVoteSites(player);
					}
				}
			}
		});

		lore = new ArrayList<String>();
		buttons.add(new BInventoryButton("&cReload Plugin", ArrayUtils.getInstance().convert(lore),
				new ItemStack(Material.STONE, 1, (short) 3)) {

			@Override
			public void onClick(ClickEvent event) {
				event.getPlayer().performCommand("av reload");
			}

		});
		return buttons;
	}

	public void loadHook() {
		for (BInventoryButton b : adminGUIButtons()) {
			com.Ben12345rocks.AdvancedCore.Commands.GUI.AdminGUI.getInstance().addButton(b);
		}
	}

	/**
	 * Open admin GUI vote sites.
	 *
	 * @param player
	 *            the player
	 */
	public void openAdminGUIVoteSites(Player player) {
		BInventory inv = new BInventory("VoteSites");
		int count = 0;
		for (VoteSite voteSite : main.getVoteSites()) {
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("Priority: " + voteSite.getPriority());
			lore.add("Name: " + voteSite.getDisplayName());
			lore.add("ServiceSite: " + voteSite.getServiceSite());
			lore.add("VoteURL: " + voteSite.getVoteURL());
			lore.add("VoteDelay: " + voteSite.getVoteDelay());
			// lore.add("Rewards: " +
			// ArrayUtils.getInstance().makeStringList(voteSite.getRewards()));

			inv.addButton(count, new BInventoryButton(voteSite.getKey(), ArrayUtils.getInstance().convert(lore),
					new ItemStack(Material.STONE)) {

				@Override
				public void onClick(ClickEvent event) {

					Player player = event.getWhoClicked();
					openAdminGUIVoteSiteSite(player, voteSite);

				}
			});
			count++;
		}
		inv.openInventory(player);
	}

	/**
	 * Open admin GUI vote site site.
	 *
	 * @param player
	 *            the player
	 * @param voteSite
	 *            the vote site
	 */
	public void openAdminGUIVoteSiteSite(Player player, VoteSite voteSite) {
		BInventory inv = new BInventory("VoteSite: " + voteSite.getDisplayName());
		inv.setMeta(player, "VoteSite", voteSite);

		inv.addButton(new BInventoryButton(voteSite.getItem().setName("&cForce vote")) {

			@Override
			public void onClick(ClickEvent event) {
				ArrayList<String> playerNames = new ArrayList<String>();
				for (Player p : Bukkit.getOnlinePlayers()) {
					playerNames.add(p.getName());
				}
				new ValueRequestBuilder(new StringListener() {

					@Override
					public void onInput(Player player, String value) {
						Object ob = PlayerUtils.getInstance().getPlayerMeta(player, "VoteSite");
						if (ob != null) {
							VoteSite site = (VoteSite) ob;
							VotiferEvent.playerVote(value, site.getServiceSite(), false);
						}
					}
				}, ArrayUtils.getInstance().convert(playerNames)).usingMethod(InputMethod.INVENTORY)
						.allowCustomOption(true).request(event.getWhoClicked());
			}
		});

		inv.addButton(new BInventoryButton("SetPriority", new String[0], new ItemStack(Material.STONE)) {

			@Override
			public void onClick(ClickEvent event) {
				Player player = event.getWhoClicked();
				new ValueRequest().requestNumber(player, "" + voteSite.getPriority(), null, new NumberListener() {

					@Override
					public void onInput(Player player, Number value) {
						VoteSite voteSite = (VoteSite) event.getMeta("VoteSite");
						ConfigVoteSites.getInstance().setPriority(voteSite.getKey(), value.intValue());
						player.sendMessage("Set Priority");
						main.reload();

					}
				});
			}
		});

		inv.addButton(new BInventoryButton("SetServiceSite", new String[0], new ItemStack(Material.STONE)) {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					Player player = event.getWhoClicked();

					new ValueRequest().requestString(player, voteSite.getServiceSite(), null, new StringListener() {

						@Override
						public void onInput(Player player, String value) {
							VoteSite voteSite = (VoteSite) event.getMeta("VoteSite");
							String siteName = voteSite.getKey();
							ConfigVoteSites.getInstance().setServiceSite(siteName, value);
							player.sendMessage("Set ServiceSite");
							main.reload();
						}
					});
				}

			}
		});

		inv.addButton(new BInventoryButton("SetName", new String[0], new ItemStack(Material.STONE)) {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					Player player = event.getWhoClicked();
					new ValueRequest().requestString(player, voteSite.getServiceSite(), null, new StringListener() {

						@Override
						public void onInput(Player player, String value) {
							VoteSite voteSite = (VoteSite) event.getMeta("VoteSite");
							ConfigVoteSites.getInstance().setDisplayName(voteSite.getKey(), value);
							player.sendMessage("Set name");
							main.reload();
						}
					});
				}

			}
		});

		inv.addButton(new BInventoryButton("SetVoteURL", new String[0], new ItemStack(Material.STONE)) {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					Player player = event.getWhoClicked();

					new ValueRequest().requestString(player, voteSite.getVoteURL(), null, new StringListener() {

						@Override
						public void onInput(Player player, String value) {
							VoteSite voteSite = (VoteSite) event.getMeta("VoteSite");
							String siteName = voteSite.getKey();
							ConfigVoteSites.getInstance().setVoteURL(siteName, value);
							player.sendMessage("Set VoteURL");
							main.reload();

						}
					});

				}

			}
		});

		inv.addButton(new BInventoryButton("SetVoteDelay", new String[0], new ItemStack(Material.STONE)) {

			@Override
			public void onClick(ClickEvent event) {
				Player player = event.getWhoClicked();
				new ValueRequest().requestNumber(player, "" + voteSite.getVoteDelay(), null, new NumberListener() {

					@Override
					public void onInput(Player player, Number value) {
						VoteSite voteSite = (VoteSite) event.getMeta("VoteSite");
						String siteName = voteSite.getKey();
						ConfigVoteSites.getInstance().setVoteDelay(siteName, value.intValue());
						player.sendMessage("Set VoteDelay");
						main.reload();

					}
				});

			}
		});
		inv.addButton(inv.getNextSlot(),
				new BInventoryButton("SetEnabled", new String[0], new ItemStack(Material.STONE)) {

					@Override
					public void onClick(ClickEvent event) {

						Player player = event.getWhoClicked();
						new ValueRequest().requestBoolean(player,
								"" + ConfigVoteSites.getInstance().getVoteSiteEnabled(voteSite.getKey()),
								new BooleanListener() {

									@Override
									public void onInput(Player player, boolean value) {
										VoteSite voteSite = (VoteSite) event.getMeta("VoteSite");
										String siteName = voteSite.getKey();
										ConfigVoteSites.getInstance().setEnabled(siteName, value);
										player.sendMessage("Set Enabled");
										main.reload();

									}
								});
					}
				});

		inv.openInventory(player);
	}
}
