/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.db.protocol.postgresql.payload;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import io.netty.buffer.CompositeByteBuf;
import lombok.Getter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.db.protocol.payload.PacketPayload;

import java.nio.charset.Charset;

/**
 * Payload operation for PostgreSQL packet data types.
 *
 * @see <a href="https://www.postgresql.org/docs/current/protocol-message-types.html">Message Data Types</a>
 */
@RequiredArgsConstructor
@Getter
public final class PostgreSQLPacketPayload implements PacketPayload {
    
    private final ByteBuf byteBuf;
    
    private final Charset charset;
    
    /**
     * Read 1 byte fixed length integer from byte buffers.
     *
     * @return 1 byte fixed length integer
     */
    public int readInt1() {
        return byteBuf.readUnsignedByte();
    }
    
    /**
     * Write 1 byte fixed length integer to byte buffers.
     *
     * @param value 1 byte fixed length integer
     */
    public void writeInt1(final int value) {
        byteBuf.writeByte(value);
    }
    
    public void writeUUID(final UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
    }
    
    /**
     * Read 2 byte fixed length integer from byte buffers.
     *
     * @return 2 byte fixed length integer
     */
    public int readInt2() {
        System.out.println("byteBuf" + byteBuf);
        return byteBuf.readUnsignedShort();
    }
    
    /**
     * Write 2 byte fixed length integer to byte buffers.
     *
     * @param value 2 byte fixed length integer
     */
    public void writeInt2(final int value) {
        byteBuf.writeShort(value);
    }
    
    /**
     * Read 4 byte fixed length integer from byte buffers.
     *
     * @return 4 byte fixed length integer
     */
    public int readInt4() {
        return byteBuf.readInt();
    }
    
    /**
     * Write 4 byte fixed length integer to byte buffers.
     *
     * @param value 4 byte fixed length integer
     */
    public void writeInt4(final int value) {
        byteBuf.writeInt(value);
    }
    
    /**
     * Read 8 byte fixed length integer from byte buffers.
     *
     * @return 8 byte fixed length integer
     */
    public long readInt8() {
        return byteBuf.readLong();
    }
    
    /**
     * Write 8 byte fixed length integer to byte buffers.
     *
     * @param value 8 byte fixed length integer
     */
    public void writeInt8(final long value) {
        byteBuf.writeLong(value);
    }
    
    /**
     * Write variable length bytes to byte buffers.
     * 
     * @param value fixed length bytes
     */
    public void writeBytes(final byte[] value) {
        byteBuf.writeBytes(value);
    }
    
    /**
     * Bytes before zero.
     *
     * @return the number of bytes before zero
     */
    public int bytesBeforeZero() {
        return byteBuf.bytesBefore((byte) 0);
    }
    
    /**
     * Read null terminated string from byte buffers.
     * 
     * @return null terminated string
     */
    public String readStringNul() {
        String result = byteBuf.readCharSequence(byteBuf.bytesBefore((byte) 0), charset).toString();
        byteBuf.skipBytes(1);
        return result;
    }
    
    /**
     * Write null terminated string to byte buffers.
     * 
     * @param value null terminated string
     */
    public void writeStringNul(final String value) {
        byteBuf.writeBytes(value.getBytes(charset));
        byteBuf.writeByte(0);
    }
    
    /**
     * Write rest of packet string to byte buffers.
     * 
     * @param value rest of packet string
     */
    public void writeStringEOF(final String value) {
        byteBuf.writeBytes(value.getBytes(charset));
    }
    
    /**
     * Skip reserved from byte buffers.
     * 
     * @param length length of reserved
     */
    public void skipReserved(final int length) {
        byteBuf.skipBytes(length);
    }
    
    /**
     * Check if there has complete packet in ByteBuf.
     * PostgreSQL Message: (byte1) message type + (int4) length + (length - 4) payload
     *
     * @return has complete packet
     */
    public boolean hasCompletePacket() {
        return byteBuf.readableBytes() >= 5 && byteBuf.readableBytes() - 1 >= byteBuf.getInt(byteBuf.readerIndex() + 1);
    }
    
    @Override
    public void close() {
        if (byteBuf instanceof CompositeByteBuf) {
            int remainBytes = byteBuf.readableBytes();
            if (remainBytes > 0) {
                byteBuf.skipBytes(remainBytes);
            }
            ((CompositeByteBuf) byteBuf).discardReadComponents();
        }
        byteBuf.release();
    }
}
