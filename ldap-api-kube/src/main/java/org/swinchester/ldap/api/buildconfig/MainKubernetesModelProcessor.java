package org.swinchester.ldap.api.buildconfig;

import io.fabric8.kubernetes.generator.annotation.KubernetesModelProcessor;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.ImageStreamBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.TemplateBuilder;

@KubernetesModelProcessor
public class MainKubernetesModelProcessor {

    public void withDeploymentConfigBuilderTemplate(DeploymentConfigBuilder builder) {
        new DeploymentConfigKubernetesModelProcessor().on(builder);
    }

    public void withImageStreamBuilderTemplate(ImageStreamBuilder builder) {
        new ImageStreamKubernetesModelProcessor().on(builder);
    }

//    public void withBuildConfigBuilderTemplate(BuildConfigBuilder builder){
//
//        new BuildConfigKubernetesModelProcessor().on(builder);
//
//    }

    public void withTemplateBuilder(TemplateBuilder builder) {

        new BuildConfigKubernetesModelProcessor().on(builder);
        new RouteConfigKubernetesModelProcessor().on(builder);

    }
}
