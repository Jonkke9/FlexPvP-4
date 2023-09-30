package fi.flexplex.pvp.game.duel;

import fi.flexplex.core.api.FlexPlayer;
import fi.flexplex.core.api.Language;
import fi.flexplex.core.api.Permissions;
import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Duels {

	private static final Set<DuelInvite> INVITES = new HashSet<>();
	private static final Set<Duel> ACTIVE_DUELS = new HashSet<>();


	public static void sendInvite(final DuelInvite invite) {

		final Player to = invite.getTo();
		final Player from = invite.getFrom();

		if (!to.isOnline() || FlexPlayer.getPlayer(to).isVanished()) {
			Language.sendMessage(from, "PVP_DUELS_INVITE_ERROR_QUIT");
			return;
		}

		if (countInvitesFrom(invite.getFrom()) > 2) {
			ArrayList<DuelInvite> invt = getInvitesFrom(invite.getFrom());

			DuelInvite oldest = invt.get(0);

			for (int i = 1; i < invt.size(); i++)
				if (invt.get(i).getTimeTag() < oldest.getTimeTag()) oldest = invt.get(i);

			INVITES.remove(oldest);
		}

		INVITES.add(invite);

		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> INVITES.remove(invite), 3000);

		sendInviteMessage(invite);
		Language.sendMessage(from, "PVP_DUELS_INVITE_SENT", Permissions.getLegacyDisplayName(to));
	}

	public static int countInvitesFrom(final Player player) {
		int amount = 0;
		for (DuelInvite invite : INVITES) {
			if (invite.getFrom() == player) {
				amount++;
			}
		}
		return amount;
	}

	public static ArrayList<DuelInvite> getInvitesFrom(final Player player) {
		final ArrayList<DuelInvite> sent = new ArrayList<>();
		for (DuelInvite invite : INVITES) {
			if (invite.getFrom() == player) {
				sent.add(invite);
			}
		}
		return sent;
	}

	public static ArrayList<DuelInvite> getInvitesTo(final Player player) {
		final ArrayList<DuelInvite> duelsToPlayer = new ArrayList<>();
		for (final DuelInvite invite : INVITES) {
			if (invite.getTo() == player) {
				duelsToPlayer.add(invite);
			}
		}
		return duelsToPlayer;
	}

	public static void clearInvitesFrom(final Player player) {
		final ArrayList<DuelInvite> toRemove = new ArrayList<>();
		for (final DuelInvite invite : INVITES) {
			if (invite.getFrom() == player) toRemove.add(invite);
		}
		for (final DuelInvite invite : toRemove) {
			INVITES.remove(invite);
		}
	}

	public static void clearInvitesTo(final Player player) {
		final ArrayList<DuelInvite> toRemove = new ArrayList<>();
		for (final DuelInvite invite : INVITES) {
			if (invite.getTo() == player) toRemove.add(invite);
		}
		for (final DuelInvite invite : toRemove) {
			INVITES.remove(invite);
		}
	}

	public static void clearInvitesToAndFrom(final Player player) {
		clearInvitesFrom(player);
		clearInvitesTo(player);
	}

	private static void sendInviteMessage(final DuelInvite invite) {
		final Player player = invite.getTo();

		final List<Component> lines = new ArrayList<>();

		lines.add(Language.getMessage(player, "PVP_DUELS_INVITE_INFO"));
		lines.add(Component.text(""));
		lines.add(Language.getMessage(player, "PVP_DUELS_INVITE_ARENA", Language.getStringMessage(player, invite.getArena().getTemplate().getDisplayNameKey())));
		lines.add(Component.text(""));
		lines.add(Language.getMessage(player, "PVP_DUELS_INVITE_KITS"));


		for (final Kit kit : invite.getKits()) {
			lines.add(Language.getMessage(player, "PVP_DUELS_INVITE_KIT", Language.getStringMessage(player, kit.getDisplayNameKey())));
		}

		lines.add(Component.text(""));
		lines.add(Language.getMessage(player, "PVP_DUELS_INVITE_CLICK"));

		final TextComponent component = Component.text()
				.content(Language.getStringMessage(player, "PVP_DUELS_INVITE_RECEIVED", Permissions.getLegacyDisplayName(invite.getFrom())))
				.hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), lines)))
				.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/duelsaccept " + invite.getFrom().getName()))
				.build();
		invite.getTo().sendMessage(component);
	}

	public static void acceptInvite(final DuelInvite invite) {
		if (!invite.getArena().isReady()) {
			Language.sendMessage(invite.getTo(), "PVP_DUELS_ARENA_NOT_READY");
			return;
		}
		INVITES.remove(invite);

		final Duel duel = new Duel(invite.getFrom(), invite.getTo(), invite.getKits(), invite.getArena(), invite.getSettings());
		invite.getArena().setDuel(duel);
		duel.start();
	}

	public static void activateDuel(final Duel duel) {
		ACTIVE_DUELS.add(duel);
	}

	public static void deActivateDuel(final Duel duel) {
		ACTIVE_DUELS.remove(duel);
	}


	public static boolean isInDuel(final Player player) {
		return getPlayerDuel(player) != null;
	}


	public static Duel getPlayerDuel(final Player player) {
		for (final Duel duel : ACTIVE_DUELS) {
			if (duel.fromPlayer() == player || duel.toPlayer() == player) {
				return duel;
			}
		}
		return null;
	}

}
