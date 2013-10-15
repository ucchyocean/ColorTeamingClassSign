/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ctcs;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;

/**
 * カラーチーミング 連携クラス
 * @author ucchy
 */
public class ColorTeamingBridge {

    private ColorTeaming colorteaming;
    
    /**
     * コンストラクタ
     * @param colorteaming 
     */
    public ColorTeamingBridge(Plugin colorteaming) {
        this.colorteaming = (ColorTeaming)colorteaming;
    }
    
    /**
     * 指定されたクラスが存在するかどうかを確認する
     * @param name クラス名
     * @return 存在するかどうか
     */
    public boolean isExistClass(String name) {
        ColorTeamingConfig config = colorteaming.getCTConfig();
        return config.getClasses().containsKey(name);
    }
    
    /**
     * 指定されたプレイヤーに指定されたクラスを設定する
     * @param player プレイヤー
     * @param name クラス名
     * @return クラス設定を実行したかどうか
     */
    public boolean setClassToPlayer(Player player, String name) {
        
        ColorTeamingAPI api = colorteaming.getAPI();
        
        ArrayList<Player> playersToSet = new ArrayList<Player>();
        playersToSet.add(player);
        
        return api.setClassToPlayer(playersToSet, name);
    }
}
