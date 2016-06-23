package org.swinchester.ldapapi.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.directory.groovyldap.LDAP
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by swinchester on 23/06/16.
 */
@Component
class DeleteProcessor implements Processor{

    @Value('#{systemEnvironment.LDAP_URL}')
    String ldapUrl

    @Value('#{systemEnvironment.LDAP_BIND_DN}')
    String bindDN

    @Value('#{systemEnvironment.LDAP_BIND_PASSWORD}')
    String bindPassword

    @Value('#{systemEnvironment.LDAP_SEARCH_BASE}')
    String defaultSearchBase


    Logger log = LoggerFactory.getLogger(DeleteProcessor.class)

    @Override
    void process(Exchange exchange) throws Exception {

        def addMapObject = exchange.in.body as Map
        def ldap = LDAP.newInstance(ldapUrl, bindDN, bindPassword)

        def deleteList = addMapObject.delete

        deleteList.each { dn ->

            log.info("deleting entry: ${dn}")

            try{
                ldap.delete(dn)
                log.info("success deleting ${dn}")
            }catch(Exception ex){
                log.error("unable to delete ${dn}")
                exchange.in.headers.put(Exchange.HTTP_RESPONSE_CODE, 409)
                exchange.in.body = [error:'unable to delete these objects']
                throw ex;
            }

        }
        exchange.in.headers.put(Exchange.HTTP_RESPONSE_CODE, 200)
        exchange.in.body = [ldap:'deleted those objects for you old chap, anything else I can do for you?']
    }
}