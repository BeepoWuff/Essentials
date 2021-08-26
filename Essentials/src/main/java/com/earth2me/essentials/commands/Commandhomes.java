package com.earth2me.essentials.commands;

import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;

public class Commandhomes extends EssentialsCommand {
    public Commandhomes() {
        super("homes");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        User player = user;
        if (args.length > 0 && user.isAuthorized("essentials.homes.others")) {
            player = getPlayer(server, user, args, 0);
        }

        final User finalPlayer = player;
        final CompletableFuture<Location> message = new CompletableFuture<>();
        message.thenAccept(bed -> {
            final List<String> homes = finalPlayer.getHomes();

            if (finalPlayer.isAuthorized("essentials.home.bed")) {
                if (bed != null) {
                    homes.add(tl("bed"));
                } else {
                    homes.add(tl("bedNull"));
                }
            }

            if (homes.isEmpty()) {
                showError(user.getBase(), new Exception(tl("noHomeSetPlayer")), commandLabel);
                return;
            }

            user.sendMessage(tl("homes", StringUtil.joinList(homes)));
        });

        if (!finalPlayer.getBase().isOnline() || finalPlayer.getBase() instanceof OfflinePlayer) {
            message.complete(null);
            return;
        }

        PaperLib.getBedSpawnLocationAsync(finalPlayer.getBase(), true).thenAccept(message::complete);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1 && user.isAuthorized("essentials.homes.others")) {
            return getPlayers(server, user);
        }

        return Collections.emptyList();
    }
}
