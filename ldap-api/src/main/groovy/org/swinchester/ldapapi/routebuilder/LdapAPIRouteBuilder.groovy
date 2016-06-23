package org.swinchester.ldapapi.routebuilder

import org.apache.camel.Predicate
import org.apache.camel.builder.PredicateBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.validation.PredicateValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.swinchester.ldapapi.processor.SearchProcessor

/**
 * Created by swinchester on 23/06/16.
 */
class LdapAPIRouteBuilder extends RouteBuilder {

    @Override
    void configure() throws Exception {



        rest("/ldap")
                .get("/ping").produces("application/json").to("direct:ping")
                .get("/search").produces("application/json").to("direct:search")
                .post("/add").consumes("application/json").to("direct:add")
                .post("/delete").consumes("application/json").to("direct:delete")

        Predicate hasFilter = header("filter").isNotNull();

        onException(PredicateValidationException.class)
            .handled(true)
            .setBody(constant([error:'sorry - invalid request or params']))

        from("direct:search").validate(hasFilter).processRef("searchProcessor")

        from("direct:delete").processRef("deleteProcessor")

        from("direct:add").processRef("addProcessor")

        from("direct:ping").setBody(constant([ping:'hello']))
    }
}
