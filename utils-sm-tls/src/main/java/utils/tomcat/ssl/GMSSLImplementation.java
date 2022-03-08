package utils.tomcat.ssl;

import javax.net.ssl.SSLSession;

import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;

public class GMSSLImplementation extends SSLImplementation {

    public GMSSLImplementation() {}

    @Override
    public SSLSupport getSSLSupport(SSLSession session) 
    {
        return new GMSupport(session);
    }

    @Override
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) 
    {
        return new GMUtil(certificate);
    }

    @Override
    public boolean isAlpnSupported() 
    {
        return false;
    }
}

