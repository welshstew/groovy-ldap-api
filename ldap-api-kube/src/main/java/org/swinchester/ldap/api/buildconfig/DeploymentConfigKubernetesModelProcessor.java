package org.swinchester.ldap.api.buildconfig;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentTriggerImageChangeParams;
import io.fabric8.openshift.api.model.DeploymentTriggerPolicy;
import io.fabric8.utils.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeploymentConfigKubernetesModelProcessor {



    public void on(DeploymentConfigBuilder builder) {
        builder.withSpec(builder.getSpec())
                .withNewMetadata()
                    .withName(ConfigParameters.APP_NAME + "-dc")
                    .withLabels(getSelectors())
                .endMetadata()
                .editSpec()
                    .withReplicas(1)
                    .withSelector(getSelectors())
                    .withNewStrategy()
                        .withType("Recreate")
                    .endStrategy()
                    .editTemplate()
                        .editSpec()
                            .withContainers(getContainers())
                            .withRestartPolicy("Always")
                        .endSpec()
                    .endTemplate()
                    .withTriggers(getTriggers())
                .endSpec()
            .build();
    }


    private List<DeploymentTriggerPolicy> getTriggers() {
        DeploymentTriggerPolicy configChange = new DeploymentTriggerPolicy();
        configChange.setType("ConfigChange");

        ObjectReference from = new ObjectReference();
        from.setName(ConfigParameters.APP_NAME + ":${IS_TAG}");
        from.setKind("ImageStreamTag");
        from.setNamespace("${IS_PULL_NAMESPACE}");

        DeploymentTriggerImageChangeParams imageChangeParms = new DeploymentTriggerImageChangeParams();
        imageChangeParms.setFrom(from);
        imageChangeParms.setAutomatic(true);

        DeploymentTriggerPolicy imageChange = new DeploymentTriggerPolicy();
        imageChange.setType("ImageChange");
        imageChange.setImageChangeParams(imageChangeParms);
        imageChangeParms.setContainerNames(Lists.newArrayList(ConfigParameters.APP_NAME));

        List<DeploymentTriggerPolicy> triggers = new ArrayList<DeploymentTriggerPolicy>();
        triggers.add(configChange);
        triggers.add(imageChange);

        return triggers;
    }

    private List<ContainerPort> getPorts() {
        List<ContainerPort> ports = new ArrayList<ContainerPort>();

        ContainerPort http = new ContainerPort();
        http.setContainerPort(9090);
        http.setProtocol("TCP");
        http.setName("http");

        ContainerPort jolokia = new ContainerPort();
        jolokia.setContainerPort(8787);
        jolokia.setProtocol("TCP");
        jolokia.setName("jolokia");

        ports.add(http);
        ports.add(jolokia);

        return ports;
    }

    private List<EnvVar> getEnvironmentVariables() {
        List<EnvVar> envVars = new ArrayList<EnvVar>();

        EnvVarSource namespaceSource = new EnvVarSource();
        namespaceSource.setFieldRef(new ObjectFieldSelector(null, "metadata.namespace"));

        EnvVar namespace = new EnvVar();
        namespace.setName("KUBERNETES_NAMESPACE");
        namespace.setValueFrom(namespaceSource);

        EnvVar ldapUrl = new EnvVar();
        ldapUrl.setName("LDAP_URL");
        ldapUrl.setValue("${LDAP_URL}");

        EnvVar ldapBindDn = new EnvVar();
        ldapBindDn.setName("LDAP_BIND_DN");
        ldapBindDn.setValue("${LDAP_BIND_DN}");

        EnvVar ldapPassword = new EnvVar();
        ldapPassword.setName("LDAP_BIND_PASSWORD");
        ldapPassword.setValue("${LDAP_BIND_PASSWORD}");

        EnvVar ldapSearchBase = new EnvVar();
        ldapSearchBase.setName("LDAP_SEARCH_BASE");
        ldapSearchBase.setValue("${LDAP_SEARCH_BASE}");

        envVars.add(namespace);
        envVars.add(ldapUrl);
        envVars.add(ldapBindDn);
        envVars.add(ldapPassword);
        envVars.add(ldapSearchBase);

        return envVars;
    }

    private Container getContainers() {
        Container container = new Container();
        container.setImage("${IS_PULL_NAMESPACE}/"+ ConfigParameters.APP_NAME +  ":${IS_TAG}");
        container.setImagePullPolicy("Always");
        container.setName(ConfigParameters.APP_NAME);
        container.setPorts(getPorts());
        container.setEnv(getEnvironmentVariables());
        container.setLivenessProbe(getProbe(new Integer(30), new Integer(60)));
        container.setReadinessProbe(getProbe(new Integer(30), new Integer(1)));
        container.setResources(getResourceRequirements());
        return container;
    }


    private Map<String, String> getSelectors() {
        Map<String, String> selectors = new HashMap<>();
        selectors.put("project", "ldap-api");
        selectors.put("provider", "swinchester");
        selectors.put("version", "1.0-SNAPSHOT");
        selectors.put("group", "ldap");

//        project: "ldap-api"
//        provider: "swinchester"
//        version: "1.0-SNAPSHOT"
//        group: "ldap"
        return selectors;
    }

    private Probe getProbe(Integer initialDelaySeconds, Integer timeoutSeconds) {
        TCPSocketAction jettyAction = new TCPSocketAction();
        jettyAction.setPort(new IntOrString(9090));

        Probe probe = new Probe();
        probe.setInitialDelaySeconds(initialDelaySeconds);
        probe.setTimeoutSeconds(timeoutSeconds);
        probe.setTcpSocket(jettyAction);

        return probe;
    }

    private ResourceRequirements getResourceRequirements() {
        ResourceRequirements resourceRequirements = new ResourceRequirements();
        resourceRequirements.setRequests(getRequests());
        resourceRequirements.setLimits(getLimits());

        return resourceRequirements;
    }

    private Map<String, Quantity> getRequests() {
        Map<String, Quantity> limits = new HashMap<String, Quantity>();
        limits.put("cpu", new Quantity("200m"));
        limits.put("memory", new Quantity("512Mi"));

        return limits;
    }

    private Map<String, Quantity> getLimits() {
        Map<String, Quantity> limits = new HashMap<String, Quantity>();
        limits.put("cpu", new Quantity("400m"));
        limits.put("memory", new Quantity("1024Mi"));

        return limits;
    }
}
