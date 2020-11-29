package org.jumpmind.pos.util.security;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;

public class TrustSSLSocketFactory extends SocketFactory {
    private static final AtomicReference<TrustSSLSocketFactory> defaultFactory = new AtomicReference<>();

    private SSLSocketFactory sf;

    public TrustSSLSocketFactory() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new SelfSignedX509TrustManager(null)},
                    new SecureRandom());
            sf = context.getSocketFactory();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SocketFactory getDefault() {
        final TrustSSLSocketFactory value = defaultFactory.get();
        if (value == null) {
            defaultFactory.compareAndSet(null, new TrustSSLSocketFactory());
            return defaultFactory.get();
        }
        return value;
    }

    @Override
    public Socket createSocket() throws IOException {
        return sf.createSocket();
    }

    @Override
    public Socket createSocket(final String s, final int i) throws IOException {
        return sf.createSocket(s, i);
    }

    @Override
    public Socket createSocket(final String s, final int i, final InetAddress inetAddress, final int i1) throws IOException {
        return sf.createSocket(s, i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(final InetAddress inetAddress, final int i) throws IOException {
        return sf.createSocket(inetAddress, i);
    }

    @Override
    public Socket createSocket(final InetAddress inetAddress, final int i, final InetAddress inetAddress1, final int i1) throws IOException {
        return sf.createSocket(inetAddress, i, inetAddress1, i1);
    }
}