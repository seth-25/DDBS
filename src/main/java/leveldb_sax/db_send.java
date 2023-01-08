package leveldb_sax;

import com.distributed.domain.Constants;
import com.distributed.domain.InstructRun;
import com.distributed.domain.Parameters;
import com.distributed.util.InstructUtil;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

import java.util.List;

public class db_send {

    //javap -verbose db_send.class
    public static void send_edit(byte[] edit) {
        //第一个字节为0， 发送versionid 4字节, amV_id 4字节, number 8字节，saxt_smallest 8字节，saxt_biggest 8字节，startTime 8字节，endTime 8字节，

        //第一个字节为1，发送versionid 4字节， 删除的个数n1 4字节，(saxt_smallest 8字节，saxt_biggest 8字节，startTime 8字节，endTime 8字节) * n1
        //增加的个数n2 4字节，(number 8字节，saxt_smallest 8字节，saxt_biggest 8字节，startTime 8字节，endTime 8字节) * n2

        InstructClient instructionClient = new InstructClient(Parameters.hostName, Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = instructionClient.start();
        InstructRun instructrun = InstructUtil.buildInstructRun(Constants.InstructionType.SEND_VERSION, new Pair<>(edit, Parameters.hostName));
        //发送信息
        System.out.println("给" + Parameters.hostName +"发送指令 " + Constants.InstructionType.SEND_VERSION);
        channelFuture.channel().writeAndFlush(instructrun);
        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        instructionClient.close();
        System.out.println("send_edit");

    }

    //返回至多k个
    public static byte[] find_tskey(byte[] info) {
        // info ts 256*4，starttime 8， endtime 8， k 4, 还要多少个 4, topdist 4, 要查的个数n 4，p * n 8*n
        // 返回至多k个ares 1040一个 见db
        byte[] a = new byte[10];
        System.out.println("find_tskey");
        return a;
    }


}
