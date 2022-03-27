package com.example.client.packet;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.BrokenJCEBlockCipher;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import android.util.Base64;

public class KeyManagerDH {
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    static KeyPair pair;

    public static KeyPair getPair(){
        return pair;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4 )+Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte [] savePublicKey (PublicKey key) throws Exception
    {
        //return key.getEncoded();

        ECPublicKey eckey = (ECPublicKey)key;
        return eckey.getQ().getEncoded(true);
    }

    public static PublicKey loadPublicKey (byte [] data) throws Exception
    {
		/*KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
		return kf.generatePublic(new X509EncodedKeySpec(data));*/
        Provider BCprovider = new BouncyCastleProvider();
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime192v1");
        ECPublicKeySpec pubKey = new ECPublicKeySpec(
                params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", BCprovider);
        return kf.generatePublic(pubKey);
    }

    public static byte [] savePrivateKey (PrivateKey key) throws Exception
    {
        //return key.getEncoded();

        ECPrivateKey eckey = (ECPrivateKey)key;
        return eckey.getD().toByteArray();
    }

    public static PrivateKey loadPrivateKey (byte [] data) throws Exception
    {
        //KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        //return kf.generatePrivate(new PKCS8EncodedKeySpec(data));
        Provider BCprovider = new BouncyCastleProvider();
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime192v1");
        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", BCprovider);
        return kf.generatePrivate(prvkey);
    }

    public static String doECDH (byte[] dataPrv, byte[] dataPub) throws Exception
    {
        Provider BCprovider = new BouncyCastleProvider();
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", BCprovider);
        ka.init(loadPrivateKey(dataPrv));
        ka.doPhase(loadPublicKey(dataPub), true);
        byte [] secret = ka.generateSecret();
        return bytesToHex(secret);
    }
    public static void generateKeyPair() {
        Provider BCprovider = new BouncyCastleProvider();
        Security.addProvider(BCprovider);

        KeyPairGenerator kpgen = null;
        try {
            kpgen = KeyPairGenerator.getInstance("ECDH", BCprovider);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            kpgen.initialize(new ECGenParameterSpec("prime192v1"), new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        KeyPair pairCur = kpgen.generateKeyPair();
        pair = pairCur;
    }
    public static String encryptGost(String data, Key sessionKey){
        Provider bc = new BouncyCastleProvider();
        Security.addProvider(bc);
        byte[] initByte = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Cipher encrypt = null;
        try {
            encrypt = Cipher.getInstance("GOST3412-2015/CBC/PKCS5PADDING", bc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            encrypt.init(Cipher.ENCRYPT_MODE, sessionKey, new IvParameterSpec(initByte));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] enc = new byte[0];
        try {
            enc = encrypt.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return bytesToHex(enc);
    }
    public static String decryptGost(String data, Key sessionKey){
        Provider bc = new BouncyCastleProvider();
        Security.addProvider(bc);

        byte[] enc = hexToByteArray(data);

        byte[] initByte = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Cipher encrypt = null;
        try {
            encrypt = Cipher.getInstance("GOST3412-2015/CBC/PKCS5PADDING", bc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            encrypt.init(Cipher.DECRYPT_MODE, sessionKey, new IvParameterSpec(initByte));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] dec = new byte[0];
        try {
            dec = encrypt.doFinal(enc);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(dec);
    }

    public static Key StringToKey(String keyStr){
        Provider BCprovider = new BouncyCastleProvider();
        Security.addProvider(BCprovider);

        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("GOST3412-2015", BCprovider);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] kk = Base64.decode(keyStr, Base64.DEFAULT);
        SecretKey key = new SecretKeySpec(kk, 0, kg.generateKey().getEncoded().length, "GOST3412-2015");
        return key;
    }
    public static String KeyToString(Key key){
        String encodedKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        return encodedKey;
    }
}
