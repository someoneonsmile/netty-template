package com.example.nettydemo.adapter.handler;

import com.example.nettydemo.util.IpUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

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
            this.logger.log(this.internalLevel, "[{}], channel write, content=[{}]", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()), msg);
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, "[{}], channel registered", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()));
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, "[{}], channel unregistered", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()));
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, "[{}], channel active", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()));
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, "[{}], channel read, content=[{}]", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()), msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, "[{}], channel inactive", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()));
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, "[{}], channel exception caught", IpUtil.getIp((InetSocketAddress) ctx.channel().remoteAddress()), cause);
        }
        super.exceptionCaught(ctx, cause);
    }
}
