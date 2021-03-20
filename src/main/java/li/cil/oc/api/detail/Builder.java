package li.cil.oc.api.detail;

import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;

public interface Builder<T extends Node> {
    T create();

    public interface ComponentConnectorBuilder extends Builder<ComponentConnector> {
    }

    public interface ConnectorBuilder extends Builder<Connector> {
        Builder.ComponentConnectorBuilder withComponent(String var1, Visibility var2);

        Builder.ComponentConnectorBuilder withComponent(String var1);
    }

    public interface ComponentBuilder extends Builder<Component> {
        Builder.ComponentConnectorBuilder withConnector(double var1);

        Builder.ComponentConnectorBuilder withConnector();
    }

    public interface NodeBuilder extends Builder<Node> {
        Builder.ComponentBuilder withComponent(String var1, Visibility var2);

        Builder.ComponentBuilder withComponent(String var1);

        Builder.ConnectorBuilder withConnector(double var1);

        Builder.ConnectorBuilder withConnector();
    }
}