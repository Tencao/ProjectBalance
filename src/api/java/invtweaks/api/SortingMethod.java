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

package invtweaks.api;

public enum SortingMethod {
    /** Standard 'r' sorting for generic inventories */
    DEFAULT,
    /** Sort method creating vertical columns of items.
     * Used for chests only, requires container to have a valid row size for correct results.
     */
    VERTICAL,
    /** Sort method creating horizontal rows of items.
     * Used for chests only, requires container to have a valid row size for correct results.
     */
    HORIZONTAL,
    /** Sort method for player inventory.
     * Applies to extra player-specified sorting rules for the main inventory.
     * Will always operate on main inventory.
     */
    INVENTORY,
    /** Attempts to even the number of items in each stack of the same type of item, without moving full stacks.
     * Used in crafting grid sorting.
     */
    EVEN_STACKS,
}
