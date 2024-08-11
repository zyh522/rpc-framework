package com.zyh.remoting.protocol.netty.codec;

import com.zyh.common.constants.RpcConstants;
import com.zyh.common.enums.CompressTypeEnum;
import com.zyh.common.enums.SerializationTypeEnum;
import com.zyh.common.factory.SingletonFactory;
import com.zyh.compress.Compress;
import com.zyh.compress.gzip.Gzip;
import com.zyh.remoting.dto.RpcMessage;
import com.zyh.serializer.Serializer;
import com.zyh.serializer.hessian.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  custom protocol decoder
 *  <pre>
 *    0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *    +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *    |   magic   code        |version | full length         |messageType| codec |compress|    RequestId       |
 *    +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *    |                                                                                                       |
 *    |                                         body                                                          |
 *    |                                                                                                       |
 *    |                                        ... ...                                                        |
 *    +-------------------------------------------------------------------------------------------------------+
 *  4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 *  1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 *  body（object类型数据）
 *  </pre>
 *
 * @author zhuyh
 * @version v1.0
 * @description 消息编码
 * @date 2024/8/7
 **/
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            // leave a place to write the value of full length
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // build full length
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = SingletonFactory.getInstance(HessianSerializer.class);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // compress the bytes
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                log.info("compress name: [{}] ", compressName);
                Compress compress = SingletonFactory.getInstance(Gzip.class);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }

    }


}