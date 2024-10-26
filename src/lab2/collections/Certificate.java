package lab2.collections;

import java.util.List;

public class Certificate {
    String owner;
    List<Byte> publicKey;
    int keySize;
    String domain;

    public Certificate(String owner, List<Byte> publicKey, int keySize, String domain) {
        this.owner = owner;
        //this.publicKey = publicKey; // shallow copy
        this.publicKey = List.copyOf(publicKey); // deep copy
        this.keySize = keySize;
        this.domain = domain;
    }

    void print() {
        System.out.printf("Certificate for %s\n", owner);
        System.out.printf("Key size: %d\n", keySize);
        System.out.printf("Domain: %s\n", domain);
        for (Byte b : publicKey) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }

    @Override
    public int hashCode() {
        return this.domain.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Certificate cert)) {
            return false;
        }
        return owner.equals(cert.owner) && publicKey.equals(cert.publicKey) && keySize == cert.keySize
                && domain.equals(cert.domain);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Certificate(owner, publicKey, keySize, domain);
    }
}
