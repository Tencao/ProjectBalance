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

import invtweaks.api.container.ContainerSection;
import net.minecraft.item.ItemStack;

/**
 * Interface to access functions exposed by Inventory Tweaks
 * <p/>
 * The main @Mod instance of the mod implements this interface, so a refernce to it can
 * be obtained via @Instance("inventorytweaks") or methods in cpw.mods.fml.common.Loader
 * <p/>
 * All of these functions currently have no effect if called on a dedicated server.
 */
public interface InvTweaksAPI {
    /**
     * Add a listener for ItemTree load events
     *
     * @param listener
     */
    void addOnLoadListener(IItemTreeListener listener);

    /**
     * Remove a listener for ItemTree load events
     *
     * @param listener
     * @return true if the listener was previously added
     */
    boolean removeOnLoadListener(IItemTreeListener listener);

    /**
     * Toggle sorting shortcut state.
     *
     * @param enabled
     */
    void setSortKeyEnabled(boolean enabled);

    /**
     * Toggle sorting shortcut supression.
     * Unlike setSortKeyEnabled, this flag is automatically cleared when GUIs are closed.
     *
     * @param enabled
     */
    void setTextboxMode(boolean enabled);

    /**
     * Compare two items using the default (non-rule based) algorithm,
     * sutable for an implementation of Comparator&lt;ItemStack&gt;.
     *
     * @param i
     * @param j
     * @return A value with a sign representing the relative order of the item stacks
     */
    int compareItems(ItemStack i, ItemStack j);

    /**
     * Initiate a sort as if the player had clicked on a sorting button or pressed the sort key.
     *
     * @param section
     * @param method
     */
    void sort(ContainerSection section, SortingMethod method);
}
