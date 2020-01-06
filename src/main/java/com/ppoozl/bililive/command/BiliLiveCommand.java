package com.ppoozl.bililive.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ppoozl.bililive.api.thread.BaseBiliLiveThread;
import com.ppoozl.bililive.api.thread.BiliLiveThreadFactory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BiliLiveCommand
{

    public BiliLiveCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("bilidm")
                        .requires(source->source.hasPermissionLevel(0))
                        .executes(BiliLiveCommand::bilidm)
                        .then(Commands.argument("action", StringArgumentType.word())
                                .executes(BiliLiveCommand::bilidmArg))
        );
    }

    private static TextComponent formatText(String translationKey, Object... args) {
        return new TranslationTextComponent(translationKey, args);
    }

    private static int bilidm(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        PlayerEntity player = context.getSource().asPlayer();
        player.sendMessage(formatText("BiliLive.provide.action"));
        return Command.SINGLE_SUCCESS;
    }

    private static int bilidmArg(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        PlayerEntity player = context.getSource().asPlayer();
        String arg = StringArgumentType.getString(context , "action" );
            bilidmAction(arg,player);
        return Command.SINGLE_SUCCESS;
    }

    public static void bilidmAction(String arg, PlayerEntity player){
        switch (arg) {
            // 重载指令
            case "restart": {
                BaseBiliLiveThread.sendChatMessage("§8§l正在重启中……");
                BiliLiveThreadFactory.restartThreads();
                break;
            }

            // 开始指令
            case "start": {

                if (!BiliLiveThreadFactory.getRunningBiliLiveThread().isEmpty()) {
                    BaseBiliLiveThread.sendChatMessage(TextFormatting.RED + "弹幕姬已处于运行状态！");
                } else {
                    BiliLiveThreadFactory.restartThreads();
                }

                break;
            }

            // 停止指令
            case "stop": {
                if (BiliLiveThreadFactory.getRunningBiliLiveThread().isEmpty()) {
                    BaseBiliLiveThread.sendChatMessage(TextFormatting.RED + "弹幕姬已停止！");
                } else {
                    BaseBiliLiveThread.sendChatMessage("§8§l正在停止中……");
                    BiliLiveThreadFactory.stopAllThreads();
                    BaseBiliLiveThread.sendChatMessage("§8§l弹幕姬已停止。");
                }
                break;
            }

            // 列出正在运行的所有弹幕线程
            case "running": {
                BaseBiliLiveThread.sendChatMessage("正在运行的弹幕线程：" + String.join("，", BiliLiveThreadFactory.getRunningBiliLiveThread()));
                break;
            }

            // 帮助指令
            case "help": {
                player.sendMessage(formatText("bililive.provide.action"));
                break;
            }

            default:
                player.sendMessage(formatText("bililive.provide.action"));
                break;
        }
    }
}
