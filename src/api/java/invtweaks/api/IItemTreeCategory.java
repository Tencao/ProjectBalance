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

import java.util.Collection;
import java.util.List;

public interface IItemTreeCategory {
    boolean contains(IItemTreeItem item);

    void addCategory(IItemTreeCategory category);

    void addItem(IItemTreeItem item);

    Collection<IItemTreeCategory> getSubCategories();

    Collection<List<IItemTreeItem>> getItems();

    String getName();

    int getCategoryOrder();

    int findCategoryOrder(String keyword);

    int findKeywordDepth(String keyword);
}
