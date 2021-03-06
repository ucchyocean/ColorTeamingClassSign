/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ctcs;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * リスナークラス
 * @author ucchy
 */
public class ClassSignListener implements Listener {

    private static final String ACTIVE = ChatColor.GREEN + "[ACTIVE]";
    private static final String INACTIVE = ChatColor.RED + "[INACTIVE]";
    private static final String FIRST_LINE = "[CClass]";

    private static final String PERMISSION_PRE = "ctclasssign.";
    private static final String PERMISSION_USER_USE = PERMISSION_PRE + "user.use";
    private static final String PERMISSION_ADMIN_TOGGLE = PERMISSION_PRE + "admin.toggle";
    private static final String PERMISSION_ADMIN_PLACE = PERMISSION_PRE + "admin.place";
    private static final String PERMISSION_ADMIN_BREAK = PERMISSION_PRE + "admin.break";

    private static final String MSG_PRE = "[CTCS]";
    private static final String MSG_PRE_ERR = ChatColor.RED + MSG_PRE;
    private static final String MSG_NOT_EXIST_CLASS =
            MSG_PRE_ERR + "指定されたクラスが存在しません。";
    private static final String MSG_NOT_HAVE_PERMISSION_PLACE =
            MSG_PRE_ERR + "権限が無いためクラスサインを設置できません。";
    private static final String MSG_NOT_HAVE_PERMISSION_BREAK =
            MSG_PRE_ERR + "権限が無いためクラスサインを除去できません。";

    private static final String MSG_NOT_TEAM_MEMBER =
            MSG_PRE_ERR + "あなたは %s チームのメンバーではないため、使用できません。";

    private ColorTeamingBridge bridge;

    /**
     * コンストラクタ
     * @param bridge
     */
    public ClassSignListener(ColorTeamingBridge bridge) {
        this.bridge = bridge;
    }

    /**
     * カンバンをクリックしたときのイベント
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        // クリックされたのがカンバンでないなら無視する
        if (event.getClickedBlock() == null ||
                !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign)event.getClickedBlock().getState();

        // 関係のないカンバンなら無視する
        if (!sign.getLine(0).equals(FIRST_LINE)) {
            return;
        }

        Player player = event.getPlayer();
        if ( event.getAction() == Action.LEFT_CLICK_BLOCK
                || (event.getPlayer().getGameMode() == GameMode.ADVENTURE
                    && event.getAction() == Action.RIGHT_CLICK_BLOCK ) ) {

            if ( !player.hasPermission(PERMISSION_USER_USE) ) {
                // 権限がない
                return;
            }

            if ( sign.getLine(3).equals(INACTIVE) ) {
                // カンバンが無効状態
                return;
            }

            String cname = sign.getLine(1);
            if ( !bridge.isExistClass(cname) ) {
                // 指定されたクラスが既に存在しない
                player.sendMessage(MSG_NOT_EXIST_CLASS);
                return;
            }

            String tname = sign.getLine(2);
            if ( tname != null && !tname.equals("")
                    && !bridge.isPlayerInTeam(player, tname) ) {
                // 指定されたチームに参加していない
                player.sendMessage(String.format(MSG_NOT_TEAM_MEMBER, tname));
                return;
            }

            String perm = PERMISSION_USER_USE + "." + cname;
            if ( player.isPermissionSet(perm) && !player.hasPermission(perm) ) {
                // 指定されたクラスの使用が権限で拒否されている
                return;
            }

            // クラスを設定する
            bridge.setClassToPlayer(player, cname);

            return;

        } else if ( event.getAction() == Action.RIGHT_CLICK_BLOCK ) {

            if ( !player.hasPermission(PERMISSION_ADMIN_TOGGLE) ) {
                // 権限が無い
                return;
            }

            // 有効状態と無効状態を切り替えする
            if ( sign.getLine(3).equals(ACTIVE) ) {
                sign.setLine(3, INACTIVE);
            } else if ( sign.getLine(3).equals(INACTIVE) ) {
                sign.setLine(3, ACTIVE);
            }
            sign.update();

            return;
        }
    }


    /**
     * カンバンを設置したときのイベント
     * @param event
     */
    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        if ( !event.getLine(0).equals(FIRST_LINE) ) {
            // 関係ないカンバンなら無視する
            return;
        }

        Player player = event.getPlayer();
        if ( !player.hasPermission(PERMISSION_ADMIN_PLACE) ) {
            // 権限が無い
            player.sendMessage(MSG_NOT_HAVE_PERMISSION_PLACE);
            event.setLine(0, "");
            return;
        }

        String cname = event.getLine(1);
        if ( !bridge.isExistClass(cname) ) {
            // 指定されたクラスが存在しない
            player.sendMessage(MSG_NOT_EXIST_CLASS);
            event.setLine(0, "");
            return;
        }

        // クラスサインを有効状態に変更する
        event.setLine(3, ACTIVE);
    }

    /**
     * ブロックが壊されたときのイベント
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        BlockState block = event.getBlock().getState();
        if ( !(block instanceof Sign) ) {
            // 壊されたブロックがカンバンでないなら無視する
            return;
        }

        Sign sign = (Sign) block;
        if ( !sign.getLine(0).equals(FIRST_LINE) ) {
            // 関係ないカンバンなら無視する
            return;
        }

        Player player = event.getPlayer();
        if ( !player.hasPermission(PERMISSION_ADMIN_BREAK) ) {
            // 権限が無い
            player.sendMessage(MSG_NOT_HAVE_PERMISSION_BREAK);
            event.setCancelled(true);
            return;
        }
    }
}
