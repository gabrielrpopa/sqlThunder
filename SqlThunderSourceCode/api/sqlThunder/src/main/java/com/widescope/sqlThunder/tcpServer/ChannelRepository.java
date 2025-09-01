package com.widescope.sqlThunder.tcpServer;

import com.widescope.sqlThunder.tcpServer.types.JSONResponse;
import com.widescope.sqlThunder.tcpServer.types.UserChannel;
import io.netty.channel.ChannelId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelRepository {

    /*userName/email to a list of ChannelId the user can potentially have from multiple devices, needed to avoid search/scanning*/
    private static final ConcurrentMap<String, List<ChannelId>> userChannelIdCache = new ConcurrentHashMap<>();
    /*ChannelId to UserChannel*/
    private static final ConcurrentMap<io.netty.channel.ChannelId, UserChannel> channelIdCache = new ConcurrentHashMap<>();


    public static void closeChannel(final io.netty.channel.Channel channel) {
        if(channelIdCache.containsKey(channel.id())) {
            UserChannel uChannel = channelIdCache.get(channel.id()); /*get UserChannel first from list of channels*/
            userChannelIdCache.get(uChannel.getUserName()).remove(uChannel.getChannel().id()); /*remove channel from user list*/
            if (userChannelIdCache.get(uChannel.getUserName()).isEmpty()) { /*if user has no more channels in channel list, remote user */
                userChannelIdCache.remove(uChannel.getUserName());
            }
            channelIdCache.remove(channel.id()); /*remove channel from list of channels*/
        } else {
            System.out.println(channel.id() + " did not login");
        }
    }

    public static void openChannel(final io.netty.channel.Channel channel, final String userName) {
        channelIdCache.putIfAbsent(channel.id(), new UserChannel(channel, userName));
        if(userChannelIdCache.containsKey(userName)) {
            userChannelIdCache.get(userName).add(channel.id());
        } else {
            List<ChannelId> newLst = new ArrayList<>();
            newLst.add(channel.id());
            userChannelIdCache.put(userName, newLst);
        }
    }

    public static boolean isChannelApproved(final io.netty.channel.Channel channel) {
        return channelIdCache.containsKey(channel.id());
    }

    /*Send TCP notification to all devices connected for a particular user */
    public static boolean sendTcpMessageToUserChannel(final String userName, final JSONResponse t) {
        System.out.println("sendTcpMessageToUserChannel Message to: " + userName);
        AtomicBoolean ret = new AtomicBoolean(false);
        userChannelIdCache.get(userName).forEach(channelId -> {
            System.out.println("sendTcpMessageToUserChannel on channel: " + channelId);
            channelIdCache.get(channelId).getChannel().writeAndFlush(t.toString());
            ret.set(true);
        });

        return ret.get();
    }

}
