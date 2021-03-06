/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.servers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.ovirt.engine.api.resource.externalhostproviders.ExternalComputeResourcesResource;
import org.ovirt.engine.api.v3.V3Server;
import org.ovirt.engine.api.v3.types.V3ExternalComputeResources;

@Produces({"application/xml", "application/json"})
public class V3ExternalComputeResourcesServer extends V3Server<ExternalComputeResourcesResource> {
    public V3ExternalComputeResourcesServer(ExternalComputeResourcesResource delegate) {
        super(delegate);
    }

    @GET
    public V3ExternalComputeResources list() {
        return adaptList(getDelegate()::list);
    }

    @Path("{id}")
    public V3ExternalComputeResourceServer getResourceResource(@PathParam("id") String id) {
        return new V3ExternalComputeResourceServer(getDelegate().getResourceResource(id));
    }
}
