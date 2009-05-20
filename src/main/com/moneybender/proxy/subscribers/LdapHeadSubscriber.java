/*
 * Created on Oct 18, 2008
 *
 * Copyright (c), 2008 Don Branson.  All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.moneybender.proxy.subscribers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.moneybender.proxy.publishers.PacketHeader;

public class LdapHeadSubscriber extends AbstractSubscriber {

	private static final byte SEQUENCE = 0x30;
	private static final byte INTEGER = 0x02;
	private static final byte OCTECT_STRING = 0x04;
	private static final byte OID = 0x06;
	private static final byte SEQUENCE2 = 0x10;

	@Override
	protected void processPacket(PacketHeader header, byte[] payload, OutputStream out)
		throws IOException
	{
			PrintStream printStream = new PrintStream(out);
			
			printStream.println("Sender is " + header.getSourceType() + " at " + header.getSenderIP() + ":" + header.getSenderPort());
			printStream.println("Packet ID:" + header.getPacketIdentifier());
			
			printStream.println("Decoding LDAP packet of " + payload.length + " bytes.");
			
			try {
				dumpBerEncodedBuffer("", printStream, payload);
			} catch (IOException e) {
				printStream.println("ERROR: Exception decoding packet: " + e.getMessage());
			}
			printStream.println();
	}

	private void dumpBerEncodedBuffer(String indent, PrintStream printStream, byte[] buffer) throws IOException {

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer));

		do {
			byte dataType = in.readByte();
			int length = in.readByte();
			if(length < 1) {
				length = (byte) (length & 0x0F);
				if(length == 2)
					length = (byte) in.readShort();
				else
					throw new IOException("Cannot read length of " + length + " bytes");
			}
			if(length < 0) {
				throw new IOException("Found negative length of " + length + " for type " + Integer.toHexString(dataType));
			}
			byte[] subBuffer = new byte[length];
			in.read(subBuffer);

			switch (dataType) {
			case SEQUENCE:
			case SEQUENCE2:
				printStream.println(indent + "SEQUENCE [" + length + "]{");
				dumpBerEncodedBuffer(indent + "  ", printStream, subBuffer);
				printStream.println(indent + "}");
				break;
	
			case INTEGER:
				DataInputStream integerStream = new DataInputStream(new ByteArrayInputStream(subBuffer));
				int integerValue = -1;
				if(length == 1) {
					integerValue = integerStream.readByte();
				} else if(length == 2) {
					integerValue = integerStream.readShort();
				} else if(length == 3) {
					// TODO: Endian assumption made, go check it
					integerValue = integerStream.readByte() * 65536 + integerStream.readShort();
				} else if(length == 4) {
					integerValue = integerStream.readInt();
				}

				printStream.println(indent + "INTEGER [" + length + "] = " + integerValue);
				break;

			case OCTECT_STRING:
				printStream.println(indent + "OCTECT_STRING [" + length + "]");
				break;
				
			case OID:
				printStream.print(indent + "OID [" + length + "] = " );
				int i = 0;
				for(; i < subBuffer.length - 1; i++)
					printStream.print(subBuffer[i] + ".");
				printStream.println(subBuffer[i]);
				break;

			default:
				printStream.println(indent + "Unhandled type (0x" + Integer.toHexString(dataType) + ") [" + length + "]");
//				= " + new String(subBuffer));
				break;
			}
		} while(in.available() > 0);
	}

}
