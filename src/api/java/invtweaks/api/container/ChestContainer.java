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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker for containers that have a chest-like persistant storage component. Enables the Inventroy Tweaks sorting
 * buttons for this container.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChestContainer {
    // Set to true if the Inventory Tweaks sorting buttons should be shown for this container.
    boolean showButtons() default true;

    // Size of a chest row
    int rowSize() default 9;

    // Uses 'large chest' mode for sorting buttons
    // (Renders buttons vertically down the right side of the GUI)
    boolean isLargeChest() default false;

    // Annotation for method to get size of a chest row if it is not a fixed size for this container class
    // Signature int func()
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface RowSizeCallback {
    }

    // Annotation for method to get size of a chest row if it is not a fixed size for this container class
    // Signature int func()
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface IsLargeCallback {
    }
}
