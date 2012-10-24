/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import java.net.MalformedURLException;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

/**
 *
 * @author urso
 */
public class CouchConnector {
    private final HttpClient httpClient;
    private final CouchDbInstance dbInstance;
    
    public CouchConnector(String couchURL) throws MalformedURLException {
        httpClient = new StdHttpClient.Builder().url(couchURL).build();
        dbInstance = new StdCouchDbInstance(httpClient);
    }

    public CouchDbInstance getDbInstance() {
        return dbInstance;
    }
}
