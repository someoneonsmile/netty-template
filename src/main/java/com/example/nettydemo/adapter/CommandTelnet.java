package com.example.nettydemo.adapter;

import com.example.nettydemo.adapter.handler.LogHandler;
import com.example.nettydemo.adapter.handler.RealInputHandler;
import com.example.nettydemo.constants.CommonConst;
import com.example.nettydemo.constants.EnvConst;
import com.example.nettydemo.statemachine.CommandStatemachine;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class CommandTelnet implements InitializingBean, DisposableBean {

    @Resource
    private EnvConst envConst;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private Channel bootstrapChannel;

    public static final AttributeKey<CommandStatemachine> stKey = AttributeKey.valueOf("statemachine");

    @Override
    public void afterPropertiesSet() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("line-base-decoder", new LineBasedFrameDecoder(1024));
                        pipeline.addLast("decoder", new StringDecoder(StandardCharsets.UTF_8));
                        pipeline.addLast("encoder", new StringEncoder(Charset.forName("GBK")));
                        pipeline.addLast("real-input", new RealInputHandler());
                        pipeline.addLast("idle", new IdleStateHandler(0, 0, 6000, TimeUnit.MILLISECONDS));
                        pipeline.addLast("log", new LogHandler(LogLevel.DEBUG));
                        pipeline.addLast("handler", new SimpleChannelInboundHandler<String>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                Channel channel = ctx.channel();
                                CommandStatemachine statemachine = new CommandStatemachine();
                                channel.attr(stKey).set(statemachine);
                                ctx.writeAndFlush(statemachine.execute(CommandStatemachine.Command.HELP.getName()));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                CommandStatemachine commandStatemachine = ctx.channel().attr(stKey).get();
                                String resp = commandStatemachine.execute(msg);
                                if (StringUtils.isNotBlank(resp)) {
                                    ctx.writeAndFlush(resp);
                                }
                                if (commandStatemachine.isTerminated()) {
                                    ctx.close();
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                CommandStatemachine statemachine = ctx.channel().attr(stKey).get();
                                ctx.writeAndFlush("Error: " + cause + CommonConst.BR + statemachine.prompt());
                            }
                        });
                    }
                });
        // 绑定监听端口, 等待绑定完成
        ChannelFuture bootstrapChannelFuture = serverBootstrap.bind(envConst.getAdapterCommandPort()).sync();
        bootstrapChannel = bootstrapChannelFuture.channel();
        log.info("CommandTelnet listening on port {}", envConst.getAdapterCommandPort());
    }

    @Override
    public void destroy() throws Exception {
        // 优雅关闭事件循环组
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bootstrapChannel != null) {
            bootstrapChannel.closeFuture().sync();
        }
    }
}
