import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.util.Base64;

public class EncryptDecrypt {
    // defines some class variables
    protected String encryptAlgo, transformation;
    protected int keyLength;
    protected PublicKey pubKey;
    protected PrivateKey privKey;
    protected static String encryptionAlgorithm = "RSA";
    protected static int encryptionKeyLen = 2048;
    protected static String transformationS = "RSA";


    public EncryptDecrypt() throws NoSuchAlgorithmException {
        // creates instance of the class
        encryptAlgo = EncryptDecrypt.encryptionAlgorithm;
        keyLength = EncryptDecrypt.encryptionKeyLen;
        transformation = EncryptDecrypt.transformationS; }
        public static BigInteger keyToNum(byte[] byteArray){ return new BigInteger(1, byteArray); }
        public String getEncryptAlgo(){ return encryptAlgo; }
        public int getKeyLength(){ return keyLength; }
        public String getTransformation(){ return transformation; }
        public PublicKey getPubKey(){ return pubKey; }
        public byte[] getPubKeyAsByteArray(){
            return pubKey.getEncoded();
        }
        public String getEncodedPubKey(){
            return Base64.getEncoder().encodeToString(getPubKeyAsByteArray());
        }
        public PrivateKey getPrivKey(){
            return privKey;
        }
        public byte[] getPrivKeyAsByteArray(){
            return privKey.getEncoded();
        }
        public String getEncodedPrivKey(){
            return Base64.getEncoder().encodeToString(getPrivKeyAsByteArray());
        }

    public byte[] encryptText(String plaintext,PublicKey pubKey){
        // takes 2 parameters, plaintext and a public key, with this a cipher is created and used to encrypt the
        // plaintext using the public key to create cipher text which cannot be read
        byte[] ciphertext = null;
        try{
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.PUBLIC_KEY, pubKey);
            ciphertext = cipher.doFinal(plaintext.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }return ciphertext;
    }

    public byte[] decryptText(byte[] ciphertext,PrivateKey privKey){
        // takes 2 parameters, ciphertext and a private key, with this a cipher is created and used to decrypt the
        // ciphertext using the private key to create plain text which can be read
        byte[] plaintext = null;
        try{
            Cipher cipher =Cipher.getInstance(transformation);
            cipher.init(Cipher.PRIVATE_KEY, privKey);
            plaintext = cipher.doFinal(ciphertext);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }return plaintext;
    }
    public KeyPair getKeyPair() throws NoSuchAlgorithmException {
        // simply generates a key pair and returns it
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(encryptAlgo);
        kpg.initialize(keyLength);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }
}

