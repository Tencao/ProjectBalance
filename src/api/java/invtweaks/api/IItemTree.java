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
import java.util.Random;

public interface IItemTree {
    void registerOre(String category, String name, String oreName, int order);

    boolean matches(List<IItemTreeItem> items, String keyword);

    boolean isKeywordValid(String keyword);

    Collection<IItemTreeCategory> getAllCategories();

    IItemTreeCategory getRootCategory();

    IItemTreeCategory getCategory(String keyword);

    boolean isItemUnknown(String id, int damage);

    List<IItemTreeItem> getItems(String id, int damage);

    List<IItemTreeItem> getItems(String name);

    IItemTreeItem getRandomItem(Random r);

    boolean containsItem(String name);

    boolean containsCategory(String name);

    void setRootCategory(IItemTreeCategory category);

    IItemTreeCategory addCategory(String parentCategory, String newCategory) throws NullPointerException;

    void addCategory(String parentCategory, IItemTreeCategory newCategory) throws NullPointerException;

    IItemTreeItem addItem(String parentCategory, String name, String id, int damage, int order)
            throws NullPointerException;

    void addItem(String parentCategory, IItemTreeItem newItem) throws NullPointerException;

    int getKeywordDepth(String keyword);

    int getKeywordOrder(String keyword);
}
