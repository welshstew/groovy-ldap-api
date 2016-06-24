package org.swinchester.ldap.api.buildconfig;

import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.TemplateBuilder;

/**
 * Created by swinchester on 24/06/16.
 */
public class RouteConfigKubernetesModelProcessor {

    public void on(TemplateBuilder builder) {
        builder.addNewRouteObject()
                .withNewMetadata()
                    .withName("groovy-ldap-api")
                .endMetadata()
                .withNewSpec()
                .withHost("${ROUTE_HOST_NAME}")
                .withNewTo()
                .withKind("Service")
                .withName("ldap-api-service")
                .endTo()
                .endSpec()
                .endRouteObject()
                .build();
    }
}
