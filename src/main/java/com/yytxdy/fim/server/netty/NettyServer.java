package com.yytxdy.fim.server.netty;

import com.yytxdy.fim.protocol.Fim;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    @Value("${fim.nettyPort}")
    private Integer nettyPort;
    @Autowired
    private HeartbeatHandler heartbeatHandler;
    @Autowired
    private ServerHandler serverHandler;

    public void run() {
        //boss线程监听端口，worker线程负责数据读写
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置线程池
            bootstrap.group(boss, worker);
            //设置socket工厂
            bootstrap.channel(NioServerSocketChannel.class);
            //设置管道工厂
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addFirst(new IdleStateHandler(
                            10 * 60 * 1000, 10 * 60 * 1000, 0, TimeUnit.MILLISECONDS));
                    // 处理半包问题
                    pipeline.addLast(new ProtobufVarint32FrameDecoder());
                    // 添加解码器
                    pipeline.addLast(new ProtobufDecoder(Fim.Protocol.getDefaultInstance()));
                    // 处理半包问题
                    pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                    // 添加编码器
                    pipeline.addLast(new ProtobufEncoder());
                    pipeline.addLast(serverHandler);
                    pipeline.addLast(heartbeatHandler);
                }
            });
            //设置TCP参数
            //1.链接缓冲池的大小（ServerSocketChannel的设置）
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //维持链接的活跃，清除死链接(SocketChannel的设置)
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //关闭延迟发送
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            //绑定端口
            ChannelFuture future = bootstrap.bind(1313).sync();
            logger.info("server start ...... ");
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("server stop ", e);
        } finally {
            //优雅退出，释放线程池资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}