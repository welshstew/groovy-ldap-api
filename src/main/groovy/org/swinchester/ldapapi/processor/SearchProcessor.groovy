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
class SearchProcessor implements Processor {

    @Value('#{systemEnvironment.LDAP_URL}')
    String ldapUrl

    @Value('#{systemEnvironment.LDAP_BIND_DN}')
    String bindDN

    @Value('#{systemEnvironment.LDAP_BIND_PASSWORD}')
    String bindPassword

    @Value('#{systemEnvironment.LDAP_SEARCH_BASE}')
    String defaultSearchBase

    Logger log = LoggerFactory.getLogger(SearchProcessor.class)

    @Override
    void process(Exchange exchange) throws Exception {

        String searchFilter = exchange.in.headers['filter']
        String searchBase = exchange.in.headers['base'] ?: defaultSearchBase

        log.info("Executing search with filter: ${searchFilter} from ${searchBase}")
        log.debug("Executing against LDAP: ${ldapUrl} with ${bindDN} and ${bindPassword}")

        def ldap = LDAP.newInstance(ldapUrl, bindDN, bindPassword)

        def entryList = []

        ldap.eachEntry(filter:searchFilter, base:searchBase){ entry ->
            entryList.add(entry)
        }

        exchange.in.body = entryList
    }
}
