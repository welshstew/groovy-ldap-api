package org.swinchester.ldap.api.buildconfig;

import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.openshift.api.model.BuildTriggerPolicy;
import io.fabric8.openshift.api.model.ImageChangeTrigger;
import io.fabric8.openshift.api.model.TemplateBuilder;

import java.util.HashMap;
import java.util.Map;

public class BuildConfigKubernetesModelProcessor {



    public void on(TemplateBuilder builder) {
        builder.addNewBuildConfigObject()
                .withNewMetadata()
                    .withName(ConfigParameters.APP_NAME)
                    .withLabels(getLabels())
                .endMetadata()
                .withNewSpec()
                    .withTriggers(getTriggers())
                    .withNewSource()
                        .withNewGit()
                            .withUri("https://github.com/welshstew/groovy-ldap-api")
                        .endGit()
                        .withContextDir("ldap-api")
                        .withType("Git")
                    .endSource()
                    .withNewStrategy()
                        .withNewSourceStrategy()
                            .withNewFrom()
                                .withKind("ImageStreamTag")
                                .withName("fis-java-openshift:1.0")
                                .withNamespace("openshift")
                            .endFrom()
                        .endSourceStrategy()
                        .withType("Source")
                    .endStrategy()
                    .withNewOutput()
                        .withNewTo()
                            .withKind("ImageStreamTag")
                            .withName(ConfigParameters.APP_NAME + ":${IS_TAG}")
                        .endTo()
                    .endOutput()
                .endSpec()
            .endBuildConfigObject()
            .build();
    }

    private BuildTriggerPolicy getTriggers() {
        ObjectReference from = new ObjectReference();
        from.setName("fis-java-openshift:1.0");
        from.setKind("ImageStreamTag");
        from.setNamespace("openshift");

        ImageChangeTrigger imageChangeTrigger = new ImageChangeTrigger();
        imageChangeTrigger.setFrom(from);

        BuildTriggerPolicy policy = new BuildTriggerPolicy();
        policy.setType("ImageChange");
        policy.setImageChange(imageChangeTrigger);

        return policy;
    }

    private Map<String, String> getLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("app", ConfigParameters.APP_NAME);
        labels.put("project", ConfigParameters.APP_NAME);
        labels.put("version", "1.0.0-SNAPSHOT");
        labels.put("group", ConfigParameters.GROUP_NAME);

        return labels;
    }

}
