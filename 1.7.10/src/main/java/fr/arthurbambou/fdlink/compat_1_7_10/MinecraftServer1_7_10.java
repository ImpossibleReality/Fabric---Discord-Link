package fr.arthurbambou.fdlink.compat_1_7_10;

import fr.arthurbambou.fdlink.versionhelpers.minecraft.Message;
import fr.arthurbambou.fdlink.versionhelpers.minecraft.MessagePacket;
import fr.arthurbambou.fdlink.versionhelpers.minecraft.MinecraftServer;
import fr.arthurbambou.fdlink.versionhelpers.minecraft.PlayerEntity;
import fr.arthurbambou.fdlink.versionhelpers.minecraft.style.TextColor;
import net.minecraft.class_2432;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MinecraftServer1_7_10 implements MinecraftServer {

    private final net.minecraft.server.MinecraftServer minecraftServer;

    public MinecraftServer1_7_10(net.minecraft.server.MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
    }

    @Override
    public String getMotd() {
        return this.minecraftServer.getServerMotd();
    }

    @Override
    public int getPlayerCount() {
        return this.minecraftServer.getCurrentPlayerCount();
    }

    @Override
    public int getMaxPlayerCount() {
        return this.minecraftServer.getMaxPlayerCount();
    }

    @Override
    public List<PlayerEntity> getPlayers() {
        List<PlayerEntity> list = new ArrayList<>();
        for (Object playerEntity : this.minecraftServer.getPlayerManager().players) {
            list.add(new PlayerEntity1_7_10((ServerPlayerEntity) playerEntity));
        }
        return list;
    }

    @Override
    public void sendMessageToAll(MessagePacket messagePacket) {
        Message message = messagePacket.getMessage();
        Text text = null;
        if (message.getType() == Message.MessageObjectType.STRING) {
            text = new LiteralText(message.getMessage());
        } else {
            if (message.getTextType() == Message.TextType.LITERAL) {
                text = new LiteralText(message.getMessage());
            } else if (message.getTextType() == Message.TextType.TRANSLATABLE) {
                text = new TranslatableText(message.getKey(), message.getArgs());
            }
        }
        Style vanillaStyle = new Style();
        fr.arthurbambou.fdlink.versionhelpers.minecraft.style.Style compatStyle = message.getStyle();
        vanillaStyle = vanillaStyle
                .setBold(compatStyle.isBold())
                .setColor(Formatting.byName(TextColor.toFormatting(compatStyle.getColor()).getName()))
                .setItalic(compatStyle.isItalic())
                .setUnderline(compatStyle.isUnderlined())
                .setObfuscated(compatStyle.isObfuscated())
                .setStrikethrough(compatStyle.isStrikethrough());
        if (compatStyle.getClickEvent() != null) {
            vanillaStyle.setClickEvent(new ClickEvent(class_2432.method_9892(compatStyle.getClickEvent().getAction().getName()),
                    compatStyle.getClickEvent().getValue()));
        }
        this.minecraftServer.getPlayerManager().sendToAll(new GameMessageS2CPacket(text));
    }

    @Override
    public String getIp() {
        return this.minecraftServer.getServerIp();
    }

    @Override
    public File getIcon() {
        return this.minecraftServer.getFile("icon.png");
    }
}