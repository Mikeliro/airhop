package com.fuzs.airhop.network;

import com.fuzs.airhop.AirHop;
import com.fuzs.airhop.network.message.IMessage;
import com.fuzs.airhop.network.message.SimpleHopMessage;
import com.fuzs.airhop.network.message.SyncHopMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@SuppressWarnings("unused")
public final class NetworkHandler {

    private static final NetworkHandler INSTANCE = new NetworkHandler();
    private final String PROTOCOL_VERSION = Integer.toString(1);
    private final SimpleChannel MAIN_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(AirHop.MODID, "main_channel"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private int discriminator;

    private NetworkHandler() {
    }

    public void init() {

        this.registerMessage(new SimpleHopMessage());
        this.registerMessage(new SyncHopMessage());
    }

    private <T extends IMessage> void registerMessage(final T message) {

        MAIN_CHANNEL.registerMessage(this.discriminator++, message.getClass(), IMessage::writePacketData, message::readPacketData, (msg, side) -> {

            NetworkEvent.Context ctx = side.get();
            ctx.setPacketHandled(true);
            if (ctx.getDirection().getOriginationSide().equals(msg.getExecutionSide())) {

                AirHop.LOGGER.error("Receiving {} at wrong side!", msg.getClass().getSimpleName());
                return;
            }

            ctx.enqueueWork(() -> msg.processPacket(ctx.getSender()));
        });
    }

    public void sendToServer(final IMessage message) {

        MAIN_CHANNEL.sendToServer(message);
    }

    public void sendTo(final IMessage message, ServerPlayerEntity player) {

        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public void sendToAll(final IMessage message) {

        MAIN_CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    public static NetworkHandler getInstance() {

        return INSTANCE;
    }

}