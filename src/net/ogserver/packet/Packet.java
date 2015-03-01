package net.ogserver.packet;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import net.ogserver.tcp.Session;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/*
* Copyright (c) 2015
* Christian Tucker.  All rights reserved.
*
* The use of OGServer is free of charge for personal use. 
*
* Commercial users are required to purchase a commercial license from
* the OGServer website at http://ogserver.net/
*
* Commercial usage is defined by the amount of concurrent connections
* that the server is handling at any given time. Once the server reaches
* a state in which it is handling an average of 10 connections, your 
* application is classified as 'Commercial'.
*
* Personal usage is only condoned if the following conditions are met:
*
* 1. It is required to mention the use of OGServer in your project, either
*    through an opening or closing splash-screen lasting a minimum 3 seconds.
*    This 'screen' is provided in the OGServer package.
*
* THIS SOFTWARE IS PROVIDED 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
* BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE DISCLAIMED.  
* IN NO EVENT SHALL THE AUTHOR  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
* THE POSSIBILITY OF SUCH DAMAGE.
*/

/**
 * A {@link Packet} is identified by a {@link PacketOpcode} and is used to invoke
 * methods in an organized manner using information sent over the network.
 * 
 * @author Christian Tucker
 */
public abstract class Packet {

	/**
	 * Contains a collection of {@link Packet}'s which are linked directly to
	 * their respective {@link PacketOpcode} for easy lookup.
	 */
	private static Map<Integer, Packet> packets = new HashMap<>();

	/**
	 * <strong>For internal usage only.</strong> The _decode method is used to process
	 * the {@link PacketOpcode} from the ByteBuffer, find the associated {@link Packet}
	 * and invoke the {@link Packet#decode(Session)} method on it.
	 * 
	 * @param session	The {@link Session} relative to this {@link Packet}.
	 */
	public static void _decode(Session session) {
		int packetOpcode = session.getInputBuffer().getInt();
		try { 
			packets.get(packetOpcode).decode(session);
		} catch (NullPointerException npe) {
			System.err.println("A packet could not be found with the opcode of: " + packetOpcode);
		}
	}
	
	/**
	 * Sends a packet to all connected accounts.
	 * 
	 * @param type			The type of packet.
	 * @param packetId		The id of the packet.
	 * @param data			The object[] of data.
	 */
	public static void sendGlobal(PacketType type, int packetId, Object... data) {
		Session.getSessions().forEach(u -> {
			Packet.send(type, u.getChannel(), packetId, data);
		});
	}
	
	/**
	 * Sends a packet to all connected accounts, albeit
	 * the account with the specified account id.
	 * 
	 * @param type			The type of packet.
	 * @param accountId		The id of the account to ignore.
	 * @param packetId		The id of the packet.
	 * @param data			The object[] of data.
	 */
	public static void sendGlobalAlbeit(PacketType type, int accountId, int packetId, Object... data) {
		Session.getSessions().forEach(u -> {
			Packet.send(type, u.getChannel(), packetId, data);
		});
	}
	
	/**
	 * Sends a packet to the specified connection.
	 * 
	 * @param connection		The connection to send the packet to.
	 * @param packetId			The id of the packet.
	 * @param data				The object[] of data.
	 */
	public static void send(PacketType type, SocketChannel channel, int packetId, Object... data) {
		ByteArrayDataOutput preBuffer = ByteStreams.newDataOutput();
		ByteArrayDataOutput postBuffer = ByteStreams.newDataOutput();
		postBuffer.writeInt((int)packetId);
		try {
			if(data.length > 0) {
				for(Object o : data) {
					if(o instanceof Integer) {
						postBuffer.writeInt((int)o);
					} else if(o instanceof Character) {
						postBuffer.writeChar((char)o);
					} else if(o instanceof String) {
						String string = (String)o;
						char[] charArray = string.toCharArray();
						int length = charArray.length;
						postBuffer.writeInt(length);
						for(int i = 0; i < length; i++) {
							postBuffer.writeChar(charArray[i]);
						}
					} else if(o instanceof Double) {
						postBuffer.writeDouble((double)o);
					} else if(o instanceof Float) {
						postBuffer.writeFloat((float)o);
					} else if(o instanceof Boolean) {
						postBuffer.writeByte((byte)(((boolean)o) ? 1 : 0));
					} else if(o instanceof Byte) {
						postBuffer.writeByte((byte)o);
					} else if(o instanceof Long) {
						postBuffer.writeLong((long)o);
					}
					else {
					}
				}
			} 
			preBuffer.writeInt(postBuffer.toByteArray().length);
			preBuffer.write(postBuffer.toByteArray());
			ByteBuffer writable = ByteBuffer.wrap(preBuffer.toByteArray());
			channel.write(writable);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The type of the packet.
	 * 
	 * @author Christian
	 */
	public enum PacketType {
		TCP, UDP
	}
	
	/**
	 * Used to process data specific to an {@link PacketOpcode}.
	 * 
	 * @param session	The {@link Session} relative to this {@link Packet}.
	 */
	public abstract void decode(Session session);
	
	/**
	 * Returns a collection of {@link Packet}'s relative to their {@link PacketOpcode}'s.
	 * 
	 * @return	The collection.
	 */
	public static Map<Integer, Packet> getPackets() {
		return packets;
	}
	
	/**
	 * Adds a {@link Packet} to the {@link #packets} collection.
	 * 
	 * @param packet	The packet.
	 */
	public static void add(Packet packet) {
		if(packet.getClass().getAnnotation(PacketOpcode.class) == null) {
			System.err.println("Packet: " + packet + " does not contain a PacketOpcodeHeader!");
			return;
		}
		packets.put(packet.getClass().getAnnotation(PacketOpcode.class).value(), packet);
		System.out.println("Packet: " + packet + " was successfully added to processing queue.");
	}
}