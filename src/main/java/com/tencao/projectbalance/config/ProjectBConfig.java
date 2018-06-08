/*
 * Copyright (C) 2018
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tencao.projectbalance.config;

import com.tencao.projectbalance.ProjectBCore;
import net.minecraftforge.common.config.Config;

@Config(modid = ProjectBCore.MODID, name = ProjectBCore.NAME + "/config")
public final class ProjectBConfig {

    @Config.Comment({"Tweaks for various mechanics within ProjectE."})
    public static final Tweaks tweaks = new Tweaks();
    public static class Tweaks {
        //EMC Generation

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("EMC Transfer Rate from Relay to KleinStars in Seconds.")
        public int TransferRateMK1 = 64;

        @Config.RangeInt(min = 0, max = 50000)
        public int TransferRateMK2 = 192;

        @Config.RangeInt(min = 0, max = 100000)
        public int TransferRateMK3 = 640;

        @Config.RangeInt(min = 0, max = 1000000)
        public int TransferRateMK4 = 1280;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Comment("EMC Collector Rate for Energy Collector in Seconds.")
        public double CollectorRateMK1 = 0.1;

        @Config.RangeDouble(min = 0, max = 400)
        public double CollectorRateMK2 = 1;

        @Config.RangeDouble(min = 0, max = 1000)
        public double CollectorRateMK3 = 10;

        @Config.RangeDouble(min = 0, max = 2000)
        public double CollectorRateMK4 = 40;

        @Config.RangeDouble(min = 0, max = 50)
        @Config.Comment("Relay Bonus Rate per collector in seconds.")
        public double RelayBonusMK1 = 0.5;

        @Config.RangeDouble(min = 0, max = 100)
        public double RelayBonusMK2 = 2.5;

        @Config.RangeDouble(min = 0, max = 400)
        public double RelayBonusMK3 = 10;

        @Config.RangeDouble(min = 0, max = 800)
        public double RelayBonusMK4 = 40;

        @Config.RangeInt(min = 0)
        @Config.Comment("Max EMC a Collector can store.")
        public int CollectorMaxMK1 = 10000;

        @Config.RangeInt(min = 0)
        public int CollectorMaxMK2 = 30000;

        @Config.RangeInt(min = 0)
        public int CollectorMaxMK3 = 60000;

        @Config.RangeInt(min = 0)
        public int CollectorMaxMK4 = 120000;

        @Config.RangeInt(min = 0)
        @Config.Comment("Max EMC a Relay can store.")
        public int RelayMaxMK1 = 100000;

        @Config.RangeInt(min = 0)
        public int RelayMaxMK2 = 1000000;

        @Config.RangeInt(min = 0)
        public int RelayMaxMK3 = 10000000;

        @Config.RangeInt(min = 0)
        public int RelayMaxMK4 = 20000000;

        @Config.RangeInt(min = 0)
        @Config.Comment("Max EMC a DM Pedestal can store.")
        public int DMPedestalMax = 10000;

        @Config.RangeInt(min = 0)
        public int RMPedestalMax = 100000;

        @Config.RangeInt(min = 0)
        public int BMPedestalMax = 1000000;

        //EMC Cost
        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("HyperKinetic Lens emc cost per charge (Doubles per charge).")
        public int HyperkineticEmc = 384;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("Destruction Catalyst emc cost per block destroyed.")
        public int DestroEmc = 8;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("SWRG Ability cost in emc")
        public int SWRGEmc = 64;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("BodyStone emc cost per use.")
        public int BodyStoneEmc = 500;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("SoulStone emc cost per use.")
        public int SoulStoneEmc = 500;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("Volcanite Amulet emc cost per use.")
        public int VolcaniteEmc = 64;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("The EMC cost per damage point received.")
        public int DMDamagePer = 45;

        @Config.RangeInt(min = 0, max = 10000)
        public int RMDamagePer = 100;

        @Config.RangeInt(min = 0, max = 10000)
        public int BMDamagePer = 200;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("The EMC cost per half point depleted.")
        public int BMFoodAbility = 100;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("The EMC cost per half point received.")
        public int BMHealAbility = 100;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("The EMC cost per explosion.")
        public int BMExplosionAbility = 1000;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("The EMC cost per lightning.")
        public int BMLightningAbility = 500;

        //Pedestal EMC Cost
        @Config.RangeInt(min = -1, max = 10000)
        @Config.Comment("The emc required per action in the Pedestal.")
        public int VolcaniteAmuletPedestalCost = 50;

        @Config.RangeInt(min = -1, max = 10000)
        public int EvertideAmuletPedestalCost = 500;

        @Config.RangeInt(min = -1, max = 10000)
        public int MindStonePedestalCost = 100;

        @Config.RangeInt(min = -1, max = 10000)
        public int BodyStonePedestalCost = 100;

        @Config.RangeInt(min = -1, max = 10000)
        public int SoulStonePedestalCost = 100;

        @Config.RangeInt(min = -1, max = 10000)
        public int BlackHolePedestalCost = -1;

        @Config.RangeInt(min = -1, max = 10000)
        public int IgnitionRingPedestalCost = 100;

        @Config.RangeInt(min = -1, max = 10000)
        public int ZeroRingPedestalCost = 10;

        @Config.RangeInt(min = -1, max = 10000)
        public int ArchangelPedestalCost = 14;

        @Config.RangeInt(min = -1, max = 10000)
        public int SWRGPedestalCost = 30;

        @Config.RangeInt(min = -1, max = 10000)
        public int HarvestGoddessPedestalCost = 48;

        @Config.RangeInt(min = -1, max = 10000)
        public int TimeWatchBoostPedestalCost = 100;

        @Config.RangeInt(min = 0, max = 10000)
        public int TomeCost = 20;

        //Cooldowns
        @Config.RangeInt(min = -1, max = 600)
        @Config.Comment("The cooldown (in ticks) for each DM Pedestal check.")
        public int scanCooldown = 20;

        @Config.RangeInt(min = -1, max = 600)
        @Config.Comment("The cooldown (in seconds) for each mob repel.")
        public int interdictionTorchCooldown = 5;

        @Config.RangeInt(min = -1, max = 600)
        @Config.Comment("The cooldown (in seconds) for each heal ability.")
        public int healCooldown = 300;

        @Config.RangeInt(min = -1, max = 600)
        @Config.Comment("The cooldown (in seconds) for each food ability.")
        public int foodCooldown = 300;

        //Tweaks
        @Config.RangeInt(min = 0)
        @Config.Comment("The max EMC the DM armor can store.")
        public double DMMaxEMC = 50000;

        @Config.RangeInt(min = 0)
        @Config.Comment("The max EMC the RM armor can store.")
        public double RMMaxEMC = 100000;

        @Config.RangeInt(min = 0)
        @Config.Comment("The max EMC the Gem armor can store.")
        public double BMMaxEMC = 150000;

        @Config.RangeInt(min = 0)
        @Config.Comment("The max EMC the Gem armor can store.")
        public int healingDuration = 20;

        @Config.RangeInt(min = 0)
        @Config.Comment("The max EMC the Gem armor can store.")
        public int foodDuration = 20;
    }
}
