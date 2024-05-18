package com.example.nettydemo.adapter.handler;

import com.example.nettydemo.util.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RealInputHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            msg = StringUtil.realInput((String) msg);
        }
        super.channelRead(ctx, msg);
    }
}
