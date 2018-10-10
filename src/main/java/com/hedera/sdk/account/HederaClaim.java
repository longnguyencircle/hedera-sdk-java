package com.hedera.sdk.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.hedera.sdk.common.HederaAccountID;
import com.hedera.sdk.common.HederaKey;
import com.hedera.sdk.common.HederaKeySignature;
import com.hedera.sdk.common.Utilities;
import com.hederahashgraph.api.proto.java.Claim;
import com.hederahashgraph.api.proto.java.KeyList;
/**
 * A hash (presumably of some kind of credential or certificate), along with a list of threshold keys. 
 * Each of them must reach its threshold when signing the transaction, to attach this claim to this account. 
 * At least one of them must reach its threshold to delete this Claim from this account. 
 * This is intended to provide a revocation service: all the authorities agree to attach the hash, 
 * to attest to the fact that the credential or certificate is valid. 
 * Any one of the authorities can later delete the hash, to indicate that the credential has been revoked. 
 * In this way, any client can prove to a third party that any particular account has certain credentials, 
 * or to identity facts proved about it, and that none of them have been revoked yet.
 *
 */
public class HederaClaim implements Serializable {
	final Logger logger = LoggerFactory.getLogger(HederaClaim.class);
	private static final long serialVersionUID = 1;

	/**
	 * the shard number for the account to which the claim exists
	 */
	public long shardNum = 0;
	/**
	 * the realm number for the account to which the claim exists
	 */
	public long realmNum = 0;
	/**
	 * the account number for the account to which the claim exists
	 */
	public long accountNum = 0;
	/**
	 * the hash of the claim
	 */
	public byte[] hash = new byte[0];
	/**
	 * List of {@link HederaKey} to attach to this claim
	 * Note, these are mutually exclusive with keySignatures.
	 * If keySignatures exist, keys will be ignored in any processing
	 */
	public List<HederaKey> keys = new ArrayList<HederaKey>();
	/**
	 * List of {@link HederaKeySignature} to attach to this claim
	 * Note, these are mutually exclusive with keys.
	 * If keySignatures exist, they will have priority over keys
	 */
	public List<HederaKeySignature> keySignatures = new ArrayList<HederaKeySignature>();
	
	/**
	 * Default constructor.
	 */
	public HederaClaim() {
	   	logger.trace("Start - Object init");
	   	logger.trace("End - Object init");
	}
	/**
	 * Constructor from shard, realm, account numbers and hash
	 * @param shardNum the shard number for the claim
	 * @param realmNum the realm number for the claim
	 * @param accountNum the account number for the claim
	 * @param hash the claim hash
	 */
	public HederaClaim(long shardNum, long realmNum, long accountNum, byte[] hash) {
	   	logger.trace("Start - Object init shardNum {}, realmNum {}, accountNum {}, hash {}", shardNum, realmNum, accountNum, hash);
		this.shardNum = shardNum;
		this.realmNum = realmNum;
		this.accountNum = accountNum;
		this.hash = hash;
	   	logger.trace("End - Object init");
	}
	/**
	 * Constructor from {@link HederaAccountID} and hash
	 * @param accountID the account ID
	 * @param hash the claim hash
	 */
	public HederaClaim(HederaAccountID accountID, byte[] hash) {
	   	logger.trace("Start - Object init shardNum {}, realmNum {}, accountNum {}, hash {}", shardNum, realmNum, accountNum, hash);
		this.shardNum = accountID.shardNum;
		this.realmNum = accountID.realmNum;
		this.accountNum = accountID.accountNum;
		this.hash = hash;
	   	logger.trace("End - Object init");
	}

	/**
	 * Construct from a {@link Claim} protobuf
	 * @param claim the claim
	 */
	public HederaClaim(Claim claim) {
	   	logger.trace("Start - Object init in claim", claim);
		this.shardNum = claim.getAccountID().getShardNum();
		this.realmNum = claim.getAccountID().getRealmNum();
		this.accountNum = claim.getAccountID().getAccountNum();

		// hash
		this.hash = claim.getHash().toByteArray();
		
		// keys
		this.keys.clear();
		this.keySignatures.clear();
		
		for (int i=0; i < claim.getKeys().getKeysCount(); i++) {
			HederaKey key = new HederaKey(claim.getKeys().getKeys(i));
			HederaKeySignature keySig = new HederaKeySignature(key.getKeyType(), key.getKey(), new byte[0]);
			this.keys.add(key);
			this.keySignatures.add(keySig);
		}
		
	   	logger.trace("End - Object init");
	}

	/**
	 * Generate a {@link Claim} protobuf payload for this object 
	 * @return {@link Claim}
	 */
	public Claim getProtobuf() {
	   	logger.trace("Start - getProtobuf");
		
	   	Claim.Builder protobuf = Claim.newBuilder();
		HederaAccountID accountID = new HederaAccountID(this.shardNum, this.realmNum, this.accountNum);
		protobuf.setAccountID(accountID.getProtobuf());
		protobuf.setHash(ByteString.copyFrom(this.hash));
		
		KeyList protoKeyList;
		
		if (!this.keySignatures.isEmpty()) {
			protoKeyList = Utilities.getProtoKeyFromKeySigList(this.keySignatures);
		} else {
			protoKeyList = Utilities.getProtoKeyList(this.keys);
		}
		protobuf.setKeys(protoKeyList);
		
	   	logger.trace("End - getProtobuf");

		return protobuf.build();
	}
	/**
	 * Adds a {@link HederaKey} to the list
	 * @param key the key to add
	 */
	public void addKey(HederaKey key) {
	   	logger.trace("Start - addKey key {}", key);
		this.keys.add(key);
	   	logger.trace("End - addKey");
	}
	/**
	 * Adds a {@link HederaKeySignature} to the list
	 * @param keySigPair the key signature pair to add
	 */
	public void addKeySignaturePair(HederaKeySignature keySigPair) {
	   	logger.trace("Start - addKey keySigPair {}", keySigPair);
		this.keySignatures.add(keySigPair);
	   	logger.trace("End - addKey");
	}
	/**
	 * Deletes a {@link HederaKey} from the list
	 * @param key the key to delete
	 * @return true if successful
	 */
	public boolean deleteKey(HederaKey key) {
	   	logger.trace("deleteKey key {}", key);
		return this.keys.remove(key);
	}
	/**
	 * Deletes a {@link HederaKeySignature} from the list
	 * @param keySigPair the key signature pair to delete
	 * @return true if successful
	 */
	public boolean deleteKeySignaturePair(HederaKeySignature keySigPair) {
	   	logger.trace("deleteKeySignaturePair {}", keySigPair);
		return this.keySignatures.remove(keySigPair);
	}
	/**
	 * Gets the list of {@link HederaKey}
	 * @return List {@link HederaKey}
	 */
	public List<HederaKey> getKeys() {
	   	logger.trace("getKeys");
		return this.keys;
	}
	/**
	 * Gets the list of {@link HederaKeySignature}
	 * @return List {@link HederaKeySignature}
	 */
	public List<HederaKeySignature> getKeySignatures() {
	   	logger.trace("getKeySignatures");
		return this.keySignatures;
	}
}