/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ctcs;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * カラーチーミングクラスサイン
 * @author ucchy
 */
public class ColorTeamingClassSign extends JavaPlugin {

    /**
     * プラグインが有効化されたときに呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // ColorTeamingの取得、dependに指定しているので必ず取得できる。
        Plugin colorteaming = getServer().getPluginManager().getPlugin("ColorTeaming");
        
        // ブリッヂの作成
        ColorTeamingBridge bridge = new ColorTeamingBridge(colorteaming);
        
        // リスナーの登録
        getServer().getPluginManager().registerEvents(new ClassSignListener(bridge), this);
    }
}
