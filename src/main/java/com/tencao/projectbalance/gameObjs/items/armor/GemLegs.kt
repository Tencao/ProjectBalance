package com.tencao.projectbalance.gameObjs.items.armor

import com.google.common.base.Predicates
import gnu.trove.map.hash.TIntLongHashMap
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class GemLegs : GemArmorBase(EntityEquipmentSlot.LEGS) {

    private val lastJumpTracker = TIntLongHashMap()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack?, world: World?, list: MutableList<String>?, advanced: ITooltipFlag?) {
        list!!.add(I18n.format("pe.gem.legs.lorename"))
    }

    @SubscribeEvent
    fun onJump(evt: LivingEvent.LivingJumpEvent) {
        if (evt.entityLiving is EntityPlayer && evt.entityLiving.entityWorld.isRemote) {
            lastJumpTracker.put(evt.entityLiving.entityId, evt.entityLiving.entityWorld.totalWorldTime)
        }
    }

    private fun jumpedRecently(player: EntityPlayer): Boolean {
        return lastJumpTracker.containsKey(player.entityId) && player.entityWorld.totalWorldTime - lastJumpTracker.get(player.entityId) < 5
    }

    override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
        if (world.isRemote) {
            if (player.isSneaking && !player.onGround && player.motionY > -8 && !jumpedRecently(player)) {
                player.motionY -= 0.32
            }
        }

        if (player.isSneaking) {
            val box = AxisAlignedBB(player.posX - 3.5, player.posY - 3.5, player.posZ - 3.5, player.posX + 3.5, player.posY + 3.5, player.posZ + 3.5)
            WorldHelper.repelEntitiesInAABBFromPoint(world, box, player.posX, player.posY, player.posZ, true)

            if (!world.isRemote && player.motionY < -0.08) {
                val entities = player.entityWorld.getEntitiesInAABBexcluding(player,
                        player.entityBoundingBox.offset(player.motionX, player.motionY, player.motionZ).grow(2.0),
                        Predicates.instanceOf(EntityLivingBase::class.java))

                for (e in entities) {
                    if (e.canBeCollidedWith()) {
                        e.attackEntityFrom(DamageSource.causePlayerDamage(player), (-player.motionY).toFloat() * 6f)
                    }
                }
            }
        }
    }
}