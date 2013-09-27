/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ctcs;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.ClassData;
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
        
        ColorTeamingConfig config = colorteaming.getCTConfig();
        ClassData cdata = config.getClasses().get(name);

        if ( cdata == null ) {
            return false;
        }
        
        ArrayList<ItemStack> itemData = cdata.getItems();
        ArrayList<ItemStack> armorData = cdata.getArmor();
        ArrayList<PotionEffect> effectData = cdata.getEffect();
        int experience = cdata.getExperience();
        
        // 全回復の実行
        if ( config.isHealOnSetClass() ) {
            Utility.heal(player);
        }
        
        // インベントリの消去
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        // アイテムの配布
        for ( ItemStack item : itemData ) {
            if ( item != null ) {
                player.getInventory().addItem(item);
            }
        }

        // 防具の配布
        if ( armorData != null ) {

            if (armorData.size() >= 1 && armorData.get(0) != null ) {
                player.getInventory().setHelmet(armorData.get(0));
            }
            if (armorData.size() >= 2 && armorData.get(1) != null ) {
                player.getInventory().setChestplate(armorData.get(1));
            }
            if (armorData.size() >= 3 && armorData.get(2) != null ) {
                player.getInventory().setLeggings(armorData.get(2));
            }
            if (armorData.size() >= 4 && armorData.get(3) != null ) {
                player.getInventory().setBoots(armorData.get(3));
            }
        }
        
        // インベントリ更新
        updateInventory(player);

        // ポーション効果の設定
        if ( effectData != null ) {
            player.addPotionEffects(effectData);
        }

        // 経験値の設定
        player.setTotalExperience(experience);
        Utility.updateExp(player);
        
        return true;
    }
    
    /**
     * プレイヤーのインベントリをアップデートする
     * @param player プレイヤー
     */
    @SuppressWarnings("deprecation")
    private void updateInventory(Player player) {
        player.updateInventory();
    }
}
