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

import be.bluexin.saomclib.message
import be.bluexin.saomclib.packets.PacketPipeline
import com.tencao.projectbalance.config.MapperConfig
import com.tencao.projectbalance.mapper.Defaults
import com.tencao.projectbalance.mapper.Graph
import com.tencao.projectbalance.network.SyncComplexityPacket
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

object RegisterCMD: CommandBase() {
    override fun getName() = "register"
    override fun getUsage(sender: ICommandSender?) = "commands.pb-register.usage"
    override fun getRequiredPermissionLevel() = 2

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>) {
        if (args.isEmpty() ) throw WrongUsageException("commands.pb-register.usage")
        val player = (sender as EntityPlayer)
        if (player.heldItemMainhand.isEmpty) throw CommandException("commands.noitemerror")
        Defaults.registerStack(player.heldItemMainhand, args[0].toInt())
        MapperConfig.saveGraph()
        Graph.clean()
        PacketPipeline.sendToAll(SyncComplexityPacket())
        player.message("commands.pb-register.success")
    }
}