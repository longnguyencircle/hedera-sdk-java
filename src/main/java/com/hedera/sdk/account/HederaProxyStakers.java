package com.hedera.sdk.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hederahashgraph.api.proto.java.AccountID;
import com.hederahashgraph.api.proto.java.AllProxyStakers;
/**
 * all of the accounts proxy staking to a given account, and the amounts proxy staked
 *
 */
public class HederaProxyStakers implements Serializable {
	final static Logger logger = LoggerFactory.getLogger(HederaProxyStakers.class);
	private static final long serialVersionUID = 1;

	/**
	 * the shard number (nonnegative) of the account being staked to
	 */
	public long shardNum = 0;

	/**
	 * the realm number (nonnegative) of the account being staked to
	 */
	public long realmNum = 0;

	/**
	 * a nonnegative number unique within its realm  of the account being staked to
	 */
	public long accountNum = 0;
	/**
	 * The list of {@link HederaProxyStakers} staking to this account
	 */
	public List<HederaProxyStaker> proxyStakers = new ArrayList<HederaProxyStaker>();
	/**
	 * Default constructor
	 */
	public HederaProxyStakers() {
	   	logger.trace("Start - Object init");
	   	logger.trace("End - Object init");
	}
	/**
	 * Construct from shard, realm and account number
	 * @param shardNum the shard number for the proxy stakers
	 * @param realmNum the realm number for the proxy stakers
	 * @param accountNum the account number for the proxy stakers
	 */
	public HederaProxyStakers(long shardNum, long realmNum, long accountNum) {
	   	logger.trace("Start - Object init in shard {}, realm {}. Account number {}", shardNum, realmNum, accountNum);
 		this.shardNum = shardNum;
		this.realmNum = realmNum;
		this.accountNum = accountNum;
		
	   	logger.trace("End - Object init");
	}
	/**
	 * Construct from a {@link AllProxyStakers} protobuf stream
	 * @param allProxyStakers protobuf
	 */
	public HederaProxyStakers(AllProxyStakers allProxyStakers) {
	   	logger.trace("Start - Object init in allProxyStakers {}", allProxyStakers);
		this.shardNum = allProxyStakers.getAccountID().getShardNum();
		this.realmNum = allProxyStakers.getAccountID().getRealmNum();
		this.accountNum = allProxyStakers.getAccountID().getAccountNum();
		
		proxyStakers.clear();
		for (int i=0; i < allProxyStakers.getProxyStakerCount(); i++) {
			proxyStakers.add(new HederaProxyStaker(allProxyStakers.getProxyStaker(i)));
		}
		
	   	logger.trace("End - Object init");
	}

	/**
	 * Generate a {@link AllProxyStakers} protobuf payload for this object
	 * @return {@link AllProxyStakers} 
	 */
	public AllProxyStakers getProtobuf() {
	   	logger.trace("Start - getProtobuf");
		
	   	AllProxyStakers.Builder allProxyStakers = AllProxyStakers.newBuilder();
		AccountID.Builder accountID = AccountID.newBuilder();
		
		accountID.setAccountNum(this.accountNum);
		accountID.setRealmNum(this.realmNum);
		accountID.setShardNum(this.shardNum);
		
		allProxyStakers.setAccountID(accountID);
		for (HederaProxyStaker proxyStaker : proxyStakers) {
			allProxyStakers.addProxyStaker(proxyStaker.getProtobuf());
		}
	   	
	   	logger.trace("End - getProtobuf");

		return allProxyStakers.build();
	}
	/**
	 * Adds a {@link HederaProxyStaker} to the list
	 * @param proxyStaker proxyStaker to add to the list
	 */
	public void addProxyStaker(HederaProxyStaker proxyStaker) {
		this.proxyStakers.add(proxyStaker);
	}
}