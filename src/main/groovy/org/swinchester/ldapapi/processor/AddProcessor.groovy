package org.swinchester.ldapapi.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.directory.groovyldap.LDAP
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**

 {
 "add": [
 {
 "ou": "bye",
 "dn": "ou=bye,dc=example,dc=com",
 "objectclass": [
 "top",
 "organizationalUnit"
 ]
 }
 ]
 }

 * Created by swinchester on 23/06/16.
 */
@Component
class AddProcessor implements Processor{

    @Value('#{systemEnvironment.LDAP_URL}')
    String ldapUrl

    @Value('#{systemEnvironment.LDAP_BIND_DN}')
    String bindDN

    @Value('#{systemEnvironment.LDAP_BIND_PASSWORD}')
    String bindPassword

    @Value('#{systemEnvironment.LDAP_SEARCH_BASE}')
    String defaultSearchBase


    Logger log = LoggerFactory.getLogger(AddProcessor.class)

    @Override
    void process(Exchange exchange) throws Exception {

        def addMapObject = exchange.in.body as Map
        def ldap = LDAP.newInstance(ldapUrl, bindDN, bindPassword)

        def addMapList = addMapObject.add

        addMapList.each { entry ->

            log.info("adding entry: ${entry}")
            String dn = entry.dn
            entry.remove('dn')

            try{
                ldap.add(dn, entry)
                log.info("success adding ${dn}")
            }catch(Exception ex){
                exchange.in.headers.put(Exchange.HTTP_RESPONSE_CODE, 409)
                exchange.in.body = [error:'unable to add these objects']
                log.error("unable to add ${dn}")
                throw ex;
            }

        }

        exchange.in.headers.put(Exchange.HTTP_RESPONSE_CODE, 201)
        exchange.in.body = [ldap:'thanks for those objects!']

    }
}
