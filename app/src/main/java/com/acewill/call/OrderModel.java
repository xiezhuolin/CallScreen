package com.acewill.call;

import java.util.ArrayList;

public class OrderModel {
	public String msg;
	public boolean success ;
	public ArrayList<String> workingOrders ;
	public ArrayList<CallOrder> callOrders ;
	public ArrayList<String> finishedOrders ;

	@Override
	public String toString() {
		return "OrderModel{" +
				"msg='" + msg + '\'' +
				", success=" + success +
				", workingOrders=" + workingOrders +
				", callOrders=" + callOrders +
				", finishedOrders=" + finishedOrders +
				'}';
	}

	public class CallOrder{
		public String fetchID;//": "89", 
		public long callTime;//": 1481683136180

		@Override
		public String toString() {
			return "CallOrder{" +
					"fetchID='" + fetchID + '\'' +
					", callTime=" + callTime +
					'}';
		}
	}
}
