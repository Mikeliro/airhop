package com.fuzs.airhop.network.messages;

import com.fuzs.airhop.AirHop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class MessageBase<T extends IMessage> implements IMessage, IMessageHandler<T, T> {

    @Override
    public T onMessage(T message, MessageContext context) {

        if (context.side == Side.CLIENT) {

            handleClientSide(message, AirHop.proxy.getClientPlayer());

        } else {

            handleServerSide(message, context.getServerHandler().playerEntity);

        }

        return null;

    }

    /**
     * Handle a packet on the client side. Note this occurs after decoding has completed.
     * @param message message to be handled
     * @param player the player reference
     */
    public abstract void handleClientSide(T message, EntityPlayer player);

    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     * @param message message to be handled
     * @param player the player reference
     */
    public abstract void handleServerSide(T message, EntityPlayer player);

}
