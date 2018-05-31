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

package invtweaks.api.container;

/**
 * Names for specific parts of containers. For unknown container types (such as mod containers), only INVENTORY and
 * CHEST sections are available.
 */
public enum ContainerSection {
    /**
     * The player's inventory
     */
    INVENTORY,
    /**
     * The player's inventory (only the hotbar)
     */
    INVENTORY_HOTBAR,
    /**
     * The player's inventory (all except the hotbar)
     */
    INVENTORY_NOT_HOTBAR,
    /**
     * The chest or dispenser contents. Also used for unknown container contents.
     */
    CHEST,
    /**
     * The crafting input
     */
    CRAFTING_IN,
    /**
     * The crafting input, for containters that store it internally
     */
    CRAFTING_IN_PERSISTENT,
    /**
     * The crafting output
     */
    CRAFTING_OUT,
    /**
     * The armor slots
     */
    ARMOR,
    /**
     * The furnace input
     */
    FURNACE_IN,
    /**
     * The furnace output
     */
    FURNACE_OUT,
    /**
     * The furnace fuel
     */
    FURNACE_FUEL,
    /**
     * The enchantment tile slot
     */
    ENCHANTMENT,
    /**
     * The three bottles slots in brewing tables
     * NOTE: Do not use without also using BREWING_INGREDIENT.
     */
    BREWING_BOTTLES,
    /**
     * The top slot in brewing tables
     * NOTE: Do not use without also using BREWING_BOTTLES.
     */
    BREWING_INGREDIENT
}
