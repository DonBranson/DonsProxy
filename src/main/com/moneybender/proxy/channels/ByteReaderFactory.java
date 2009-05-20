/*
 * Created on October 20, 2007
 * 
 * Copyright (c), 2007 Don Branson.  All Rights Reserved.
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
package com.moneybender.proxy.channels;

import org.apache.log4j.Logger;

import com.moneybender.proxy.channels.decorators.ConnectionLossDecorator;
import com.moneybender.proxy.channels.decorators.LatencyDecorator;
import com.moneybender.proxy.channels.decorators.PacketLossDecorator;
import com.moneybender.proxy.channels.decorators.ThrottleDecorator;
import com.moneybender.proxy.management.DecoratorControl;
import com.moneybender.proxy.management.MBeanManager;

public class ByteReaderFactory {

	private static Logger log = Logger.getLogger(ByteReaderFactory.class);

	public static final String LATENCY_PROPERTY_NAME = "latency.millis";
	public static final String PACKET_LOSS_PROPERTY_NAME = "packet.loss.rate";
	public static final String THROTTLE_PROPERTY_NAME = "bandwidth.throttle";
	public static final String CONNECTION_LOSS_PROPERTY_NAME = "enable.connection.loss";

	public IReadBytes getByteReader(DecoratorControl decoratorControl, MBeanManager mbeanManager){
		IReadBytes byteReader = new ByteReader();

		byteReader = checkForLatencyOption(byteReader, decoratorControl);
		byteReader = checkForPacketLossOption(byteReader, decoratorControl);
		byteReader = checkForBandwidthLimit(byteReader, decoratorControl);
		byteReader = checkForConnectionLoss(byteReader, decoratorControl);
		
		return byteReader;
	}

	private IReadBytes checkForLatencyOption(IReadBytes byteReader, DecoratorControl decoratorControl) {
		String latencyProperty = System.getProperty(LATENCY_PROPERTY_NAME);
		if(latencyProperty != null){
			int latency = Integer.parseInt(latencyProperty.trim());
			String readerState = "Injecting a latency of " + latency + " milliseconds.";
			log.info(readerState);
			byteReader = new LatencyDecorator(byteReader, latency);

			decoratorControl.setLatencyDecoratorState(readerState);
		}
		return byteReader;
	}

	private IReadBytes checkForPacketLossOption(IReadBytes byteReader, DecoratorControl decoratorControl) {
		String packetLossRate = System.getProperty(PACKET_LOSS_PROPERTY_NAME);
		if(packetLossRate != null){
			int lossRate = Integer.parseInt(packetLossRate.trim());
			String readerState = "Dropping 1 of every " + packetLossRate + " packets.";
			log.info(readerState);
			byteReader = new PacketLossDecorator(byteReader, lossRate);

			decoratorControl.setPacketLossDecoratorState(readerState);
		}
		return byteReader;
	}

	private IReadBytes checkForBandwidthLimit(IReadBytes byteReader, DecoratorControl decoratorControl) {
		String kilobytesPerSecond = System.getProperty(THROTTLE_PROPERTY_NAME);
		if(kilobytesPerSecond != null){
			int lossRate = Integer.parseInt(kilobytesPerSecond.trim());
			String readerState = "Limiting bandwidth to " + kilobytesPerSecond + " kilobytes/second.";
			log.info(readerState);
			byteReader = new ThrottleDecorator(byteReader, lossRate);

			decoratorControl.setThrottleDecoratorState(readerState);
		}
		return byteReader;
	}

	private IReadBytes checkForConnectionLoss(IReadBytes byteReader, DecoratorControl decoratorControl) {
		String connectionLossProperty = System.getProperty(CONNECTION_LOSS_PROPERTY_NAME);
		if(connectionLossProperty != null){
			if(Boolean.parseBoolean(connectionLossProperty.trim())){
				String readerState = "Connection loss enabled.  Call ConnectionLossDecorator.networkOn() and " +
					"ConnectionLossDecorator.networkOff() to toggle.";
				log.info(readerState);
				byteReader = new ConnectionLossDecorator(byteReader);

				decoratorControl.setConnectionLossDecoratorState(readerState);
			}
		}
		return byteReader;
	}

}
