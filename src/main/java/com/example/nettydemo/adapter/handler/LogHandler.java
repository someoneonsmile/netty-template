package com.example.nettydemo.adapter.handler;

import com.example.nettydemo.constants.AttrKeys;
import com.example.nettydemo.util.IpUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.InetSocketAddress;

public class LogHandler extends ChannelDuplexHandler {

    protected final InternalLogger logger;
    protected final InternalLogLevel internalLevel;

    public LogHandler(LogLevel level) {
        this.logger = InternalLoggerFactory.getInstance(this.getClass());
        this.internalLevel = level.toInternalLevel();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel write, content=[{}]", msg);
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String logId = RandomStringUtils.randomAlphabetic(6) + ctx.channel().id().asShortText();
        ctx.channel().attr(AttrKeys.LOG_ID).set(logId);
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel registered");
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel unregistered");
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel active");
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel read, content=[{}]", msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel inactive");
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            log(ctx, "channel exception caught", cause);
        }
        super.exceptionCaught(ctx, cause);
    }

    private void log(ChannelHandlerContext ctx, String format, Object... args) {
        String logId = ctx.channel().attr(AttrKeys.LOG_ID).get();
        String ip = IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress());
        this.logger.log(this.internalLevel, "[{}], [{}], " + format, logId, ip, args);
    }
}
