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

package com.tencao.projectbalance.commands

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer

object CommandBase: CommandBase() {

    override fun getName() = "projectb"
    override fun getUsage(sender: ICommandSender?) = "com.tencao.projectbalance.commands.pb.usage"
    override fun getRequiredPermissionLevel() = 2
    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>) {
        if (args.isEmpty()) throw WrongUsageException(getUsage(sender))
        when (args[0]){
            "register" -> RegisterCMD.execute(server, sender, args.copyOfRange(1, args.size))
            "remove" -> RemoveCMD.execute(server, sender, args.copyOfRange(1, args.size))
            else -> throw WrongUsageException(getUsage(sender))
        }
    }
}